package co.com.crediya.cy_authentication.r2dbc;

import co.com.crediya.cy_authentication.exception.DataRetrievalException;
import co.com.crediya.cy_authentication.model.role.Role;
import co.com.crediya.cy_authentication.r2dbc.entity.RoleEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.transaction.reactive.TransactionalOperator;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RoleReactiveRepositoryAdapterTest {

    @Mock
    private RoleReactiveReository repository;
    
    @Mock
    private ObjectMapper mapper;

    @Mock
    private TransactionalOperator readOnlyTransactional;

    private RoleReactiveRepositoryAdapter adapter;

    private Role validRole;
    private RoleEntity validRoleEntity;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        when(readOnlyTransactional.transactional(any(Flux.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(readOnlyTransactional.transactional(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        adapter = new RoleReactiveRepositoryAdapter(repository, mapper, readOnlyTransactional);

        validRole = Role.builder()
                .id(1)
                .name("ADMIN")
                .description("Administrator role")
                .build();

        validRoleEntity = new RoleEntity();
        validRoleEntity.setId(1);
        validRoleEntity.setName("ADMIN");
        validRoleEntity.setDescription("Administrator role");
    }

    @Test
    @DisplayName("Should get all roles successfully")
    void shouldGetAllRolesSuccessfully() {
        // Given
        when(repository.findAll()).thenReturn(Flux.just(validRoleEntity));
        when(mapper.map(any(RoleEntity.class), any())).thenReturn(validRole);

        // When & Then
        StepVerifier.create(adapter.getAllRoles())
                .expectNext(validRole)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should get empty flux when no roles exist")
    void shouldGetEmptyFluxWhenNoRolesExist() {
        // Given
        when(repository.findAll()).thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(adapter.getAllRoles())
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle get all roles error")
    void shouldHandleGetAllRolesError() {
        // Given
        when(repository.findAll()).thenReturn(Flux.error(new RuntimeException("Database error")));

        // When & Then
        StepVerifier.create(adapter.getAllRoles())
                .expectError(DataRetrievalException.class)
                .verify();
    }

    @Test
    @DisplayName("Should get role by id successfully")
    void shouldGetRoleByIdSuccessfully() {
        // Given
        when(repository.findById(1)).thenReturn(Mono.just(validRoleEntity));
        when(mapper.map(any(RoleEntity.class), any())).thenReturn(validRole);

        // When & Then
        StepVerifier.create(adapter.getRoleById(1))
                .expectNext(validRole)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should get empty mono when role not found")
    void shouldGetEmptyMonoWhenRoleNotFound() {
        // Given
        when(repository.findById(999)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(adapter.getRoleById(999))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle get role by id error")
    void shouldHandleGetRoleByIdError() {
        // Given
        when(repository.findById(anyInt())).thenReturn(Mono.error(new RuntimeException("Database error")));

        // When & Then
        StepVerifier.create(adapter.getRoleById(1))
                .expectError(DataRetrievalException.class)
                .verify();
    }

    @Test
    @DisplayName("Should get multiple roles successfully")
    void shouldGetMultipleRolesSuccessfully() {
        // Given
        Role userRole = Role.builder()
                .id(2)
                .name("USER")
                .description("Standard user role")
                .build();

        RoleEntity userRoleEntity = new RoleEntity();
        userRoleEntity.setId(2);
        userRoleEntity.setName("USER");
        userRoleEntity.setDescription("Standard user role");

        when(repository.findAll()).thenReturn(Flux.just(validRoleEntity, userRoleEntity));
        when(mapper.map(validRoleEntity, Role.class)).thenReturn(validRole);
        when(mapper.map(userRoleEntity, Role.class)).thenReturn(userRole);

        // When & Then
        StepVerifier.create(adapter.getAllRoles())
                .expectNext(validRole)
                .expectNext(userRole)
                .verifyComplete();
    }
}