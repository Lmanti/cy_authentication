package co.com.crediya.cy_authentication.api.config;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import co.com.crediya.cy_authentication.model.security.gateways.TokenGenerator;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.stream.Collectors;

@Component
public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final TokenGenerator tokens;

    public JwtReactiveAuthenticationManager(TokenGenerator tokens) {
        this.tokens = tokens;
    }

    @Override
    public Mono<org.springframework.security.core.Authentication> authenticate(org.springframework.security.core.Authentication authentication) {
        var token = (String) authentication.getCredentials();
        return Mono.fromCallable(() -> tokens.verify(token))
            .subscribeOn(Schedulers.boundedElastic())
            .flatMap(opt -> opt
                .map(data -> {
                var authorities = data.getRoles().stream()
                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                    .collect(Collectors.toList());
                return new UsernamePasswordAuthenticationToken(data.getSubject(), token, authorities);
                })
                .map(Mono::just)
                .orElseGet(() -> Mono.error(new BadCredentialsException("Sesión expirada o token inválido")))
            );
    }
}
