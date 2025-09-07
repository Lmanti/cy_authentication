package co.com.crediya.cy_authentication.api.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ServerWebExchange;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RestAccessDeniedHandlerTest {

  @Test
  @DisplayName("handle escribe 403 + JSON con error, message y path")
  void handleWritesForbiddenJson() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    RestAccessDeniedHandler handler = new RestAccessDeniedHandler(mapper);

    ServerWebExchange exchange = MockServerWebExchange.from(
        MockServerHttpRequest.get("/api/v1/recurso").build()
    );

    StepVerifier.create(handler.handle(exchange, new AccessDeniedException("denied")))
        .verifyComplete();

    var resp = (org.springframework.mock.http.server.reactive.MockServerHttpResponse) exchange.getResponse();
    assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
    assertEquals(MediaType.APPLICATION_JSON, resp.getHeaders().getContentType());

    String body = resp.getBodyAsString().block();
    assertNotNull(body);

    Map<?, ?> json = mapper.readValue(body, Map.class);
    assertEquals("Forbidden", json.get("error"));
    assertEquals("Lo sentimos, no tienes permitido entrar aquí", json.get("message"));
    assertEquals("/api/v1/recurso", json.get("path"));
  }

  @Test
  @DisplayName("handle usa fallback cuando el ObjectMapper lanza excepción")
  void handleFallbackWhenMapperFails() throws JsonProcessingException {
    ObjectMapper failing = mock(ObjectMapper.class);
    when(failing.writeValueAsBytes(any())).thenThrow(new RuntimeException("boom"));
    RestAccessDeniedHandler handler = new RestAccessDeniedHandler(failing);

    ServerWebExchange exchange = MockServerWebExchange.from(
        MockServerHttpRequest.get("/fallback").build()
    );

    StepVerifier.create(handler.handle(exchange, new AccessDeniedException("denied")))
        .verifyComplete();

    var resp = (org.springframework.mock.http.server.reactive.MockServerHttpResponse) exchange.getResponse();
    assertEquals(HttpStatus.FORBIDDEN, resp.getStatusCode());
    assertEquals(MediaType.APPLICATION_JSON, resp.getHeaders().getContentType());

    String body = resp.getBodyAsString().block();
    assertEquals("{\"error\":\"forbidden\"}", body);
  }
}
