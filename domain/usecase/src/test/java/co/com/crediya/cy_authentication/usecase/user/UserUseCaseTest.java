package co.com.crediya.cy_authentication.usecase.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.com.crediya.cy_authentication.exception.InvalidUserDataException;
import co.com.crediya.cy_authentication.model.user.User;
import co.com.crediya.cy_authentication.model.user.gateways.UserRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
public class UserUseCaseTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserUseCase userUseCase;

    private User validUser;

    @BeforeEach
    void setUp() {
        validUser = User.builder()
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
    }

    // Tests para saveUser
    @Test
    void saveUser_withValidUser_shouldSaveSuccessfully() {
        // Arrange
        when(userRepository.findByEmailOrIdNumber(anyString(), anyLong()))
                .thenReturn(Flux.empty());
        when(userRepository.saveUser(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Mono<User> result = userUseCase.saveUser(Mono.just(validUser));

        // Assert
        StepVerifier.create(result)
                .expectNext(validUser)
                .verifyComplete();
        
        verify(userRepository).findByEmailOrIdNumber(validUser.getEmail(), validUser.getIdNumber());
        verify(userRepository).saveUser(any(Mono.class));
    }

    @Test
    void saveUser_withNullIdNumber_shouldReturnError() {
        // Arrange
        User invalidUser = validUser.toBuilder()
                .idNumber(null)
                .build();

        // Act
        Mono<User> result = userUseCase.saveUser(Mono.just(invalidUser));

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException && 
                    throwable.getMessage().contains("identificación es requerido"))
                .verify();
    }

    @Test
    void saveUser_withZeroIdNumber_shouldReturnError() {
        // Arrange
        User invalidUser = validUser.toBuilder()
                .idNumber(0L)
                .build();

        // Act
        Mono<User> result = userUseCase.saveUser(Mono.just(invalidUser));

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException && 
                    throwable.getMessage().contains("identificación es requerido"))
                .verify();
    }

    @Test
    void saveUser_withNullName_shouldReturnError() {
        // Arrange
        User invalidUser = validUser.toBuilder()
                .name(null)
                .build();

        // Act
        Mono<User> result = userUseCase.saveUser(Mono.just(invalidUser));

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException && 
                    throwable.getMessage().contains("nombre es requerido"))
                .verify();
    }

    @Test
    void saveUser_withEmptyName_shouldReturnError() {
        // Arrange
        User invalidUser = validUser.toBuilder()
                .name("")
                .build();

        // Act
        Mono<User> result = userUseCase.saveUser(Mono.just(invalidUser));

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException && 
                    throwable.getMessage().contains("nombre es requerido"))
                .verify();
    }

    @Test
    void saveUser_withNullLastname_shouldReturnError() {
        // Arrange
        User invalidUser = validUser.toBuilder()
                .lastname(null)
                .build();

        // Act
        Mono<User> result = userUseCase.saveUser(Mono.just(invalidUser));

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException && 
                    throwable.getMessage().contains("apellido es requerido"))
                .verify();
    }

    @Test
    void saveUser_withEmptyLastname_shouldReturnError() {
        // Arrange
        User invalidUser = validUser.toBuilder()
                .lastname("")
                .build();

        // Act
        Mono<User> result = userUseCase.saveUser(Mono.just(invalidUser));

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException && 
                    throwable.getMessage().contains("apellido es requerido"))
                .verify();
    }

    @Test
    void saveUser_withNullEmail_shouldReturnError() {
        // Arrange
        User invalidUser = validUser.toBuilder()
                .email(null)
                .build();

        // Act
        Mono<User> result = userUseCase.saveUser(Mono.just(invalidUser));

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException && 
                    throwable.getMessage().contains("correo electrónico es requerido"))
                .verify();
    }

    @Test
    void saveUser_withEmptyEmail_shouldReturnError() {
        // Arrange
        User invalidUser = validUser.toBuilder()
                .email("")
                .build();

        // Act
        Mono<User> result = userUseCase.saveUser(Mono.just(invalidUser));

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException && 
                    throwable.getMessage().contains("correo electrónico es requerido"))
                .verify();
    }

    @Test
    void saveUser_withInvalidEmailFormat_shouldReturnError() {
        // Arrange
        User invalidUser = validUser.toBuilder()
                .email("invalid-email")
                .build();

        // Act
        Mono<User> result = userUseCase.saveUser(Mono.just(invalidUser));

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException && 
                    throwable.getMessage().contains("formato del correo electrónico"))
                .verify();
    }

    @Test
    void saveUser_withNullBaseSalary_shouldReturnError() {
        // Arrange
        User invalidUser = validUser.toBuilder()
                .baseSalary(null)
                .build();

        // Act
        Mono<User> result = userUseCase.saveUser(Mono.just(invalidUser));

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException && 
                    throwable.getMessage().contains("salario base es requerido"))
                .verify();
    }

    @Test
    void saveUser_withNegativeBaseSalary_shouldReturnError() {
        // Arrange
        User invalidUser = validUser.toBuilder()
                .baseSalary(-1.0)
                .build();

        // Act
        Mono<User> result = userUseCase.saveUser(Mono.just(invalidUser));

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException && 
                    throwable.getMessage().contains("salario base debe estar entre"))
                .verify();
    }

    @Test
    void saveUser_withTooHighBaseSalary_shouldReturnError() {
        // Arrange
        User invalidUser = validUser.toBuilder()
                .baseSalary(20000000.0) // Above MAX_SALARY
                .build();

        // Act
        Mono<User> result = userUseCase.saveUser(Mono.just(invalidUser));

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException && 
                    throwable.getMessage().contains("salario base debe estar entre"))
                .verify();
    }

    @Test
    void saveUser_withNullUsername_shouldReturnError() {
        // Arrange
        User invalidUser = validUser.toBuilder()
                .username(null)
                .build();

        // Act
        Mono<User> result = userUseCase.saveUser(Mono.just(invalidUser));

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException && 
                    throwable.getMessage().contains("username es requerido"))
                .verify();
    }

    @Test
    void saveUser_withEmptyUsername_shouldReturnError() {
        // Arrange
        User invalidUser = validUser.toBuilder()
                .username("")
                .build();

        // Act
        Mono<User> result = userUseCase.saveUser(Mono.just(invalidUser));

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException && 
                    throwable.getMessage().contains("username es requerido"))
                .verify();
    }

    @Test
    void saveUser_withNullPassword_shouldReturnError() {
        // Arrange
        User invalidUser = validUser.toBuilder()
                .password(null)
                .build();

        // Act
        Mono<User> result = userUseCase.saveUser(Mono.just(invalidUser));

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException && 
                    throwable.getMessage().contains("contraseña es requerida"))
                .verify();
    }

    @Test
    void saveUser_withEmptyPassword_shouldReturnError() {
        // Arrange
        User invalidUser = validUser.toBuilder()
                .password("")
                .build();

        // Act
        Mono<User> result = userUseCase.saveUser(Mono.just(invalidUser));

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException && 
                    throwable.getMessage().contains("contraseña es requerida"))
                .verify();
    }

    @Test
    void saveUser_withExistingEmail_shouldReturnError() {
        // Arrange
        User existingUser = validUser.toBuilder()
                .idNumber(987654321L) // Different ID
                .build();
        
        when(userRepository.findByEmailOrIdNumber(anyString(), anyLong()))
                .thenReturn(Flux.just(existingUser));

        // Act
        Mono<User> result = userUseCase.saveUser(Mono.just(validUser));

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException && 
                    throwable.getMessage().contains("correo electrónico ya está registrado"))
                .verify();
    }

    @Test
    void saveUser_withExistingIdNumber_shouldReturnError() {
        // Arrange
        User existingUser = validUser.toBuilder()
                .email("another@example.com") // Different email
                .build();
        
        when(userRepository.findByEmailOrIdNumber(anyString(), anyLong()))
                .thenReturn(Flux.just(existingUser));

        // Act
        Mono<User> result = userUseCase.saveUser(Mono.just(validUser));

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException && 
                    throwable.getMessage().contains("número de identificación ya está registrado"))
                .verify();
    }

    // Tests para getAllUsers
    @Test
    void getAllUsers_shouldReturnAllUsers() {
        // Arrange
        User anotherUser = validUser.toBuilder()
                .idNumber(987654321L)
                .email("jane.doe@example.com")
                .build();
        
        when(userRepository.getAllUsers())
                .thenReturn(Flux.just(validUser, anotherUser));

        // Act
        Flux<User> result = userUseCase.getAllUsers();

        // Assert
        StepVerifier.create(result)
                .expectNext(validUser)
                .expectNext(anotherUser)
                .verifyComplete();
    }

    // Tests para getByIdNumber
    @Test
    void getByIdNumber_shouldReturnUser() {
        // Arrange
        when(userRepository.getByIdNumber(anyLong()))
                .thenReturn(Mono.just(validUser));

        // Act
        Mono<User> result = userUseCase.getByIdNumber(123456789L);

        // Assert
        StepVerifier.create(result)
                .expectNext(validUser)
                .verifyComplete();
    }

    // Tests para editUser
    @Test
    void editUser_withValidUser_shouldUpdateSuccessfully() {
        // Arrange
        User updatedUser = validUser.toBuilder()
                .address("456 New St")
                .phone("9876543210")
                .build();
        
        when(userRepository.findByEmailOrIdNumber(anyString(), anyLong()))
                .thenReturn(Flux.just(validUser)); // Existing user with same ID and email
        
        when(userRepository.editUser(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Mono<User> result = userUseCase.editUser(Mono.just(updatedUser));

        // Assert
        StepVerifier.create(result)
                .expectNext(updatedUser)
                .verifyComplete();
    }

    @Test
    void editUser_withInvalidData_shouldReturnError() {
        // Arrange
        User invalidUser = validUser.toBuilder()
                .name("")
                .build();

        // Act
        Mono<User> result = userUseCase.editUser(Mono.just(invalidUser));

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException && 
                    throwable.getMessage().contains("nombre es requerido"))
                .verify();
    }

    @Test
    void editUser_withEmailAlreadyInUseByAnotherUser_shouldReturnError() {
        // Arrange
        User updatedUser = validUser.toBuilder()
                .email("new.email@example.com")
                .build();
        
        User existingUser = User.builder()
                .idNumber(987654321L) // Different ID
                .email("new.email@example.com") // Same email as the updated user
                .name("Jane")
                .lastname("Doe")
                .baseSalary(5000.0)
                .username("janedoe")
                .password("password123")
                .build();
        
        when(userRepository.findByEmailOrIdNumber(anyString(), anyLong()))
                .thenReturn(Flux.just(existingUser));

        // Act
        Mono<User> result = userUseCase.editUser(Mono.just(updatedUser));

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException && 
                    throwable.getMessage().contains("correo electrónico ya está registrado"))
                .verify();
    }

    @Test
    void editUser_withIdNumberAlreadyInUseByAnotherUser_shouldReturnError() {
        // Arrange
        User updatedUser = validUser.toBuilder()
                .idNumber(987654321L) // Changed ID
                .build();
        
        User existingUser = User.builder()
                .idNumber(987654321L) // Same ID as the updated user
                .email("jane.doe@example.com") // Different email
                .name("Jane")
                .lastname("Doe")
                .baseSalary(5000.0)
                .username("janedoe")
                .password("password123")
                .build();
        
        when(userRepository.findByEmailOrIdNumber(anyString(), anyLong()))
                .thenReturn(Flux.just(existingUser));

        // Act
        Mono<User> result = userUseCase.editUser(Mono.just(updatedUser));

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable -> 
                    throwable instanceof InvalidUserDataException && 
                    throwable.getMessage().contains("número de identificación ya está registrado"))
                .verify();
    }

    @Test
    void editUser_whenNoConflictsExist_shouldUpdateSuccessfully() {
        // Arrange
        User updatedUser = validUser.toBuilder()
                .address("New Address")
                .phone("9876543210")
                .build();
        
        // No conflicts (empty result from findByEmailOrIdNumber)
        when(userRepository.findByEmailOrIdNumber(anyString(), anyLong()))
                .thenReturn(Flux.empty());
        
        when(userRepository.editUser(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Mono<User> result = userUseCase.editUser(Mono.just(updatedUser));

        // Assert
        StepVerifier.create(result)
                .expectNext(updatedUser)
                .verifyComplete();
    }

    // Tests para deleteUser
    @Test
    void deleteUser_shouldDeleteSuccessfully() {
        // Arrange
        when(userRepository.deleteUser(anyLong()))
                .thenReturn(Mono.empty());

        // Act
        Mono<Void> result = userUseCase.deleteUser(123456789L);

        // Assert
        StepVerifier.create(result)
                .verifyComplete();
        
        verify(userRepository).deleteUser(123456789L);
    }

    // Tests adicionales para validaciones específicas
    @Test
    void validateUserUniqueness_whenUpdatingWithSameIdAndEmail_shouldSucceed() {
        // Arrange
        // Existing user with same ID and email (this is the user being updated)
        when(userRepository.findByEmailOrIdNumber(eq(validUser.getEmail()), eq(validUser.getIdNumber())))
                .thenReturn(Flux.just(validUser));
        
        when(userRepository.editUser(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Mono<User> result = userUseCase.editUser(Mono.just(validUser));

        // Assert
        StepVerifier.create(result)
                .expectNext(validUser)
                .verifyComplete();
    }

    @Test
    void validateUserUniqueness_whenNoConflictsExist_shouldSucceed() {
        // Arrange
        when(userRepository.findByEmailOrIdNumber(anyString(), anyLong()))
                .thenReturn(Flux.empty());
        
        when(userRepository.saveUser(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Mono<User> result = userUseCase.saveUser(Mono.just(validUser));

        // Assert
        StepVerifier.create(result)
                .expectNext(validUser)
                .verifyComplete();
    }

    @Test
    void validateUserData_withAllValidFields_shouldReturnUser() {
        // Arrange
        when(userRepository.findByEmailOrIdNumber(anyString(), anyLong()))
                .thenReturn(Flux.empty());
        
        when(userRepository.saveUser(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Mono<User> result = userUseCase.saveUser(Mono.just(validUser));

        // Assert
        StepVerifier.create(result)
                .expectNext(validUser)
                .verifyComplete();
    }

    @Test
    void validateUserData_withBorderlineSalary_shouldSucceed() {
        // Arrange
        User borderlineUser = validUser.toBuilder()
                .baseSalary(15000000.0) // Exactly MAX_SALARY
                .build();
        
        when(userRepository.findByEmailOrIdNumber(anyString(), anyLong()))
                .thenReturn(Flux.empty());
        
        when(userRepository.saveUser(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Mono<User> result = userUseCase.saveUser(Mono.just(borderlineUser));

        // Assert
        StepVerifier.create(result)
                .expectNext(borderlineUser)
                .verifyComplete();
    }

    @Test
    void validateUserData_withZeroSalary_shouldSucceed() {
        // Arrange
        User zeroSalaryUser = validUser.toBuilder()
                .baseSalary(0.0) // Exactly MIN_SALARY
                .build();
        
        when(userRepository.findByEmailOrIdNumber(anyString(), anyLong()))
                .thenReturn(Flux.empty());
        
        when(userRepository.saveUser(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Mono<User> result = userUseCase.saveUser(Mono.just(zeroSalaryUser));

        // Assert
        StepVerifier.create(result)
                .expectNext(zeroSalaryUser)
                .verifyComplete();
    }

    @Test
    void validateUserData_withValidEmail_shouldSucceed() {
        // Arrange
        User[] validEmailUsers = {
            validUser.toBuilder().email("test@example.com").build(),
            validUser.toBuilder().email("test.user@example.co.uk").build(),
            validUser.toBuilder().email("test_user123@example-domain.com").build(),
            validUser.toBuilder().email("test+user@example.org").build()
        };
        
        when(userRepository.findByEmailOrIdNumber(anyString(), anyLong()))
                .thenReturn(Flux.empty());
        
        when(userRepository.saveUser(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act & Assert
        for (User user : validEmailUsers) {
            StepVerifier.create(userUseCase.saveUser(Mono.just(user)))
                    .expectNext(user)
                    .verifyComplete();
        }
    }
}
