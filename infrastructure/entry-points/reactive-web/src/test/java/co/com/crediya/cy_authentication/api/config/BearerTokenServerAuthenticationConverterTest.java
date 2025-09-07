package co.com.crediya.cy_authentication.api.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.server.ServerWebExchange;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class BearerTokenServerAuthenticationConverterTest {

  private final BearerTokenServerAuthenticationConverter converter =
      new BearerTokenServerAuthenticationConverter();

  @Test
  @DisplayName("Devuelve vacío cuando no hay Authorization")
  void noAuthorizationHeader() {
    ServerWebExchange exchange = MockServerWebExchange.from(
        MockServerHttpRequest.get("/test").build()
    );

    StepVerifier.create(converter.convert(exchange))
        .verifyComplete();
  }

  @Test
  @DisplayName("Devuelve vacío cuando el esquema no es Bearer")
  void nonBearerScheme() {
    ServerWebExchange exchange = MockServerWebExchange.from(
        MockServerHttpRequest.get("/test")
            .header(HttpHeaders.AUTHORIZATION, "Basic abc123")
            .build()
    );

    StepVerifier.create(converter.convert(exchange))
        .verifyComplete();
  }

  @Test
  @DisplayName("Convierte Authorization: Bearer <token> en Authentication con token como principal/credentials")
  void bearerTokenOk() {
    String token = "jwt-123";
    ServerWebExchange exchange = MockServerWebExchange.from(
        MockServerHttpRequest.get("/test")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .build()
    );

    StepVerifier.create(converter.convert(exchange))
        .assertNext(auth -> {
          assertTrue(auth instanceof UsernamePasswordAuthenticationToken);
          assertEquals(token, auth.getPrincipal());
          assertEquals(token, auth.getCredentials());
          // Aún no hay authorities; el manager las establecerá
          assertTrue(auth.getAuthorities() == null || auth.getAuthorities().isEmpty());
        })
        .verifyComplete();
  }

  @Test
  @DisplayName("Acepta 'Bearer ' con token vacío (devuelve Authentication con token vacío)")
  void bearerEmptyToken() {
    ServerWebExchange exchange = MockServerWebExchange.from(
        MockServerHttpRequest.get("/test")
            .header(HttpHeaders.AUTHORIZATION, "Bearer ")
            .build()
    );

    StepVerifier.create(converter.convert(exchange))
        .assertNext(auth -> {
          assertEquals("", auth.getPrincipal());
          assertEquals("", auth.getCredentials());
        })
        .verifyComplete();
  }

  @Test
  @DisplayName("Es sensible a mayúsculas: 'bearer' en minúscula no se acepta")
  void lowercaseBearerNotAccepted() {
    ServerWebExchange exchange = MockServerWebExchange.from(
        MockServerHttpRequest.get("/test")
            .header(HttpHeaders.AUTHORIZATION, "bearer jwt-123")
            .build()
    );

    StepVerifier.create(converter.convert(exchange))
        .verifyComplete();
  }
}
