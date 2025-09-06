package co.com.crediya.cy_authentication.model.security.gateways;

import java.time.Duration;
import java.util.Collection;
import java.util.Optional;

import co.com.crediya.cy_authentication.model.security.TokenData;

public interface TokenGenerator {
    String generate(String subject, Collection<String> roles, Duration ttl);
    Optional<TokenData> verify(String token);
}
