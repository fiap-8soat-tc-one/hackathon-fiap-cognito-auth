package cogniteAuth;

import cogniteAuth.domain.Login;
import cogniteAuth.infrastructure.AuthenticationServiceImp;
import cogniteAuth.models.ApiRequestModel;
import cogniteAuth.models.ApiResponseModel;
import cogniteAuth.services.AuthenticationException;
import cogniteAuth.services.AuthenticationService;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.util.Optional;

public class LambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final AuthenticationService authService;

    public LambdaHandler() {
        this.authService = new AuthenticationServiceImp(
                AWSCognitoIdentityProviderClientBuilder.defaultClient(),
                System.getenv("COGNITO_CLIENT_ID")
        );
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {
        try {
            Optional<Login> login = ApiRequestModel.parseLoginRequest(event, context.getLogger());

            if (!login.isPresent()) {
                return ApiResponseModel.createResponse(400, "Invalid request body or missing required fields");
            }

            return ApiResponseModel.createResponse(200, authService.authenticate(login.get()));

        } catch (AuthenticationException e) {
            return ApiResponseModel.createResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            context.getLogger().log("Error processing request: " + e.getMessage());
            return ApiResponseModel.createResponse(500, "Internal server error");
        }
    }
}