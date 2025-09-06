package co.com.crediya.cy_authentication.api.config;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

@Component
public class RestAccessDeniedHandler implements ServerAccessDeniedHandler {

    private final ObjectMapper mapper;

    public RestAccessDeniedHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException ex) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of(
            "error", "Forbidden",
            "message", "Lo sentimos, no tienes permitido entrar aquí",
            "path", exchange.getRequest().getPath().value()
        );

        try {
            byte[] bytes = mapper.writeValueAsBytes(body);
            return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
        } catch (Exception e) {
            DataBuffer fallback = response.bufferFactory().wrap("{\"error\":\"forbidden\"}".getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(fallback));
        }
    }
    
}
