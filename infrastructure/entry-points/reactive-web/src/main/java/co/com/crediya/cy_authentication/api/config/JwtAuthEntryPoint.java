package co.com.crediya.cy_authentication.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class JwtAuthEntryPoint implements ServerAuthenticationEntryPoint {

    private final ObjectMapper mapper;

    public JwtAuthEntryPoint(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of(
            "error", "Unauthorized",
            "message", "Lo sentimos, no está autorizado para realizar esta acción",
            "path", exchange.getRequest().getPath().value()
        );

        try {
            byte[] bytes = mapper.writeValueAsBytes(body);
            return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)));
        } catch (Exception e) {
            DataBuffer fallback = response.bufferFactory().wrap("{\"error\":\"unauthorized\"}".getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(fallback));
        }
    }
}
