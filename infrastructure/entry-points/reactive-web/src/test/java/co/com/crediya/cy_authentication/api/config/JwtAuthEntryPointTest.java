package co.com.crediya.cy_authentication.api.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.server.ServerWebExchange;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtAuthEntryPointTest {

  @Test
  @DisplayName("commence escribe 401 + JSON con error, message y path")
  void commenceWritesUnauthorizedJson() throws Exception {
    // Arrange
    ObjectMapper mapper = new ObjectMapper();
    JwtAuthEntryPoint entryPoint = new JwtAuthEntryPoint(mapper);

    ServerWebExchange exchange = MockServerWebExchange.from(
        MockServerHttpRequest.get("/api/v1/test").build()
    );

    // Act
    StepVerifier.create(
        entryPoint.commence(exchange, new AuthenticationCredentialsNotFoundException("no auth"))
    ).verifyComplete();

    // Assert
    var resp = (org.springframework.mock.http.server.reactive.MockServerHttpResponse) exchange.getResponse();

    assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    assertEquals(MediaType.APPLICATION_JSON, resp.getHeaders().getContentType());

    String body = resp.getBodyAsString().block();
    assertNotNull(body);

    Map<?, ?> json = mapper.readValue(body, Map.class);
    assertEquals("Unauthorized", json.get("error"));
    assertEquals("Lo sentimos, no está autorizado para realizar esta acción", json.get("message"));
    assertEquals("/api/v1/test", json.get("path"));
  }

  @Test
  @DisplayName("commence usa fallback cuando el ObjectMapper lanza excepción")
  void commenceFallbackWhenMapperFails() throws JsonProcessingException {
    // Arrange: ObjectMapper que falla
    ObjectMapper failing = mock(ObjectMapper.class);
    when(failing.writeValueAsBytes(any())).thenThrow(new RuntimeException("boom"));
    JwtAuthEntryPoint entryPoint = new JwtAuthEntryPoint(failing);

    ServerWebExchange exchange = MockServerWebExchange.from(
        MockServerHttpRequest.get("/fallback").build()
    );

    // Act
    StepVerifier.create(
        entryPoint.commence(exchange, new AuthenticationCredentialsNotFoundException("no auth"))
    ).verifyComplete();

    // Assert
    var resp = (org.springframework.mock.http.server.reactive.MockServerHttpResponse) exchange.getResponse();
    assertEquals(HttpStatus.UNAUTHORIZED, resp.getStatusCode());
    assertEquals(MediaType.APPLICATION_JSON, resp.getHeaders().getContentType());

    String body = resp.getBodyAsString().block();
    assertEquals("{\"error\":\"unauthorized\"}", body);
  }
}
