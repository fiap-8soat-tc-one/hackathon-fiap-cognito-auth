package cogniteAuth;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AuthFlowType;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.NotAuthorizedException;
import com.amazonaws.services.cognitoidp.model.InvalidParameterException;
import com.amazonaws.services.cognitoidp.model.InternalErrorException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class LambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final AWSCognitoIdentityProvider cognitoClient = AWSCognitoIdentityProviderClientBuilder.defaultClient();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        try {
            Login login = new ObjectMapper().readValue(event.getBody(), Login.class);

            if (login.getEmail() == null || login.getPassword() == null) {
                response.setStatusCode(400);
                response.setBody("{\"message\": \"Email e senha são obrigatórios.\"}");
                return response;
            }

            InitiateAuthRequest authRequest = new InitiateAuthRequest()
                    .withAuthFlow(AuthFlowType.USER_PASSWORD_AUTH)
                    .withClientId(System.getenv("COGNITO_CLIENT_ID"))
                    .withAuthParameters(getAuthParams(login));

            InitiateAuthResult authResult = cognitoClient.initiateAuth(authRequest);

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

}