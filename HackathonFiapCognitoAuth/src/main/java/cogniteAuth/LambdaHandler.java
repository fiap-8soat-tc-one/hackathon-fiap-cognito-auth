package cogniteAuth;

import cogniteAuth.domain.Login;
import cogniteAuth.infrastructure.AuthenticationServiceImp;
import cogniteAuth.models.ApiRequestModel;
import cogniteAuth.models.ApiResponseModel;
import cogniteAuth.services.AuthenticationException;
import cogniteAuth.services.AuthenticationService;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
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

        LambdaLogger logger = context.getLogger();
        try {
            Optional<Login> login = ApiRequestModel.parseLoginRequest(event,logger );

            if (!login.isPresent()) {
                return ApiResponseModel.createResponse(400, "Requisição inválida ou campos obrigatórios não preenchidos corretamente!");
            }

            return ApiResponseModel.createResponse(200, authService.authenticate(login.get()));

        } catch (AuthenticationException e) {
            return ApiResponseModel.createResponse(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            logger.log("Erro interno ao processar requisição: " + e.getMessage());
            return ApiResponseModel.createResponse(500, "Erro interno do servidor!");
        }
    }
}