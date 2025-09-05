package co.com.crediya.cy_authentication.usecase.role;

import co.com.crediya.cy_authentication.model.role.Role;
import co.com.crediya.cy_authentication.model.role.gateways.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleUseCaseTest {

    @Mock
    private RoleRepository roleRepository;

    private RoleUseCase roleUseCase;

    private Role adminRole;
    private Role userRole;

    @BeforeEach
    void setUp() {
        roleUseCase = new RoleUseCase(roleRepository);

        adminRole = Role.builder()
                .id(1)
                .name("ADMIN")
                .description("Administrator role")
                .build();

        userRole = Role.builder()
                .id(2)
                .name("USER")
                .description("Standard user role")
                .build();
    }

    @Test
    @DisplayName("Should get all roles successfully")
    void shouldGetAllRolesSuccessfully() {
        // Given
        when(roleRepository.getAllRoles()).thenReturn(Flux.just(adminRole, userRole));

        // When & Then
        StepVerifier.create(roleUseCase.getAllRoles())
                .expectNext(adminRole)
                .expectNext(userRole)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should get empty flux when no roles exist")
    void shouldGetEmptyFluxWhenNoRolesExist() {
        // Given
        when(roleRepository.getAllRoles()).thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(roleUseCase.getAllRoles())
                .verifyComplete();
    }

    @Test
    @DisplayName("Should get role by id successfully")
    void shouldGetRoleByIdSuccessfully() {
        // Given
        when(roleRepository.getRoleById(1)).thenReturn(Mono.just(adminRole));

        // When & Then
        StepVerifier.create(roleUseCase.getRoleById(1))
                .expectNext(adminRole)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should get empty mono when role not found")
    void shouldGetEmptyMonoWhenRoleNotFound() {
        // Given
        when(roleRepository.getRoleById(999)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(roleUseCase.getRoleById(999))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle repository error")
    void shouldHandleRepositoryError() {
        // Given
        when(roleRepository.getAllRoles()).thenReturn(Flux.error(new RuntimeException("Database error")));

        // When & Then
        StepVerifier.create(roleUseCase.getAllRoles())
                .expectError(RuntimeException.class)
                .verify();
    }
}