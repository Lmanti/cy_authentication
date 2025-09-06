package co.com.crediya.cy_authentication.model.security.gateways;

public interface PasswordHasher {
    String hash(String raw);
    boolean matches(String raw, String hashed);
}
