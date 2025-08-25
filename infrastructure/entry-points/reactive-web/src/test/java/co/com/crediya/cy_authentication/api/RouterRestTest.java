package co.com.crediya.cy_authentication.api;

import co.com.crediya.cy_authentication.api.config.GlobalExceptionHandler;
import co.com.crediya.cy_authentication.api.config.TestConfig;
import co.com.crediya.cy_authentication.api.mapper.UserDTOMapper;
import co.com.crediya.cy_authentication.exception.UserNotFoundException;
import co.com.crediya.cy_authentication.usecase.user.IUserUseCase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {RouterRest.class, Handler.class, TestConfig.class})
@Import(GlobalExceptionHandler.class)
@ActiveProfiles("test")
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private IUserUseCase userUseCase;

    @Autowired
    private UserDTOMapper userMapper;

    private final String baseUrl = "/api/v1/usuarios";

    @BeforeEach
    void setUp() {
        when(userUseCase.getAllUsers()).thenReturn(Flux.empty());
        when(userMapper.toResponseList(any())).thenReturn(new ArrayList<>());
    }

    @Test
    void testGetAllUsers() {
        webTestClient.get().uri(baseUrl)
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    void testUserNotFound() {
        when(userUseCase.deleteUser(anyLong())).thenReturn(
            Mono.error(new UserNotFoundException("Usuario no encontrado"))
        );
        
        webTestClient.delete()
            .uri(baseUrl + "/123")
            .exchange()
            .expectStatus().isNotFound()
            .expectBody()
            .jsonPath("$.message").isEqualTo("Usuario no encontrado")
            .jsonPath("$.status").isEqualTo(404);
    }
    
    @Test
    void testDeleteUserSuccess() {
        when(userUseCase.deleteUser(123L)).thenReturn(Mono.empty());
        
        webTestClient.delete()
            .uri(baseUrl + "/123")
            .exchange()
            .expectStatus().isOk();
    }
}