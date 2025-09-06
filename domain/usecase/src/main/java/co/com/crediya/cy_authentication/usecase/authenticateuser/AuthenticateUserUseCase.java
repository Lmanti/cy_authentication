package co.com.crediya.cy_authentication.usecase.authenticateuser;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;

import co.com.crediya.cy_authentication.exception.InvalidCredentialsException;
import co.com.crediya.cy_authentication.model.role.gateways.RoleRepository;
import co.com.crediya.cy_authentication.model.security.JwtResponse;
import co.com.crediya.cy_authentication.model.security.gateways.PasswordHasher;
import co.com.crediya.cy_authentication.model.security.gateways.TokenGenerator;
import co.com.crediya.cy_authentication.model.user.gateways.UserRepository;

@RequiredArgsConstructor
public final class AuthenticateUserUseCase {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordHasher hasher;
    private final TokenGenerator tokens;

    public Mono<JwtResponse> handle(String username, String password) {
        return userRepository.getByEmail(username)
            .switchIfEmpty(Mono.error(new InvalidCredentialsException("Credenciales inválidas")))
            .flatMap(toValidate ->
                roleRepository.getRoleById(toValidate.getRoleId())
                    .flatMap(role ->
                        Mono.fromCallable(() -> hasher.matches(password, toValidate.getPassword()))
                            .subscribeOn(Schedulers.boundedElastic())
                            .flatMap(matches -> {
                                if (!matches) {
                                    return Mono.error(new InvalidCredentialsException("Credenciales inválidas"));
                                } else {
                                    String token = tokens.generate(
                                        toValidate.getIdNumber().toString(),
                                        List.of(role.getName()),
                                        Duration.ofHours(4)
                                    );
                                    
                                    return Mono.just(new JwtResponse(token));
                                }
                            })
                    )
            );
    }
}
