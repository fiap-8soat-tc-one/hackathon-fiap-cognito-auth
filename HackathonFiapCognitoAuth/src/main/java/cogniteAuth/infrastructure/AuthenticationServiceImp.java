package cogniteAuth.infrastructure;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import cogniteAuth.domain.Login;
import cogniteAuth.services.AuthenticationException;
import cogniteAuth.services.AuthenticationService;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

import java.util.HashMap;
import java.util.Map;

public class AuthenticationServiceImp implements AuthenticationService {
    private final AWSCognitoIdentityProvider cognitoClient;
    private final String clientId;

    public AuthenticationServiceImp(AWSCognitoIdentityProvider cognitoClient, String clientId) {
        this.cognitoClient = cognitoClient;
        this.clientId = clientId;
    }

    @Override
    public Map<String, String> authenticate(Login login, LambdaLogger  logger) throws AuthenticationException {
        try {

            logger.log("Autênticando usuário: " + login.getEmail());
            InitiateAuthResult authResult = cognitoClient.initiateAuth(createAuthRequest(login));
            logger.log("Usuário autenticado com sucesso: " + login.getEmail());
            return createAuthResponse(authResult);
        } catch (NotAuthorizedException | InvalidParameterException e) {
            throw new AuthenticationException("Autênticação falhou: " + e.getMessage(), 401);
        } catch (Exception e) {
            throw new AuthenticationException("Erro ao Autênticar!", 500);
        }
    }

    private InitiateAuthRequest createAuthRequest(Login login) {
        Map<String, String> authParams = new HashMap<>();
        authParams.put("USERNAME", login.getEmail());
        authParams.put("PASSWORD", login.getPassword());

        return new InitiateAuthRequest()
                .withAuthFlow(AuthFlowType.USER_PASSWORD_AUTH)
                .withClientId(clientId)
                .withAuthParameters(authParams);
    }

    private Map<String, String> createAuthResponse(InitiateAuthResult authResult) {
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("id_token", authResult.getAuthenticationResult().getIdToken());
        responseBody.put("expires_in", String.valueOf(authResult.getAuthenticationResult().getExpiresIn()));
        return responseBody;
    }
}