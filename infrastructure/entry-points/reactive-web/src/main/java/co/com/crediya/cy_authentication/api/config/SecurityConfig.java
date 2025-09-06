package co.com.crediya.cy_authentication.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationEntryPointFailureHandler;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
        JwtReactiveAuthenticationManager authManager,
        BearerTokenServerAuthenticationConverter converter,
        JwtAuthEntryPoint entryPoint,
        RestAccessDeniedHandler deniedHandler
    ) {

        AuthenticationWebFilter authWebFilter = new AuthenticationWebFilter(authManager);
        authWebFilter.setServerAuthenticationConverter(converter);
        authWebFilter.setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance());
        authWebFilter.setAuthenticationFailureHandler(new ServerAuthenticationEntryPointFailureHandler(entryPoint));

        return http
            .csrf(csrf -> csrf.disable())
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(entryPoint)
                .accessDeniedHandler(deniedHandler)
            )
            .authorizeExchange(auth -> auth
                .pathMatchers(
                    HttpMethod.GET,
                    "/actuator/health"
                ).permitAll()
                .pathMatchers(
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs",
                    "/v3/api-docs/**",
                    "/v3/api-docs.yaml",
                    "/v3/api-docs/swagger-config",
                    "/api-docs",
                    "/api-docs/**",
                    "/api-docs.yaml",
                    "/api-docs/swagger-config",
                    "/webjars/**"
                ).permitAll()
                .pathMatchers(
                    HttpMethod.POST, 
                    "/api/v1/usuarios",
                    "/api/v1/usuarios/login"
                ).permitAll()
                .anyExchange().authenticated()
            )
            .addFilterAt(authWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .build();
    }
}
