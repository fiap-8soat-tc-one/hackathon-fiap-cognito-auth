package cogniteAuth.models;

import cogniteAuth.domain.Login;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ApiRequestModel {
    public static Optional<Login> parseLoginRequest(APIGatewayProxyRequestEvent event, LambdaLogger logger) {
        try {
            String body = event.getBody();

            logger.log("Processando a requisição: " + body);

            Map<String, String> bodyMap = parseJsonBody(body);
            String email = bodyMap.get("email");
            String password = bodyMap.get("password");

            Login login = new Login(email, password);

            if (login.isValid()) {
                return Optional.of(login);
            }
        } catch (Exception e) {
            logger.log("Erro na conversão do corpo da requisição: " + e.getMessage());
        }
        return Optional.empty();
    }

    private static Map<String, String> parseJsonBody(String body) {
        Map<String, String> map = new HashMap<>();
        String[] keyValuePairs = body.replaceAll("[{}\"]", "").split(",");
        for (String pair : keyValuePairs) {
            String[] keyValue = pair.split(":");
            map.put(keyValue[0].trim(), keyValue[1].trim());
        }
        return map;
    }
}