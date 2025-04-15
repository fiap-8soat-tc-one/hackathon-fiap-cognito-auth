package cogniteAuth;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Login {

    private String email;
    private String password;

    public Login() {
        this.email = null;
        this.password = null;
    }

    @JsonCreator
    public Login(
            @JsonProperty("email") String email,
            @JsonProperty("password") String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}


