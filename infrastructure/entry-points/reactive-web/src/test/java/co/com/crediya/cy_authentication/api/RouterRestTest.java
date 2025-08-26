package co.com.crediya.cy_authentication.api;

import co.com.crediya.cy_authentication.api.dto.CreateUserDTO;
import co.com.crediya.cy_authentication.api.dto.EditUserDTO;
import co.com.crediya.cy_authentication.api.dto.UserDTO;
import co.com.crediya.cy_authentication.api.mapper.UserDTOMapper;
import co.com.crediya.cy_authentication.model.user.User;
import co.com.crediya.cy_authentication.usecase.user.IUserUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mock.Strictness;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RouterRestTest {

    @Mock(strictness = Strictness.LENIENT)
    private IUserUseCase userUseCase;

    @Mock(strictness = Strictness.LENIENT)
    private UserDTOMapper userMapper;
    
    @Test
    void shouldConfigureRoutesCorrectly() {
        // Arrange
        Handler handler = new Handler(userUseCase, userMapper);
        RouterFunction<ServerResponse> routerFunction = new RouterRest().routerFunction(handler);
        WebTestClient client = WebTestClient.bindToRouterFunction(routerFunction).build();
        
        // Configurar comportamiento de los mocks
        User mockUser = User.builder()
                .idNumber(123456789L)
                .idType(1)
                .name("John")
                .lastname("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address("123 Main St")
                .phone("1234567890")
                .email("john.doe@example.com")
                .baseSalary(5000.0)
                .username("johndoe")
                .password("password123")
                .build();
        
        when(userUseCase.getAllUsers()).thenReturn(Flux.just(mockUser));
        when(userUseCase.saveUser(any())).thenReturn(Mono.just(mockUser));
        when(userUseCase.editUser(any())).thenReturn(Mono.just(mockUser));
        when(userUseCase.deleteUser(any())).thenReturn(Mono.empty());
        
        // Mock UserDTOMapper para que devuelva objetos válidos
        UserDTO userDTO = new UserDTO();
        userDTO.setIdNumber(123456789L);
        userDTO.setName("John");
        userDTO.setLastname("Doe");
        userDTO.setEmail("john.doe@example.com");
        
        when(userMapper.toResponseList(any())).thenReturn(Collections.singletonList(userDTO));
        when(userMapper.toResponse(any())).thenReturn(userDTO);
        when(userMapper.toModel(any(CreateUserDTO.class))).thenReturn(mockUser);
        when(userMapper.toModel(any(EditUserDTO.class))).thenReturn(mockUser);

        // Act & Assert - GET /api/v1/usuarios
        client.get()
                .uri("/api/v1/usuarios")
                .exchange()
                .expectStatus().isOk();

        // Act & Assert - POST /api/v1/usuarios
        CreateUserDTO createDTO = new CreateUserDTO();
        createDTO.setIdNumber(123456789L);
        createDTO.setName("John");
        createDTO.setLastname("Doe");
        createDTO.setEmail("john.doe@example.com");
        createDTO.setBaseSalary(5000.0);
        createDTO.setUsername("johndoe");
        createDTO.setPassword("password123");
        
        client.post()
                .uri("/api/v1/usuarios")
                .bodyValue(createDTO)
                .exchange()
                .expectStatus().isCreated();

        // Act & Assert - PUT /api/v1/usuarios
        EditUserDTO editDTO = new EditUserDTO();
        editDTO.setIdNumber(123456789L);
        editDTO.setName("John");
        editDTO.setLastname("Doe");
        editDTO.setEmail("john.doe@example.com");
        
        client.put()
                .uri("/api/v1/usuarios")
                .bodyValue(editDTO)
                .exchange()
                .expectStatus().isOk();

        // Act & Assert - DELETE /api/v1/usuarios/{idNumber}
        client.delete()
                .uri("/api/v1/usuarios/123456789")
                .exchange()
                .expectStatus().isOk();
    }
}