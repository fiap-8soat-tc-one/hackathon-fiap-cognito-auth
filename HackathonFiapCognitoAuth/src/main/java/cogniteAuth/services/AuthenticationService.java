package cogniteAuth.services;

import cogniteAuth.domain.Login;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

import java.util.Map;

public interface AuthenticationService {
    Map<String, String> authenticate(Login login, LambdaLogger logger) throws AuthenticationException;
}