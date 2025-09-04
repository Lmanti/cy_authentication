package co.com.crediya.cy_authentication.usecase.user;

import co.com.crediya.cy_authentication.exception.InvalidUserDataException;
import co.com.crediya.cy_authentication.model.idtype.IdType;
import co.com.crediya.cy_authentication.model.idtype.gateways.IdTypeRepository;
import co.com.crediya.cy_authentication.model.role.Role;
import co.com.crediya.cy_authentication.model.role.gateways.RoleRepository;
import co.com.crediya.cy_authentication.model.user.User;
import co.com.crediya.cy_authentication.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigInteger;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.reset;

class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private IdTypeRepository idTypeRepository;
    
    @Mock
    private RoleRepository roleRepository;

    private UserUseCase userUseCase;

    private User validUser;
    private IdType validIdType;
    private Role validRole;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Reset all mocks before each test
        reset(userRepository, idTypeRepository, roleRepository);
        
        userUseCase = new UserUseCase(userRepository, idTypeRepository, roleRepository);

        validIdType = IdType.builder()
                .id(1)
                .name("CC")
                .description("Cédula de Ciudadanía")
                .build();

        validRole = Role.builder()
                .id(1)
                .name("USER")
                .description("Standard user")
                .build();

        validUser = User.builder()
                .id(BigInteger.valueOf(1))
                .idNumber(12345678L)
                .idTypeId(1)
                .name("John")
                .lastname("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("john.doe@example.com")
                .baseSalary(3000000.0)
                .roleId(1)
                .password("password123")
                .build();
    }

    @Test
    @DisplayName("Should save user successfully")
    void shouldSaveUserSuccessfully() {
        // Given
        when(idTypeRepository.getIdTypeById(1)).thenReturn(Mono.just(validIdType));
        when(roleRepository.getRoleById(1)).thenReturn(Mono.just(validRole));
        when(userRepository.findByEmailOrIdNumber(any(), any())).thenReturn(Mono.empty());
        when(userRepository.saveUser(any())).thenReturn(Mono.just(validUser));

        // When & Then
        StepVerifier.create(userUseCase.saveUser(Mono.just(validUser)))
                .expectNextMatches(userRecord -> 
                    userRecord.name().equals("John") &&
                    userRecord.email().equals("john.doe@example.com") &&
                    userRecord.idType().getName().equals("CC") &&
                    userRecord.role().getName().equals("USER")
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should fail when IdType not found")
    void shouldFailWhenIdTypeNotFound() {
        // Given
        when(userRepository.findByEmailOrIdNumber(any(), any())).thenReturn(Mono.empty());
        when(idTypeRepository.getIdTypeById(1)).thenReturn(Mono.empty());
        when(roleRepository.getRoleById(1)).thenReturn(Mono.just(validRole));

        // When & Then
        StepVerifier.create(userUseCase.saveUser(Mono.just(validUser)))
                .expectError(InvalidUserDataException.class)
                .verify();
    }

    @Test
    @DisplayName("Should fail when Role not found")
    void shouldFailWhenRoleNotFound() {
        // Given
        when(userRepository.findByEmailOrIdNumber(any(), any())).thenReturn(Mono.empty());
        when(idTypeRepository.getIdTypeById(1)).thenReturn(Mono.just(validIdType));
        when(roleRepository.getRoleById(1)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(userUseCase.saveUser(Mono.just(validUser)))
                .expectError(InvalidUserDataException.class)
                .verify();
    }

    @Test
    @DisplayName("Should get all users successfully")
    void shouldGetAllUsersSuccessfully() {
        // Given
        when(idTypeRepository.getAllIdTypes()).thenReturn(Flux.just(validIdType));
        when(roleRepository.getAllRoles()).thenReturn(Flux.just(validRole));
        when(userRepository.getAllUsers()).thenReturn(Flux.just(validUser));

        // When & Then
        StepVerifier.create(userUseCase.getAllUsers())
                .expectNextMatches(userRecord -> 
                    userRecord.name().equals("John") &&
                    userRecord.idType().getName().equals("CC")
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should get user by id number successfully")
    void shouldGetUserByIdNumberSuccessfully() {
        // Given
        when(userRepository.getByIdNumber(12345678L)).thenReturn(Mono.just(validUser));
        when(idTypeRepository.getIdTypeById(1)).thenReturn(Mono.just(validIdType));
        when(roleRepository.getRoleById(1)).thenReturn(Mono.just(validRole));

        // When & Then
        StepVerifier.create(userUseCase.getByIdNumber(12345678L))
                .expectNextMatches(userRecord -> 
                    userRecord.idNumber().equals(12345678L) &&
                    userRecord.name().equals("John")
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should edit user successfully")
    void shouldEditUserSuccessfully() {
        // Given - Configurar todos los mocks necesarios para este test
        when(idTypeRepository.getIdTypeById(1)).thenReturn(Mono.just(validIdType));
        when(roleRepository.getRoleById(1)).thenReturn(Mono.just(validRole));
        when(userRepository.findByEmailOrIdNumber(any(), any())).thenReturn(Mono.just(validUser));
        when(userRepository.editUser(any())).thenReturn(Mono.just(validUser));

        // When & Then
        StepVerifier.create(userUseCase.editUser(Mono.just(validUser)))
                .expectNextMatches(userRecord -> 
                    userRecord.name().equals("John") &&
                    userRecord.email().equals("john.doe@example.com")
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUserSuccessfully() {
        // Given
        when(userRepository.deleteUser(12345678L)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(userUseCase.deleteUser(12345678L))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should fail validation when name is null")
    void shouldFailValidationWhenNameIsNull() {
        // Given
        User invalidUser = validUser.toBuilder().name(null).build();
        when(idTypeRepository.getIdTypeById(1)).thenReturn(Mono.just(validIdType));
        when(roleRepository.getRoleById(1)).thenReturn(Mono.just(validRole));

        // When & Then
        StepVerifier.create(userUseCase.saveUser(Mono.just(invalidUser)))
                .expectError(InvalidUserDataException.class)
                .verify();
    }

    @Test
    @DisplayName("Should fail validation when email is invalid")
    void shouldFailValidationWhenEmailIsInvalid() {
        // Given
        User invalidUser = validUser.toBuilder().email("invalid-email").build();
        when(idTypeRepository.getIdTypeById(1)).thenReturn(Mono.just(validIdType));
        when(roleRepository.getRoleById(1)).thenReturn(Mono.just(validRole));

        // When & Then
        StepVerifier.create(userUseCase.saveUser(Mono.just(invalidUser)))
                .expectError(InvalidUserDataException.class)
                .verify();
    }

    @Test
    @DisplayName("Should fail validation when salary is out of range")
    void shouldFailValidationWhenSalaryIsOutOfRange() {
        // Given
        User invalidUser = validUser.toBuilder().baseSalary(20000000.0).build();
        when(idTypeRepository.getIdTypeById(1)).thenReturn(Mono.just(validIdType));
        when(roleRepository.getRoleById(1)).thenReturn(Mono.just(validRole));

        // When & Then
        StepVerifier.create(userUseCase.saveUser(Mono.just(invalidUser)))
                .expectError(InvalidUserDataException.class)
                .verify();
    }

    @Test
    @DisplayName("Should fail when user email already exists")
    void shouldFailWhenUserEmailAlreadyExists() {
        // Given
        User existingUser = validUser.toBuilder().idNumber(87654321L).build();
        when(idTypeRepository.getIdTypeById(1)).thenReturn(Mono.just(validIdType));
        when(roleRepository.getRoleById(1)).thenReturn(Mono.just(validRole));
        when(userRepository.findByEmailOrIdNumber(any(), any())).thenReturn(Mono.just(existingUser));

        // When & Then
        StepVerifier.create(userUseCase.saveUser(Mono.just(validUser)))
                .expectError(InvalidUserDataException.class)
                .verify();
    }
}