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

import java.util.HashMap;
import java.util.Map;

public class LambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final AWSCognitoIdentityProvider cognitoClient = AWSCognitoIdentityProviderClientBuilder.defaultClient();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        try {
            // Parse the request body
            Map<String, String> body = parseJsonBody(event.getBody());
            String email = body.get("email");
            String senha = body.get("senha");

            // Validate email and senha
            if (email == null || senha == null) {
                response.setStatusCode(400);
                response.setBody("{\"message\": \"Email e senha são obrigatórios.\"}");
                return response;
            }

            // Prepare parameters for the Cognito request
            Map<String, String> authParams = new HashMap<>();
            authParams.put("USERNAME", email); // Cognito configurado para validar por e-mail
            authParams.put("PASSWORD", senha);

            InitiateAuthRequest authRequest = new InitiateAuthRequest()
                    .withAuthFlow(AuthFlowType.USER_PASSWORD_AUTH)
                    .withClientId("aiikv53cphpca7r6plv0vilec")
                    .withAuthParameters(authParams);

            // Call Cognito to authenticate
            InitiateAuthResult authResult = cognitoClient.initiateAuth(authRequest);

            // Prepare the response
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("access_token", authResult.getAuthenticationResult().getAccessToken());
            responseBody.put("id_token", authResult.getAuthenticationResult().getIdToken());
            responseBody.put("refresh_token", authResult.getAuthenticationResult().getRefreshToken());
            responseBody.put("expires_in", String.valueOf(authResult.getAuthenticationResult().getExpiresIn()));

            response.setStatusCode(200);
            response.setBody(responseBody.toString());
        } catch (InvalidParameterException | NotAuthorizedException e) {
            response.setStatusCode(401);
            response.setBody("{\"message\": \"Falha na autenticação\", \"error\": \"" + e.getMessage() + "\"}");
        } catch (InternalErrorException e) {
            response.setStatusCode(500);
            response.setBody("{\"message\": \"Erro interno no servidor\", \"error\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setBody("{\"message\": \"Erro na autenticação\", \"error\": \"" + e.getMessage() + "\"}");
        }
        return response;
    }

    private Map<String, String> parseJsonBody(String body) {
        Map<String, String> map = new HashMap<>();
        String[] keyValuePairs = body.replaceAll("[{}\"]", "").split(",");
        for (String pair : keyValuePairs) {
            String[] keyValue = pair.split(":");
            map.put(keyValue[0].trim(), keyValue[1].trim());
        }
        return map;
    }
}