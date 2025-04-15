package cogniteAuth.services;

import cogniteAuth.domain.Login;
import java.util.Map;

public interface AuthenticationService {
    Map<String, String> authenticate(Login login) throws AuthenticationException;
}