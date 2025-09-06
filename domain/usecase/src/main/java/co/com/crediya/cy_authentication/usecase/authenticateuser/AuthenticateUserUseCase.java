package co.com.crediya.cy_authentication.usecase.authenticateuser;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;

import co.com.crediya.cy_authentication.exception.InvalidCredentialsException;
import co.com.crediya.cy_authentication.model.role.gateways.RoleRepository;
import co.com.crediya.cy_authentication.model.security.JwtResponse;
import co.com.crediya.cy_authentication.model.security.gateways.AttempLimiter;
import co.com.crediya.cy_authentication.model.security.gateways.PasswordHasher;
import co.com.crediya.cy_authentication.model.security.gateways.TokenGenerator;
import co.com.crediya.cy_authentication.model.user.gateways.UserRepository;

@RequiredArgsConstructor
public final class AuthenticateUserUseCase {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordHasher hasher;
    private final TokenGenerator tokens;
    private final AttempLimiter attempLimiter;

    public Mono<JwtResponse> handle(String username, String password, String ip) {
        return attempLimiter.assertAllowed(username, ip)
            .then(userRepository.getByEmail(username)
                .switchIfEmpty(Mono.error(new InvalidCredentialsException("Credenciales inválidas"))))
            .flatMap(user -> roleRepository.getRoleById(user.getRoleId())
                .switchIfEmpty(Mono.error(new InvalidCredentialsException("Credenciales inválidas")))
                .flatMap(role ->
                    Mono.fromCallable(() -> hasher.matches(password, user.getPassword()))
                        .subscribeOn(Schedulers.boundedElastic())
                        .flatMap(matches -> {
                            if (!matches) {
                                return Mono.error(new InvalidCredentialsException("Credenciales inválidas"));
                            } else {
                                String token = tokens.generate(
                                    user.getIdNumber().toString(),
                                    List.of(role.getName()),
                                    Duration.ofHours(4)
                                );
    
                                return attempLimiter.onSuccess(username, ip)
                                    .thenReturn(new JwtResponse(token));
                            }
                        })
                )
            )
            .onErrorResume(InvalidCredentialsException.class,
                ex -> attempLimiter.onFailure(username, ip)
                    .then(Mono.error(ex)
            )
        );
    }
}
