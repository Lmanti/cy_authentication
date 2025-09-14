package co.com.crediya.cy_authentication.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.authentication.ServerAuthenticationEntryPointFailureHandler;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    private final String baseURL = "/api/v1/usuarios";

    private enum Roles {
        ADMIN("ADMIN"),
        ASESOR("ASESOR"),
        CLIENTE("CLIENTE");

        private String value;

        Roles(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
        ReactiveAuthenticationManager authManager,
        ServerAuthenticationConverter converter,
        ServerAuthenticationEntryPoint entryPoint,
        ServerAccessDeniedHandler deniedHandler
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
                .pathMatchers("/actuator/health").permitAll()
                .pathMatchers("/swagger-ui.html", "/swagger-ui/**").permitAll()
                .pathMatchers("/v3/api-docs", "/v3/api-docs/**", "/v3/api-docs.yaml", "/v3/api-docs/swagger-config").permitAll()
                .pathMatchers("/api-docs", "/api-docs/**", "/api-docs.yaml", "/api-docs/swagger-config").permitAll()
                .pathMatchers("/webjars/**").permitAll()
                .pathMatchers(HttpMethod.GET, baseURL + "/basicInfo").permitAll()
                .pathMatchers(HttpMethod.POST, baseURL + "/login").permitAll()
                .pathMatchers(HttpMethod.POST, baseURL)
                    .hasAnyRole(Roles.ADMIN.toString(), Roles.ASESOR.toString())
                .pathMatchers(HttpMethod.GET, baseURL + "/exists/**")
                    .hasRole(Roles.CLIENTE.toString())
                .anyExchange().authenticated()
            )
            .addFilterAt(authWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .build();
    }
}
