package cogniteAuth.models;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

public class ApiResponseModel {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static APIGatewayProxyResponseEvent createResponse(int statusCode, String message) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(Map.of("Content-Type", "application/json"));
        
        try {
            String body = objectMapper.writeValueAsString(Map.of("message", message));
            return response.withStatusCode(statusCode).withBody(body);
        } catch (Exception e) {
            return response.withStatusCode(500).withBody("{\"message\":\"Error creating response\"}");
        }
    }

    public static APIGatewayProxyResponseEvent createResponse(int statusCode, Map<String, String> body) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(Map.of("Content-Type", "application/json"));
                
        try {
            return response.withStatusCode(statusCode).withBody(objectMapper.writeValueAsString(body));
        } catch (Exception e) {
            return response.withStatusCode(500).withBody("{\"message\":\"Error creating response\"}");
        }
    }
}