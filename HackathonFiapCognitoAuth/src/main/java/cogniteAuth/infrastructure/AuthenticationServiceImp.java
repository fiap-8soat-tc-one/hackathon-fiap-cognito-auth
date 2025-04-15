package cogniteAuth.infrastructure;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import cogniteAuth.domain.Login;
import cogniteAuth.services.AuthenticationException;
import cogniteAuth.services.AuthenticationService;

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
    public Map<String, String> authenticate(Login login) throws AuthenticationException {
        try {
            InitiateAuthResult authResult = cognitoClient.initiateAuth(createAuthRequest(login));
            return createAuthResponse(authResult);
        } catch (NotAuthorizedException | InvalidParameterException e) {
            throw new AuthenticationException("Authentication failed: " + e.getMessage(), 401);
        } catch (Exception e) {
            throw new AuthenticationException("Internal authentication error", 500);
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