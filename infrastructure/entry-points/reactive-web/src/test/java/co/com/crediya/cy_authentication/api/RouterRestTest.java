package co.com.crediya.cy_authentication.api;

import co.com.crediya.cy_authentication.api.dto.CreateUserDTO;
import co.com.crediya.cy_authentication.api.dto.EditUserDTO;
import co.com.crediya.cy_authentication.api.dto.UserDTO;
import co.com.crediya.cy_authentication.model.idtype.IdType;
import co.com.crediya.cy_authentication.model.role.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RouterRestTest {

    @Mock
    private Handler handler;

    private RouterRest routerRest;
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        routerRest = new RouterRest();
        RouterFunction<ServerResponse> routerFunction = routerRest.routerFunction(handler);
        webTestClient = WebTestClient.bindToRouterFunction(routerFunction).build();
    }

    @Test
    @DisplayName("Should route GET /api/v1/usuarios to getAllUsers handler")
    void shouldRouteGetAllUsersToHandler() {
        // Given
        List<UserDTO> users = Arrays.asList(createSampleUserDTO());
        when(handler.getAllUsers(any())).thenReturn(ServerResponse.ok().bodyValue(users));

        // When & Then
        webTestClient.get()
                .uri("/api/v1/usuarios")
                .exchange()
                .expectStatus().isOk();

        verify(handler).getAllUsers(any());
    }

    @Test
    @DisplayName("Should route POST /api/v1/usuarios to createUser handler")
    void shouldRouteCreateUserToHandler() {
        // Given
        CreateUserDTO createUserDTO = createSampleCreateUserDTO();
        UserDTO userDTO = createSampleUserDTO();
        
        when(handler.createUser(any())).thenReturn(
            ServerResponse.created(java.net.URI.create("/detallesUsuario/12345678"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDTO)
        );

        // When & Then
        webTestClient.post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createUserDTO)
                .exchange()
                .expectStatus().isCreated();

        verify(handler).createUser(any());
    }

    @Test
    @DisplayName("Should route PUT /api/v1/usuarios to updateUser handler")
    void shouldRouteUpdateUserToHandler() {
        // Given
        EditUserDTO editUserDTO = createSampleEditUserDTO();
        UserDTO userDTO = createSampleUserDTO();
        
        when(handler.updateUser(any())).thenReturn(
            ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userDTO)
        );

        // When & Then
        webTestClient.put()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(editUserDTO)
                .exchange()
                .expectStatus().isOk();

        verify(handler).updateUser(any());
    }

    @Test
    @DisplayName("Should route DELETE /api/v1/usuarios/{idNumber} to deleteUser handler")
    void shouldRouteDeleteUserToHandler() {
        // Given
        when(handler.deleteUser(any())).thenReturn(ServerResponse.ok().build());

        // When & Then
        webTestClient.delete()
                .uri("/api/v1/usuarios/12345678")
                .exchange()
                .expectStatus().isOk();

        verify(handler).deleteUser(any());
    }

    @Test
    @DisplayName("Should route GET /api/v1/usuarios/parametros/tiposDeIdentificacion to getAllIdTypes handler")
    void shouldRouteGetAllIdTypesToHandler() {
        // Given
        List<IdType> idTypes = Arrays.asList(createSampleIdType());
        when(handler.getAllIdTypes(any())).thenReturn(ServerResponse.ok().bodyValue(idTypes));

        // When & Then
        webTestClient.get()
                .uri("/api/v1/usuarios/parametros/tiposDeIdentificacion")
                .exchange()
                .expectStatus().isOk();

        verify(handler).getAllIdTypes(any());
    }

    @Test
    @DisplayName("Should route GET /api/v1/usuarios/parametros/roles to getAllRoles handler")
    void shouldRouteGetAllRolesToHandler() {
        // Given
        List<Role> roles = Arrays.asList(createSampleRole());
        when(handler.getAllRoles(any())).thenReturn(ServerResponse.ok().bodyValue(roles));

        // When & Then
        webTestClient.get()
                .uri("/api/v1/usuarios/parametros/roles")
                .exchange()
                .expectStatus().isOk();

        verify(handler).getAllRoles(any());
    }

    @Test
    @DisplayName("Should return 404 for non-existent routes")
    void shouldReturn404ForNonExistentRoutes() {
        // When & Then
        webTestClient.get()
                .uri("/api/v1/non-existent")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("Should handle path variables correctly")
    void shouldHandlePathVariablesCorrectly() {
        // Given
        when(handler.deleteUser(any())).thenReturn(ServerResponse.ok().build());

        // When & Then
        webTestClient.delete()
                .uri("/api/v1/usuarios/987654321")
                .exchange()
                .expectStatus().isOk();

        verify(handler).deleteUser(any());
    }

    @Test
    @DisplayName("Should create router function successfully")
    void shouldCreateRouterFunctionSuccessfully() {
        // When
        RouterFunction<ServerResponse> routerFunction = routerRest.routerFunction(handler);

        // Then
        assertNotNull(routerFunction);
    }

    // Helper methods para crear objetos de prueba
    private UserDTO createSampleUserDTO() {
        UserDTO userDTO = new UserDTO();
        userDTO.setIdNumber(12345678L);
        userDTO.setIdType(createSampleIdType());
        userDTO.setName("John");
        userDTO.setLastname("Doe");
        userDTO.setBirthDate(LocalDate.of(1990, 1, 1));
        userDTO.setEmail("john.doe@example.com");
        userDTO.setBaseSalary(3000000.0);
        userDTO.setRole(createSampleRole());
        return userDTO;
    }

    private CreateUserDTO createSampleCreateUserDTO() {
        CreateUserDTO createUserDTO = new CreateUserDTO();
        createUserDTO.setIdNumber(12345678L);
        createUserDTO.setIdTypeId(1);
        createUserDTO.setName("John");
        createUserDTO.setLastname("Doe");
        createUserDTO.setBirthDate(LocalDate.of(1990, 1, 1));
        createUserDTO.setEmail("john.doe@example.com");
        createUserDTO.setBaseSalary(3000000.0);
        createUserDTO.setRoleId(1);
        createUserDTO.setPassword("password123");
        return createUserDTO;
    }

    private EditUserDTO createSampleEditUserDTO() {
        EditUserDTO editUserDTO = new EditUserDTO();
        editUserDTO.setIdNumber(12345678L);
        editUserDTO.setIdTypeId(1);
        editUserDTO.setName("John");
        editUserDTO.setLastname("Doe");
        editUserDTO.setBirthDate(LocalDate.of(1990, 1, 1));
        editUserDTO.setEmail("john.doe@example.com");
        editUserDTO.setBaseSalary(3000000.0);
        editUserDTO.setRoleId(1);
        editUserDTO.setPassword("password123");
        return editUserDTO;
    }

    private IdType createSampleIdType() {
        return IdType.builder()
                .id(1)
                .name("CC")
                .description("Cédula de Ciudadanía")
                .build();
    }

    private Role createSampleRole() {
        return Role.builder()
                .id(1)
                .name("USER")
                .description("Standard user")
                .build();
    }
}