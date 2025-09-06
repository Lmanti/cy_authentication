package co.com.crediya.cy_authentication.api.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import co.com.crediya.cy_authentication.model.security.TokenData;
import co.com.crediya.cy_authentication.model.security.gateways.TokenGenerator;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class JwtTokenGenerator implements TokenGenerator {

    private final Key key;
    private final JwtParser parser;

    public JwtTokenGenerator(@Value("${security.jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.parser = Jwts.parserBuilder().setSigningKey(key).build();
    }

    @Override
    public String generate(String subject, Collection<String> roles, Duration ttl) {
        Instant now = Instant.now();
        return Jwts.builder()
            .setSubject(subject)
            .claim("roles", roles)
            .setIssuedAt(Date.from(now))
            .setExpiration(Date.from(now.plus(ttl)))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<TokenData> verify(String token) {
        try {
            Claims claims = parser.parseClaimsJws(token).getBody();
            String subject = claims.getSubject();
            List<String> roles = claims.get("roles", List.class);
            return Optional.of(new TokenData(subject, roles));
        } catch (JwtException e) {
            return Optional.empty();
        }
    }
}
