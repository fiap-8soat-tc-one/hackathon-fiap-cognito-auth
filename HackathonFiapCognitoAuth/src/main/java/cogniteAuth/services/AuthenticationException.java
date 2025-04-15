package cogniteAuth.services;

public class AuthenticationException extends Exception {
    private final int statusCode;

    public AuthenticationException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}