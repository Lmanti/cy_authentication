package co.com.crediya.cy_authentication.usecase.authenticateuser;

import co.com.crediya.cy_authentication.exception.InvalidCredentialsException;
import co.com.crediya.cy_authentication.model.role.Role;
import co.com.crediya.cy_authentication.model.role.gateways.RoleRepository;
import co.com.crediya.cy_authentication.model.security.JwtToken;
import co.com.crediya.cy_authentication.model.security.gateways.PasswordHasher;
import co.com.crediya.cy_authentication.model.security.gateways.TokenGenerator;
import co.com.crediya.cy_authentication.model.user.User;
import co.com.crediya.cy_authentication.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigInteger;
import java.time.Duration;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticateUserUseCaseTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private RoleRepository roleRepository;

  @Mock
  private PasswordHasher hasher;

  @Mock
  private TokenGenerator tokens;

  private AuthenticateUserUseCase useCase;

  @BeforeEach
  void setUp() {
    useCase = new AuthenticateUserUseCase(userRepository, roleRepository, hasher, tokens);
  }

  @Test
  @DisplayName("Debería autenticar y devolver JwtToken cuando credenciales son válidas")
  void shouldAuthenticateAndReturnJwtToken() {
    // Arrange
    String username = "john.doe@example.com";
    String rawPassword = "pass123";
    String hashedPassword = "$2a$12$hash";
    BigInteger userId = BigInteger.valueOf(1l);

    User user = mock(User.class);
    when(user.getRoleId()).thenReturn(1);
    when(user.getPassword()).thenReturn(hashedPassword);
    when(user.getId()).thenReturn(userId);

    Role role = mock(Role.class);
    when(role.getId()).thenReturn(1);

    when(userRepository.getByEmail(username)).thenReturn(Mono.just(user));
    when(roleRepository.getRoleById(1)).thenReturn(Mono.just(role));
    when(hasher.matches(rawPassword, hashedPassword)).thenReturn(true);
    when(tokens.generate(userId.toString(), List.of(1), Duration.ofHours(4))).thenReturn("jwt-token-123");

    // Act & Assert
    StepVerifier.create(useCase.handle(username, rawPassword))
        .expectNextMatches(jwt -> jwt instanceof JwtToken && "jwt-token-123".equals(jwt.getToken()))
        .verifyComplete();
  }

  @Test
  @DisplayName("Debería fallar con InvalidCredentialsException cuando usuario no existe")
  void shouldFailWhenUserNotFound() {
    // Arrange
    when(userRepository.getByEmail(anyString())).thenReturn(Mono.empty());

    // Act & Assert
    StepVerifier.create(useCase.handle("noone@example.com", "x"))
        .expectError(InvalidCredentialsException.class)
        .verify();
  }

  @Test
  @DisplayName("Debería completar vacío cuando el rol no existe (Mono.empty en RoleRepository)")
  void shouldCompleteEmptyWhenRoleNotFound() {
    // Arrange
    User user = mock(User.class);
    when(user.getRoleId()).thenReturn(99);
    when(userRepository.getByEmail(anyString())).thenReturn(Mono.just(user));
    when(roleRepository.getRoleById(99)).thenReturn(Mono.empty());

    // Act & Assert
    StepVerifier.create(useCase.handle("john.doe@example.com", "x"))
        .verifyComplete();
  }

  @Test
  @DisplayName("Debería fallar con InvalidCredentialsException cuando la contraseña no coincide")
  void shouldFailWhenPasswordDoesNotMatch() {
    // Arrange
    String username = "john.doe@example.com";
    String rawPassword = "wrong";
    String hashedPassword = "$2a$12$hash";

    User user = mock(User.class);
    when(user.getRoleId()).thenReturn(1);
    when(user.getPassword()).thenReturn(hashedPassword);

    when(userRepository.getByEmail(username)).thenReturn(Mono.just(user));
    when(roleRepository.getRoleById(1)).thenReturn(Mono.just(mock(Role.class)));
    when(hasher.matches(rawPassword, hashedPassword)).thenReturn(false);

    // Act & Assert
    StepVerifier.create(useCase.handle(username, rawPassword))
        .expectError(InvalidCredentialsException.class)
        .verify();
  }
}
