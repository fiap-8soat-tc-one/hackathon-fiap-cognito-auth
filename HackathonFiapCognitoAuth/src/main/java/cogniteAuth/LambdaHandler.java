package cogniteAuth;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AuthFlowType;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.NotAuthorizedException;
import com.amazonaws.services.cognitoidp.model.InvalidParameterException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class LambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final AWSCognitoIdentityProvider cognitoClient = AWSCognitoIdentityProviderClientBuilder.defaultClient();
    private final ObjectMapper objectMapper= new ObjectMapper();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        APIGatewayProxyResponseEvent response = getApiGatewayProxyResponseEvent();
        LambdaLogger logger = context.getLogger();

        try {
            String body = event.getBody();
            logger.log("Raw body: " + body);

            if(event.getIsBase64Encoded()) {
                body = new String(java.util.Base64.getDecoder().decode(body));
                logger.log("\nDecoded body: " + body);
            }

            if (!isValidJson(body)) {
                response.setStatusCode(400);
                response.setBody("{\"message\": \"Invalid JSON payload\"}");
                return response;
            }

            Login login = objectMapper.readValue(body, Login.class);

            logger.log("\nParsed login object - email: " + login.getEmail());

            boolean isValidEmail = login.getEmail() != null;
            boolean isValidPassword = login.getPassword() != null;

            if (!isValidEmail || !isValidPassword) {
                response.setStatusCode(400);
                response.setBody("{\"message\": \"Email e password são obrigatórios.\"}");
                return response;
            }

            InitiateAuthResult authResult = cognitoClient.initiateAuth(getAuthRequest(login));

            response.setStatusCode(200);
            response.setBody(getResponseBody(authResult).toString());

        } catch (InvalidParameterException | NotAuthorizedException e) {
            response.setStatusCode(401);
            response.setBody("{\"message\": \"Falha na autenticação\", \"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setBody("{\"message\": \"Erro na autenticação\", \"error\": \"" + e.getMessage() + "\"}");
        }
        return response;
    }

    private static InitiateAuthRequest getAuthRequest(Login login) {
        return new InitiateAuthRequest()
                .withAuthFlow(AuthFlowType.USER_PASSWORD_AUTH)
                .withClientId(System.getenv("COGNITO_CLIENT_ID"))
                .withAuthParameters(getAuthParams(login));
    }

    private static APIGatewayProxyResponseEvent getApiGatewayProxyResponseEvent() {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setHeaders(Map.of("Content-Type", "application/json"));
        return response;
    }

    private static Map<String, String> getResponseBody(InitiateAuthResult authResult) {
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("id_token", authResult.getAuthenticationResult().getIdToken());
        responseBody.put("expires_in", String.valueOf(authResult.getAuthenticationResult().getExpiresIn()));
        return responseBody;
    }

    private static Map<String, String> getAuthParams(Login login) {
        Map<String, String> authParams = new HashMap<>();
        authParams.put("USERNAME", login.getEmail());
        authParams.put("PASSWORD", login.getPassword());
        return authParams;
    }

    private boolean isValidJson(String json) {
        try {
            objectMapper.readTree(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}