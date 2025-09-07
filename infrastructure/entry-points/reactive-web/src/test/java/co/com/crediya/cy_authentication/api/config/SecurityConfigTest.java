package co.com.crediya.cy_authentication.api.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = {
        SecurityConfigTest.TestApp.class,      // Boot config mínima
        SecurityConfig.class,                  // tu config real
        SecurityConfigTest.RoutesConfig.class, // rutas dummy
        SecurityConfigTest.SecurityTestBeans.class // beans de seguridad fake
    }
)
@AutoConfigureWebTestClient
class SecurityConfigTest {

  private static final String BASE = "/api/v1/usuarios";

  @Autowired
  WebTestClient client;

  @Test
  void loginIsPublic() {
    client.post().uri(BASE + "/login")
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue("{}"))
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  void createUserRequiresAdminOrAsesor() {
    client.post().uri(BASE).exchange().expectStatus().isUnauthorized();

    client.post().uri(BASE)
        .header("Authorization", "Bearer cliente-token")
        .exchange()
        .expectStatus().isForbidden();

    client.post().uri(BASE)
        .header("Authorization", "Bearer admin-token")
        .exchange()
        .expectStatus().isCreated();

    client.post().uri(BASE)
        .header("Authorization", "Bearer asesor-token")
        .exchange()
        .expectStatus().isCreated();
  }

  @Test
  void existsRequiresCliente() {
    client.get().uri(BASE + "/exists/1").exchange().expectStatus().isUnauthorized();

    client.get().uri(BASE + "/exists/1")
        .header("Authorization", "Bearer admin-token")
        .exchange()
        .expectStatus().isForbidden();

    client.get().uri(BASE + "/exists/1")
        .header("Authorization", "Bearer cliente-token")
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  void otherRequiresAuthenticated() {
    client.get().uri(BASE + "/other").exchange().expectStatus().isUnauthorized();

    client.get().uri(BASE + "/other")
        .header("Authorization", "Bearer asesor-token")
        .exchange()
        .expectStatus().isOk();
  }

  // Boot configuration mínima para el contexto de pruebas
  @SpringBootConfiguration
  @EnableAutoConfiguration
  static class TestApp { }

  // Rutas dummy que matchean tus pathMatchers
  @Configuration
  static class RoutesConfig {
    @Bean
    RouterFunction<ServerResponse> routes() {
      return route(POST("/api/v1/usuarios/login"), req -> ServerResponse.ok().build())
          .andRoute(POST("/api/v1/usuarios"), req -> ServerResponse.status(201).build())
          .andRoute(GET("/api/v1/usuarios/exists/{id}"), req -> ServerResponse.ok().build())
          .andRoute(GET("/api/v1/usuarios/other"), req -> ServerResponse.ok().build());
    }
  }

  // Beans fake de seguridad para el test (ganan por @Primary)
  @Configuration
  static class SecurityTestBeans {

    @Bean
    @Primary
    ServerAuthenticationConverter converter() {
      return exchange -> {
        var h = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (h != null && h.startsWith("Bearer ")) {
          var token = h.substring(7);
          return Mono.just(new UsernamePasswordAuthenticationToken(token, token));
        }
        return Mono.empty();
      };
    }

    @Bean
    @Primary
    ReactiveAuthenticationManager authManager() {
      return authentication -> {
        var token = (String) authentication.getCredentials();
        if (token == null) return Mono.error(new BadCredentialsException("no token"));
        return switch (token) {
          case "admin-token" -> Mono.just(new UsernamePasswordAuthenticationToken(
              "admin", token, List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));
          case "asesor-token" -> Mono.just(new UsernamePasswordAuthenticationToken(
              "asesor", token, List.of(new SimpleGrantedAuthority("ROLE_ASESOR"))));
          case "cliente-token" -> Mono.just(new UsernamePasswordAuthenticationToken(
              "cliente", token, List.of(new SimpleGrantedAuthority("ROLE_CLIENTE"))));
          default -> Mono.error(new BadCredentialsException("bad token"));
        };
      };
    }

    @Bean
    @Primary
    ServerAuthenticationEntryPoint entryPoint() {
      return (exchange, ex) -> {
        exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
      };
    }

    @Bean
    @Primary
    ServerAccessDeniedHandler deniedHandler() {
      return (exchange, ex) -> {
        exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.FORBIDDEN);
        return exchange.getResponse().setComplete();
      };
    }
  }
}