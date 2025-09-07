package co.com.crediya.cy_authentication.usecase.user;

import co.com.crediya.cy_authentication.exception.InvalidUserDataException;
import co.com.crediya.cy_authentication.model.idtype.IdType;
import co.com.crediya.cy_authentication.model.idtype.gateways.IdTypeRepository;
import co.com.crediya.cy_authentication.model.role.Role;
import co.com.crediya.cy_authentication.model.role.gateways.RoleRepository;
import co.com.crediya.cy_authentication.model.security.gateways.PasswordHasher;
import co.com.crediya.cy_authentication.model.user.User;
import co.com.crediya.cy_authentication.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigInteger;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.reset;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private IdTypeRepository idTypeRepository;
    
    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordHasher passwordHasher;

    private UserUseCase userUseCase;

    private User validUser;
    private IdType validIdType;
    private Role validRole;

    @BeforeEach
    void setUp() {        
        // Reset all mocks before each test
        reset(userRepository, idTypeRepository, roleRepository);
        
        userUseCase = new UserUseCase(userRepository, idTypeRepository, roleRepository, passwordHasher);

        when(passwordHasher.hash(any())).thenReturn("$2a$12$dummyhashdummyhashdummyhashdum");

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
    @DisplayName("Should fail validation when name is null")
    void shouldFailValidationWhenNameIsNull2() {
        // Given
        User invalidUser = validUser.toBuilder().name(null).build();

        // When & Then
        StepVerifier.create(userUseCase.saveUser(Mono.just(invalidUser)))
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException &&
                    throwable.getMessage().contains("El nombre es requerido"))
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

    @Test
    @DisplayName("Should fail validation when idNumber is null")
    void shouldFailValidationWhenIdNumberIsNull() {
        // Given
        User invalidUser = validUser.toBuilder().idNumber(null).build();

        // When & Then
        StepVerifier.create(userUseCase.saveUser(Mono.just(invalidUser)))
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException &&
                    throwable.getMessage().contains("El número de identificación es requerido"))
                .verify();
    }

    @Test
    @DisplayName("Should fail validation when idNumber is zero")
    void shouldFailValidationWhenIdNumberIsZero() {
        // Given
        User invalidUser = validUser.toBuilder().idNumber(0L).build();

        // When & Then
        StepVerifier.create(userUseCase.saveUser(Mono.just(invalidUser)))
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException &&
                    throwable.getMessage().contains("El número de identificación es requerido"))
                .verify();
    }

    @Test
    @DisplayName("Should fail validation when lastname is null")
    void shouldFailValidationWhenLastnameIsNull() {
        // Given
        User invalidUser = validUser.toBuilder().lastname(null).build();

        // When & Then
        StepVerifier.create(userUseCase.saveUser(Mono.just(invalidUser)))
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException &&
                    throwable.getMessage().contains("El apellido es requerido"))
                .verify();
    }

    @Test
    @DisplayName("Should fail validation when lastname is empty")
    void shouldFailValidationWhenLastnameIsEmpty() {
        // Given
        User invalidUser = validUser.toBuilder().lastname("").build();

        // When & Then
        StepVerifier.create(userUseCase.saveUser(Mono.just(invalidUser)))
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException &&
                    throwable.getMessage().contains("El apellido es requerido"))
                .verify();
    }

    @Test
    @DisplayName("Should fail validation when lastname is whitespace")
    void shouldFailValidationWhenLastnameIsWhitespace() {
        // Given
        User invalidUser = validUser.toBuilder().lastname("   ").build();

        // When & Then
        StepVerifier.create(userUseCase.saveUser(Mono.just(invalidUser)))
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException &&
                    throwable.getMessage().contains("El apellido es requerido"))
                .verify();
    }

    @Test
    @DisplayName("Should fail validation when email is null")
    void shouldFailValidationWhenEmailIsNull() {
        // Given
        User invalidUser = validUser.toBuilder().email(null).build();

        // When & Then
        StepVerifier.create(userUseCase.saveUser(Mono.just(invalidUser)))
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException &&
                    throwable.getMessage().contains("El correo electrónico es requerido"))
                .verify();
    }

    @Test
    @DisplayName("Should fail validation when email is empty")
    void shouldFailValidationWhenEmailIsEmpty() {
        // Given
        User invalidUser = validUser.toBuilder().email("").build();

        // When & Then
        StepVerifier.create(userUseCase.saveUser(Mono.just(invalidUser)))
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException &&
                    throwable.getMessage().contains("El correo electrónico es requerido"))
                .verify();
    }

    @Test
    @DisplayName("Should fail validation when email is whitespace")
    void shouldFailValidationWhenEmailIsWhitespace() {
        // Given
        User invalidUser = validUser.toBuilder().email("   ").build();

        // When & Then
        StepVerifier.create(userUseCase.saveUser(Mono.just(invalidUser)))
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException &&
                    throwable.getMessage().contains("El correo electrónico es requerido"))
                .verify();
    }

    @Test
    @DisplayName("Should fail validation when baseSalary is null")
    void shouldFailValidationWhenBaseSalaryIsNull() {
        // Given
        User invalidUser = validUser.toBuilder().baseSalary(null).build();

        // When & Then
        StepVerifier.create(userUseCase.saveUser(Mono.just(invalidUser)))
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException &&
                    throwable.getMessage().contains("El salario base es requerido"))
                .verify();
    }

    @Test
    @DisplayName("Should fail validation when password is null")
    void shouldFailValidationWhenPasswordIsNull() {
        // Given
        User invalidUser = validUser.toBuilder().password(null).build();

        // When & Then
        StepVerifier.create(userUseCase.saveUser(Mono.just(invalidUser)))
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException &&
                    throwable.getMessage().contains("La contraseña es requerida"))
                .verify();
    }

    @Test
    @DisplayName("Should fail validation when password is empty")
    void shouldFailValidationWhenPasswordIsEmpty() {
        // Given
        User invalidUser = validUser.toBuilder().password("").build();

        // When & Then
        StepVerifier.create(userUseCase.saveUser(Mono.just(invalidUser)))
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException &&
                    throwable.getMessage().contains("La contraseña es requerida"))
                .verify();
    }

    @Test
    @DisplayName("Should fail validation when password is whitespace")
    void shouldFailValidationWhenPasswordIsWhitespace() {
        // Given
        User invalidUser = validUser.toBuilder().password("   ").build();

        // When & Then
        StepVerifier.create(userUseCase.saveUser(Mono.just(invalidUser)))
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException &&
                    throwable.getMessage().contains("La contraseña es requerida"))
                .verify();
    }

    @Test
    @DisplayName("Should fail validation when name is empty")
    void shouldFailValidationWhenNameIsEmpty() {
        // Given
        User invalidUser = validUser.toBuilder().name("").build();

        // When & Then
        StepVerifier.create(userUseCase.saveUser(Mono.just(invalidUser)))
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException &&
                    throwable.getMessage().contains("El nombre es requerido"))
                .verify();
    }

    @Test
    @DisplayName("Should fail validation when name is whitespace")
    void shouldFailValidationWhenNameIsWhitespace() {
        // Given
        User invalidUser = validUser.toBuilder().name("   ").build();

        // When & Then
        StepVerifier.create(userUseCase.saveUser(Mono.just(invalidUser)))
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException &&
                    throwable.getMessage().contains("El nombre es requerido"))
                .verify();
    }

    @Test
    @DisplayName("Should fail validation in editUser when idNumber is null")
    void shouldFailValidationInEditUserWhenIdNumberIsNull() {
        // Given
        User invalidUser = validUser.toBuilder().idNumber(null).build();

        // When & Then
        StepVerifier.create(userUseCase.editUser(Mono.just(invalidUser)))
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException &&
                    throwable.getMessage().contains("El número de identificación es requerido"))
                .verify();
    }

    @Test
    @DisplayName("Should fail when both email and idNumber already exist in CREATE mode")
    void shouldFailWhenBothEmailAndIdNumberAlreadyExistInCreateMode() {
        // Given
        User existingUser = validUser.toBuilder()
                .email("john.doe@example.com")  // mismo email
                .idNumber(12345678L)            // mismo idNumber
                .build();
        
        when(userRepository.findByEmailOrIdNumber(any(), any())).thenReturn(Mono.just(existingUser));
        when(idTypeRepository.getIdTypeById(1)).thenReturn(Mono.just(validIdType));
        when(roleRepository.getRoleById(1)).thenReturn(Mono.just(validRole));

        // When & Then
        StepVerifier.create(userUseCase.saveUser(Mono.just(validUser)))
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException &&
                    throwable.getMessage().contains("El correo electrónico y el número de identificación ya han sido registrados por otro usuario"))
                .verify();
    }

    @Test
    @DisplayName("Should fail when trying to change both email and idNumber in UPDATE mode")
    void shouldFailWhenTryingToChangeBothEmailAndIdNumberInUpdateMode() {
        // Given
        User existingUser = validUser.toBuilder()
                .email("different@example.com")  // email diferente
                .idNumber(87654321L)             // idNumber diferente
                .build();
        
        when(userRepository.findByEmailOrIdNumber(any(), any())).thenReturn(Mono.just(existingUser));
        when(idTypeRepository.getIdTypeById(1)).thenReturn(Mono.just(validIdType));
        when(roleRepository.getRoleById(1)).thenReturn(Mono.just(validRole));

        // When & Then
        StepVerifier.create(userUseCase.editUser(Mono.just(validUser)))
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException &&
                    throwable.getMessage().contains("No se pueden cambiar el correo electrónico ni el número de identificación que ya han sido registrados por el usuario"))
                .verify();
    }

    @Test
    @DisplayName("Should fail when only email already exists in CREATE mode")
    void shouldFailWhenOnlyEmailAlreadyExistsInCreateMode() {
        // Given
        User existingUser = validUser.toBuilder()
                .email("john.doe@example.com")  // mismo email
                .idNumber(87654321L)            // idNumber diferente
                .build();
        
        when(userRepository.findByEmailOrIdNumber(any(), any())).thenReturn(Mono.just(existingUser));
        when(idTypeRepository.getIdTypeById(1)).thenReturn(Mono.just(validIdType));
        when(roleRepository.getRoleById(1)).thenReturn(Mono.just(validRole));

        // When & Then
        StepVerifier.create(userUseCase.saveUser(Mono.just(validUser)))
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException &&
                    throwable.getMessage().contains("El correo electrónico ya ha sido registrado por otro usuario"))
                .verify();
    }

    @Test
    @DisplayName("Should fail when only idNumber already exists in CREATE mode")
    void shouldFailWhenOnlyIdNumberAlreadyExistsInCreateMode() {
        // Given
        User existingUser = validUser.toBuilder()
                .email("different@example.com")  // email diferente
                .idNumber(12345678L)             // mismo idNumber
                .build();
        
        when(userRepository.findByEmailOrIdNumber(any(), any())).thenReturn(Mono.just(existingUser));
        when(idTypeRepository.getIdTypeById(1)).thenReturn(Mono.just(validIdType));
        when(roleRepository.getRoleById(1)).thenReturn(Mono.just(validRole));

        // When & Then
        StepVerifier.create(userUseCase.saveUser(Mono.just(validUser)))
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException &&
                    throwable.getMessage().contains("El número de identificación ya ha sido registrado por otro usuario"))
                .verify();
    }

    @Test
    @DisplayName("Should fail when trying to change email in UPDATE mode")
    void shouldFailWhenTryingToChangeEmailInUpdateMode() {
        // Given
        User existingUser = validUser.toBuilder()
                .email("different@example.com")  // email diferente
                .idNumber(12345678L)             // mismo idNumber
                .build();
        
        when(userRepository.findByEmailOrIdNumber(any(), any())).thenReturn(Mono.just(existingUser));
        when(idTypeRepository.getIdTypeById(1)).thenReturn(Mono.just(validIdType));
        when(roleRepository.getRoleById(1)).thenReturn(Mono.just(validRole));

        // When & Then
        StepVerifier.create(userUseCase.editUser(Mono.just(validUser)))
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException &&
                    throwable.getMessage().contains("No se puede cambiar el correo electrónico registrado por el usuario"))
                .verify();
    }

    @Test
    @DisplayName("Should fail when trying to change idNumber in UPDATE mode")
    void shouldFailWhenTryingToChangeIdNumberInUpdateMode() {
        // Given
        User existingUser = validUser.toBuilder()
                .email("john.doe@example.com")  // mismo email
                .idNumber(87654321L)            // idNumber diferente
                .build();
        
        when(userRepository.findByEmailOrIdNumber(any(), any())).thenReturn(Mono.just(existingUser));
        when(idTypeRepository.getIdTypeById(1)).thenReturn(Mono.just(validIdType));
        when(roleRepository.getRoleById(1)).thenReturn(Mono.just(validRole));

        // When & Then
        StepVerifier.create(userUseCase.editUser(Mono.just(validUser)))
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException &&
                    throwable.getMessage().contains("No se puede cambiar el número de identificación registrado por el usuario"))
                .verify();
    }

    @Test
    @DisplayName("Should succeed when user data matches existing user in UPDATE mode")
    void shouldSucceedWhenUserDataMatchesExistingUserInUpdateMode() {
        // Given
        User existingUser = validUser.toBuilder()
                .email("john.doe@example.com")  // mismo email
                .idNumber(12345678L)            // mismo idNumber
                .build();
        
        when(userRepository.findByEmailOrIdNumber(any(), any())).thenReturn(Mono.just(existingUser));
        when(idTypeRepository.getIdTypeById(1)).thenReturn(Mono.just(validIdType));
        when(roleRepository.getRoleById(1)).thenReturn(Mono.just(validRole));
        when(userRepository.editUser(any())).thenReturn(Mono.just(validUser));

        // When & Then
        StepVerifier.create(userUseCase.editUser(Mono.just(validUser)))
                .expectNextMatches(userRecord -> 
                    userRecord.name().equals("John") &&
                    userRecord.email().equals("john.doe@example.com"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should succeed when no existing user found in CREATE mode")
    void shouldSucceedWhenNoExistingUserFoundInCreateMode() {
        // Given
        when(userRepository.findByEmailOrIdNumber(any(), any())).thenReturn(Mono.empty());
        when(idTypeRepository.getIdTypeById(1)).thenReturn(Mono.just(validIdType));
        when(roleRepository.getRoleById(1)).thenReturn(Mono.just(validRole));
        when(userRepository.saveUser(any())).thenReturn(Mono.just(validUser));

        // When & Then
        StepVerifier.create(userUseCase.saveUser(Mono.just(validUser)))
                .expectNextMatches(userRecord -> 
                    userRecord.name().equals("John") &&
                    userRecord.email().equals("john.doe@example.com"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should fail validation when salary is below minimum")
    void shouldFailValidationWhenSalaryIsBelowMinimum() {
        // Given
        User invalidUser = validUser.toBuilder().baseSalary(-1.0).build();

        // When & Then
        StepVerifier.create(userUseCase.saveUser(Mono.just(invalidUser)))
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException &&
                    throwable.getMessage().contains("El salario base debe estar entre"))
                .verify();
    }

    @Test
    @DisplayName("Should fail validation when salary is exactly minimum boundary")
    void shouldFailValidationWhenSalaryIsExactlyMinimumBoundary() {
        // Given - Usar un valor menor que MIN_SALARY (0.0)
        User invalidUser = validUser.toBuilder().baseSalary(-0.01).build();

        // When & Then
        StepVerifier.create(userUseCase.saveUser(Mono.just(invalidUser)))
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException &&
                    throwable.getMessage().contains("El salario base debe estar entre"))
                .verify();
    }

    @Test
    @DisplayName("Should fail validation when salary is above maximum")
    void shouldFailValidationWhenSalaryIsAboveMaximum() {
        // Given - Usar un valor mayor que MAX_SALARY (15000000.0)
        User invalidUser = validUser.toBuilder().baseSalary(15000001.0).build();

        // When & Then
        StepVerifier.create(userUseCase.saveUser(Mono.just(invalidUser)))
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException &&
                    throwable.getMessage().contains("El salario base debe estar entre"))
                .verify();
    }

    @Test
    @DisplayName("Should pass validation when salary is at minimum boundary")
    void shouldPassValidationWhenSalaryIsAtMinimumBoundary() {
        // Given
        User validUserWithMinSalary = validUser.toBuilder().baseSalary(0.0).build();
        
        when(userRepository.findByEmailOrIdNumber(any(), any())).thenReturn(Mono.empty());
        when(idTypeRepository.getIdTypeById(1)).thenReturn(Mono.just(validIdType));
        when(roleRepository.getRoleById(1)).thenReturn(Mono.just(validRole));
        when(userRepository.saveUser(any())).thenReturn(Mono.just(validUserWithMinSalary));

        // When & Then
        StepVerifier.create(userUseCase.saveUser(Mono.just(validUserWithMinSalary)))
                .expectNextMatches(userRecord -> userRecord.baseSalary().equals(0.0))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should pass validation when salary is at maximum boundary")
    void shouldPassValidationWhenSalaryIsAtMaximumBoundary() {
        // Given
        User validUserWithMaxSalary = validUser.toBuilder().baseSalary(15000000.0).build();
        
        when(userRepository.findByEmailOrIdNumber(any(), any())).thenReturn(Mono.empty());
        when(idTypeRepository.getIdTypeById(1)).thenReturn(Mono.just(validIdType));
        when(roleRepository.getRoleById(1)).thenReturn(Mono.just(validRole));
        when(userRepository.saveUser(any())).thenReturn(Mono.just(validUserWithMaxSalary));

        // When & Then
        StepVerifier.create(userUseCase.saveUser(Mono.just(validUserWithMaxSalary)))
                .expectNextMatches(userRecord -> userRecord.baseSalary().equals(15000000.0))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return user when no existing user found in CREATE mode")
    void shouldReturnUserWhenNoExistingUserFoundInCreateMode() {
        // Given
        when(userRepository.findByEmailOrIdNumber(any(), any())).thenReturn(Mono.empty());
        when(idTypeRepository.getIdTypeById(1)).thenReturn(Mono.just(validIdType));
        when(roleRepository.getRoleById(1)).thenReturn(Mono.just(validRole));
        when(userRepository.saveUser(any())).thenReturn(Mono.just(validUser));

        // When & Then
        StepVerifier.create(userUseCase.saveUser(Mono.just(validUser)))
                .expectNextMatches(userRecord -> 
                    userRecord.name().equals("John") &&
                    userRecord.email().equals("john.doe@example.com"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should fail when no existing user found in UPDATE mode")
    void shouldFailWhenNoExistingUserFoundInUpdateMode() {
        when(userRepository.findByEmailOrIdNumber(any(), any())).thenReturn(Mono.empty());
        when(idTypeRepository.getIdTypeById(1)).thenReturn(Mono.just(validIdType));
        when(roleRepository.getRoleById(1)).thenReturn(Mono.just(validRole));

        StepVerifier.create(userUseCase.editUser(Mono.just(validUser)))
            .expectErrorMatches(t ->
                t instanceof InvalidUserDataException &&
                t.getMessage().contains("No existe un usuario con los datos proporcionados"))
            .verify();
    }

    @Test
    @DisplayName("Should edit user successfully without changing password")
    void shouldEditUserWithoutChangingPassword() {
        User req = validUser.toBuilder().password(null).build(); // no viene nueva contraseña

        when(userRepository.findByEmailOrIdNumber(any(), any())).thenReturn(Mono.just(validUser)); // existing
        when(idTypeRepository.getIdTypeById(1)).thenReturn(Mono.just(validIdType));
        when(roleRepository.getRoleById(1)).thenReturn(Mono.just(validRole));
        when(userRepository.editUser(any())).thenReturn(Mono.just(validUser));

        StepVerifier.create(userUseCase.editUser(Mono.just(req)))
            .expectNextMatches(ur -> 
                ur.name().equals("John") &&
                ur.email().equals("john.doe@example.com"))
            .verifyComplete();
    }

    @Test
    @DisplayName("Should return user when validation passes in UPDATE mode")
    void shouldReturnUserWhenValidationPassesInUpdateMode() {
        // Given
        User existingUser = validUser.toBuilder()
                .email("john.doe@example.com")  // mismo email
                .idNumber(12345678L)            // mismo idNumber
                .build();
        
        when(userRepository.findByEmailOrIdNumber(any(), any())).thenReturn(Mono.just(existingUser));
        when(idTypeRepository.getIdTypeById(1)).thenReturn(Mono.just(validIdType));
        when(roleRepository.getRoleById(1)).thenReturn(Mono.just(validRole));
        when(userRepository.editUser(any())).thenReturn(Mono.just(validUser));

        // When & Then
        StepVerifier.create(userUseCase.editUser(Mono.just(validUser)))
                .expectNextMatches(userRecord -> 
                    userRecord.name().equals("John") &&
                    userRecord.email().equals("john.doe@example.com"))
                .verifyComplete();
    }

}