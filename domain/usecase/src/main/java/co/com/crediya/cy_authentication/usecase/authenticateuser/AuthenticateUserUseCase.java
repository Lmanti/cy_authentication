package co.com.crediya.cy_authentication.usecase.authenticateuser;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;

import co.com.crediya.cy_authentication.exception.InvalidCredentialsException;
import co.com.crediya.cy_authentication.model.role.gateways.RoleRepository;
import co.com.crediya.cy_authentication.model.security.JwtToken;
import co.com.crediya.cy_authentication.model.security.gateways.PasswordHasher;
import co.com.crediya.cy_authentication.model.security.gateways.TokenGenerator;
import co.com.crediya.cy_authentication.model.user.gateways.UserRepository;

@RequiredArgsConstructor
public final class AuthenticateUserUseCase {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordHasher passwordHasher;
    private final TokenGenerator tokenGenerator;

    public Mono<JwtToken> handle(String username, String password) {
        return userRepository.getByEmail(username)
            .switchIfEmpty(Mono.error(new InvalidCredentialsException("Credenciales inválidas")))
            .flatMap(toValidate ->
                roleRepository.getRoleById(toValidate.getRoleId())
                    .flatMap(role ->
                        Mono.fromCallable(() -> passwordHasher.matches(password, toValidate.getPassword()))
                            .subscribeOn(Schedulers.boundedElastic())
                            .flatMap(matches -> {
                                if (!matches) {
                                    return Mono.error(new InvalidCredentialsException("Credenciales inválidas"));
                                } else {
                                    String token = tokenGenerator.generate(
                                        toValidate.getIdNumber().toString(),
                                        List.of(role.getName()),
                                        Duration.ofHours(4)
                                    );
                                    
                                    return Mono.just(new JwtToken(token));
                                }
                            })
                    )
            );
    }
}
