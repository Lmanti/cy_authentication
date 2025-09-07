package co.com.crediya.cy_authentication.api.config;

import co.com.crediya.cy_authentication.api.dto.ErrorResponse;
import co.com.crediya.cy_authentication.exception.DataPersistenceException;
import co.com.crediya.cy_authentication.exception.InvalidCredentialsException;
import co.com.crediya.cy_authentication.exception.InvalidUserDataException;
import co.com.crediya.cy_authentication.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.server.ServerWebExchange;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler handler;

  @BeforeEach
  void setUp() {
    handler = new GlobalExceptionHandler();
  }

  private ServerWebExchange exchange(String path) {
    var request = MockServerHttpRequest.get(path).build();
    return MockServerWebExchange.from(request);
  }

  @Test
  @DisplayName("UserNotFoundException -> 404")
  void handleUserNotFound() {
    var ex = new UserNotFoundException("no existe");
    var exch = exchange("/api/users/1");

    StepVerifier.create(handler.handleUserNotFoundException(ex, exch))
        .assertNext(resp -> {
          assertEquals(HttpStatus.NOT_FOUND, resp.getStatusCode());
          ErrorResponse body = resp.getBody();
          assertNotNull(body);
          assertEquals("/api/users/1", body.getPath());
          assertEquals("no existe", body.getMessage());
          assertEquals(404, body.getStatus());
          assertEquals("Not Found", body.getError());
          assertNotNull(body.getTimestamp());
          assertEquals(exch.getRequest().getId(), body.getTraceId());
        })
        .verifyComplete();
  }

  @Test
  @DisplayName("InvalidUserDataException -> 400")
  void handleInvalidUserData() {
    var ex = new InvalidUserDataException("datos inválidos");
    var exch = exchange("/api/users");

    StepVerifier.create(handler.handleInvalidUserDataException(ex, exch))
        .assertNext(resp -> {
          assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
          var body = resp.getBody();
          assertNotNull(body);
          assertEquals("/api/users", body.getPath());
          assertEquals("datos inválidos", body.getMessage());
          assertEquals(400, body.getStatus());
          assertEquals("Bad Request", body.getError());
        })
        .verifyComplete();
  }

  @Test
  @DisplayName("InvalidCredentialsException -> 401")
  void handleInvalidCredentialsCustom() {
    var ex = new InvalidCredentialsException("Credenciales inválidas");
    var exch = exchange("/auth/login");

    StepVerifier.create(handler.handleInvalidCredentialsException(ex, exch))
        .assertNext(resp -> {
          assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
          var body = resp.getBody();
          assertNotNull(body);
          assertEquals("Credenciales inválidas", body.getMessage());
          assertEquals(401, body.getStatus());
          assertEquals("Unauthorized", body.getError());
        })
        .verifyComplete();
  }

  @Test
  @DisplayName("BadCredentialsException -> 401")
  void handleInvalidCredentialsSpring() {
    var ex = new BadCredentialsException("Bad credentials");
    var exch = exchange("/auth/login");

    StepVerifier.create(handler.handleInvalidCredentialsException(ex, exch))
        .assertNext(resp -> {
          assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
          var body = resp.getBody();
          assertNotNull(body);
          assertEquals("Bad credentials", body.getMessage());
          assertEquals(401, body.getStatus());
        })
        .verifyComplete();
  }

  @Test
  @DisplayName("DataPersistenceException -> 500 con prefijo")
  void handleDataPersistence() {
    var ex = new DataPersistenceException("falló insert", new RuntimeException("db"));
    var exch = exchange("/api/users");

    StepVerifier.create(handler.handleDataPersistenceException(ex, exch))
        .assertNext(resp -> {
          assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
          var body = resp.getBody();
          assertNotNull(body);
          assertTrue(body.getMessage().startsWith("Error en la persistencia de datos: "));
          assertTrue(body.getMessage().contains("falló insert"));
          assertEquals(500, body.getStatus());
          assertEquals("Internal Server Error", body.getError());
        })
        .verifyComplete();
  }

  @Test
  @DisplayName("Generic Exception -> 500 con prefijo")
  void handleGeneric() {
    var ex = new RuntimeException("algo salió mal");
    var exch = exchange("/any");

    StepVerifier.create(handler.handleGenericException(ex, exch))
        .assertNext(resp -> {
          assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, resp.getStatusCode());
          var body = resp.getBody();
          assertNotNull(body);
          assertTrue(body.getMessage().startsWith("Error interno del servidor: "));
          assertTrue(body.getMessage().contains("algo salió mal"));
        })
        .verifyComplete();
  }
}
