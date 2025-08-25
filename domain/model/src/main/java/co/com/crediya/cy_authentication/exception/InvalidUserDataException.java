package co.com.crediya.cy_authentication.exception;

public class InvalidUserDataException extends RuntimeException {
    public InvalidUserDataException() {
        super("Invalid user data");
    }
    
    public InvalidUserDataException(String message) {
        super(message);
    }
    
    public InvalidUserDataException(String message, Throwable cause) {
        super(message, cause);
    }
}