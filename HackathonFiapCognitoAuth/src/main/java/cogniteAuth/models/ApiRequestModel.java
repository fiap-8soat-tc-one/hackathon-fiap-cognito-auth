package cogniteAuth.models;

import cogniteAuth.domain.Login;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Optional;

public class ApiRequestModel {
    public static Optional<Login> parseLoginRequest(APIGatewayProxyRequestEvent event, LambdaLogger logger) {
        try {
            String body = event.getBody();

            logger.log("\nProcessando a requisição: " + body);

            return Optional.of( new ObjectMapper().readValue(body, Login.class));

        } catch (Exception e) {
            logger.log("Erro na conversão do corpo da requisição: " + e.getMessage());
        }
        return Optional.empty();
    }
}