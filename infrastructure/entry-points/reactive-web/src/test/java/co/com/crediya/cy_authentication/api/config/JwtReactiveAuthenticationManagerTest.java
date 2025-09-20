package co.com.crediya.cy_authentication.api.config;

import co.com.crediya.cy_authentication.model.security.TokenData;
import co.com.crediya.cy_authentication.model.security.gateways.TokenGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtReactiveAuthenticationManagerTest {

  private TokenGenerator tokenGenerator;
  private JwtReactiveAuthenticationManager authManager;

  @BeforeEach
  void setUp() {
    tokenGenerator = mock(TokenGenerator.class);
    authManager = new JwtReactiveAuthenticationManager(tokenGenerator);
  }

  @Test
  @DisplayName("Autentica con token válido y mapea roles con prefijo ROLE_")
  void authenticateSuccess() {
    String token = "good-token";
    var data = new TokenData("user-123", List.of(1, 3));

    when(tokenGenerator.verify(token)).thenReturn(Optional.of(data));

    var inputAuth = new UsernamePasswordAuthenticationToken("ignored", token);

    StepVerifier.create(authManager.authenticate(inputAuth))
        .assertNext(auth -> {
          // Principal subject
          assertEquals("user-123", auth.getName());
          // Credenciales = token original
          assertEquals(token, auth.getCredentials());
          // Authorities con prefijo
          var authorities = auth.getAuthorities().stream().map(Object::toString).toList();
          // Puede ser [ROLE_ADMIN, ROLE_CLIENTE] en cualquier orden
          org.assertj.core.api.Assertions.assertThat(authorities)
              .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_CLIENTE");
        })
        .verifyComplete();

    verify(tokenGenerator).verify(token);
  }

  @Test
  @DisplayName("Falla con BadCredentialsException cuando el token es inválido (Optional.empty)")
  void authenticateInvalidToken() {
    String token = "bad-token";
    when(tokenGenerator.verify(token)).thenReturn(Optional.empty());

    var inputAuth = new UsernamePasswordAuthenticationToken("ignored", token);

    StepVerifier.create(authManager.authenticate(inputAuth))
        .expectError(BadCredentialsException.class)
        .verify();

    verify(tokenGenerator).verify(token);
  }

  @Test
  @DisplayName("Propaga excepción cuando el generador de tokens lanza error")
  void authenticateTokenGeneratorThrows() {
    String token = "oops-token";
    when(tokenGenerator.verify(token)).thenThrow(new RuntimeException("boom"));

    var inputAuth = new UsernamePasswordAuthenticationToken("ignored", token);

    StepVerifier.create(authManager.authenticate(inputAuth))
        .expectErrorMatches(e -> e instanceof RuntimeException && e.getMessage().contains("boom"))
        .verify();

    verify(tokenGenerator).verify(token);
  }

  @Test
  @DisplayName("Token nulo se trata como inválido (BadCredentials)")
  void authenticateNullToken() {
    when(tokenGenerator.verify(isNull())).thenReturn(Optional.empty());

    var inputAuth = new UsernamePasswordAuthenticationToken("ignored", null);

    StepVerifier.create(authManager.authenticate(inputAuth))
        .expectError(BadCredentialsException.class)
        .verify();

    verify(tokenGenerator).verify(null);
  }
}
