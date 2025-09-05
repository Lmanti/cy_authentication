package co.com.crediya.cy_authentication.r2dbc;

import co.com.crediya.cy_authentication.exception.DataPersistenceException;
import co.com.crediya.cy_authentication.exception.DataRetrievalException;
import co.com.crediya.cy_authentication.exception.UserNotFoundException;
import co.com.crediya.cy_authentication.model.user.User;
import co.com.crediya.cy_authentication.r2dbc.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigInteger;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsersReactiveRepositoryAdapterTest {

    @Mock
    private UserReactiveRepository repository;
    
    @Mock
    private ObjectMapper mapper;

    private UserReactiveRepositoryAdapter adapter;

    private User validUser;
    private UserEntity validUserEntity;

    @BeforeEach
    void setUp() {
        adapter = new UserReactiveRepositoryAdapter(repository, mapper);

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

        validUserEntity = new UserEntity();
        validUserEntity.setId(BigInteger.valueOf(1));
        validUserEntity.setIdNumber(12345678L);
        validUserEntity.setIdTypeId(1);
        validUserEntity.setName("John");
        validUserEntity.setLastname("Doe");
        validUserEntity.setBirthDate(LocalDate.of(1990, 1, 1));
        validUserEntity.setEmail("john.doe@example.com");
        validUserEntity.setBaseSalary(3000000.0);
        validUserEntity.setRoleId(1);
        validUserEntity.setPassword("password123");
    }

    @Test
    @DisplayName("Should save user successfully")
    void shouldSaveUserSuccessfully() {
        // Given
        when(mapper.map(any(User.class), any())).thenReturn(validUserEntity);
        when(mapper.map(any(UserEntity.class), any())).thenReturn(validUser);
        when(repository.save(any(UserEntity.class))).thenReturn(Mono.just(validUserEntity));

        // When & Then
        StepVerifier.create(adapter.saveUser(Mono.just(validUser)))
                .expectNext(validUser)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle save user error")
    void shouldHandleSaveUserError() {
        // Given
        when(mapper.map(any(User.class), any())).thenReturn(validUserEntity);
        when(repository.save(any(UserEntity.class))).thenReturn(Mono.error(new RuntimeException("Database error")));

        // When & Then
        StepVerifier.create(adapter.saveUser(Mono.just(validUser)))
                .expectError(DataPersistenceException.class)
                .verify();
    }

    @Test
    @DisplayName("Should get all users successfully")
    void shouldGetAllUsersSuccessfully() {
        // Given
        when(repository.findAll()).thenReturn(Flux.just(validUserEntity));
        when(mapper.map(any(UserEntity.class), any())).thenReturn(validUser);

        // When & Then
        StepVerifier.create(adapter.getAllUsers())
                .expectNext(validUser)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle get all users error")
    void shouldHandleGetAllUsersError() {
        // Given
        when(repository.findAll()).thenReturn(Flux.error(new RuntimeException("Database error")));

        // When & Then
        StepVerifier.create(adapter.getAllUsers())
                .expectError(DataRetrievalException.class)
                .verify();
    }

    @Test
    @DisplayName("Should get user by id number successfully")
    void shouldGetUserByIdNumberSuccessfully() {
        // Given
        when(repository.findByIdNumber(12345678L)).thenReturn(Mono.just(validUserEntity));
        when(mapper.map(any(UserEntity.class), any())).thenReturn(validUser);

        // When & Then
        StepVerifier.create(adapter.getByIdNumber(12345678L))
                .expectNext(validUser)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user not found by id number")
    void shouldThrowUserNotFoundExceptionWhenUserNotFoundByIdNumber() {
        // Given
        when(repository.findByIdNumber(999L)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(adapter.getByIdNumber(999L))
                .expectError(UserNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("Should edit user successfully")
    void shouldEditUserSuccessfully() {
        // Given
        when(repository.findByIdNumber(12345678L)).thenReturn(Mono.just(validUserEntity));
        when(repository.save(any(UserEntity.class))).thenReturn(Mono.just(validUserEntity));
        when(mapper.map(any(UserEntity.class), any())).thenReturn(validUser);

        // When & Then
        StepVerifier.create(adapter.editUser(Mono.just(validUser)))
                .expectNext(validUser)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when editing non-existent user")
    void shouldThrowUserNotFoundExceptionWhenEditingNonExistentUser() {
        // Given
        when(repository.findByIdNumber(999L)).thenReturn(Mono.empty());

        User nonExistentUser = validUser.toBuilder().idNumber(999L).build();

        // When & Then
        StepVerifier.create(adapter.editUser(Mono.just(nonExistentUser)))
                .expectError(UserNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUserSuccessfully() {
        // Given
        when(repository.findByIdNumber(12345678L)).thenReturn(Mono.just(validUserEntity));
        when(repository.deleteById(BigInteger.valueOf(1))).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(adapter.deleteUser(12345678L))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when deleting non-existent user")
    void shouldThrowUserNotFoundExceptionWhenDeletingNonExistentUser() {
        // Given
        when(repository.findByIdNumber(999L)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(adapter.deleteUser(999L))
                .expectError(UserNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("Should get user by email successfully")
    void shouldGetUserByEmailSuccessfully() {
        // Given
        when(repository.findByEmail("john.doe@example.com")).thenReturn(Mono.just(validUserEntity));
        when(mapper.map(any(UserEntity.class), any())).thenReturn(validUser);

        // When & Then
        StepVerifier.create(adapter.getByEmail("john.doe@example.com"))
                .expectNext(validUser)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user not found by email")
    void shouldThrowUserNotFoundExceptionWhenUserNotFoundByEmail() {
        // Given
        when(repository.findByEmail("notfound@example.com")).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(adapter.getByEmail("notfound@example.com"))
                .expectError(UserNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("Should find user by email or id number successfully")
    void shouldFindUserByEmailOrIdNumberSuccessfully() {
        // Given
        when(repository.findByEmailOrIdNumber("john.doe@example.com", 12345678L))
                .thenReturn(Mono.just(validUserEntity));
        when(mapper.map(any(UserEntity.class), any())).thenReturn(validUser);

        // When & Then
        StepVerifier.create(adapter.findByEmailOrIdNumber("john.doe@example.com", 12345678L))
                .expectNext(validUser)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return empty when no user found by email or id number")
    void shouldReturnEmptyWhenNoUserFoundByEmailOrIdNumber() {
        // Given
        when(repository.findByEmailOrIdNumber("notfound@example.com", 999L))
                .thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(adapter.findByEmailOrIdNumber("notfound@example.com", 999L))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return empty when both email and id number are null")
    void shouldReturnEmptyWhenBothEmailAndIdNumberAreNull() {
        // When & Then
        StepVerifier.create(adapter.findByEmailOrIdNumber(null, null))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return empty when email is empty and id number is null")
    void shouldReturnEmptyWhenEmailIsEmptyAndIdNumberIsNull() {
        // When & Then
        StepVerifier.create(adapter.findByEmailOrIdNumber("", null))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle database error in findByEmailOrIdNumber")
    void shouldHandleDatabaseErrorInFindByEmailOrIdNumber() {
        // Given
        when(repository.findByEmailOrIdNumber(anyString(), anyLong()))
                .thenReturn(Mono.error(new RuntimeException("Database error")));

        // When & Then
        StepVerifier.create(adapter.findByEmailOrIdNumber("test@example.com", 123L))
                .expectError(DataRetrievalException.class)
                .verify();
    }

    @Test
    @DisplayName("Should handle repository error in getByIdNumber")
    void shouldHandleRepositoryErrorInGetByIdNumber() {
    // Given
    when(repository.findByIdNumber(12345678L))
            .thenReturn(Mono.error(new RuntimeException("Database connection error")));

    // When & Then
    StepVerifier.create(adapter.getByIdNumber(12345678L))
            .expectError(DataRetrievalException.class)
            .verify();
    }

    @Test
    @DisplayName("Should handle repository error in editUser")
    void shouldHandleRepositoryErrorInEditUser() {
    // Given
    when(repository.findByIdNumber(12345678L)).thenReturn(Mono.just(validUserEntity));
    when(repository.save(any(UserEntity.class)))
            .thenReturn(Mono.error(new RuntimeException("Database save error")));

    // When & Then
    StepVerifier.create(adapter.editUser(Mono.just(validUser)))
            .expectError(DataPersistenceException.class)
            .verify();
    }

    @Test
    @DisplayName("Should handle repository error in deleteUser")
    void shouldHandleRepositoryErrorInDeleteUser() {
    // Given
    when(repository.findByIdNumber(12345678L)).thenReturn(Mono.just(validUserEntity));
    when(repository.deleteById(BigInteger.valueOf(1)))
            .thenReturn(Mono.error(new RuntimeException("Database delete error")));

    // When & Then
    StepVerifier.create(adapter.deleteUser(12345678L))
            .expectError(DataPersistenceException.class)
            .verify();
    }

    @Test
    @DisplayName("Should handle mapper error in editUser")
    void shouldHandleMapperErrorInEditUser() {
    // Given
    when(repository.findByIdNumber(12345678L)).thenReturn(Mono.just(validUserEntity));
    when(repository.save(any(UserEntity.class))).thenReturn(Mono.just(validUserEntity));
    when(mapper.map(any(UserEntity.class), any()))
            .thenThrow(new RuntimeException("Mapping error"));

    // When & Then
    StepVerifier.create(adapter.editUser(Mono.just(validUser)))
            .expectError(DataPersistenceException.class)
            .verify();
    }

    @Test
    @DisplayName("Should handle mapper error in getByIdNumber")
    void shouldHandleMapperErrorInGetByIdNumber() {
    // Given
    when(repository.findByIdNumber(12345678L)).thenReturn(Mono.just(validUserEntity));
    when(mapper.map(any(UserEntity.class), any()))
            .thenThrow(new RuntimeException("Mapping error"));

    // When & Then
    StepVerifier.create(adapter.getByIdNumber(12345678L))
            .expectError(DataRetrievalException.class)
            .verify();
    }

    @Test
    @DisplayName("Should handle findByIdNumber error in editUser")
    void shouldHandleFindByIdNumberErrorInEditUser() {
    // Given
    when(repository.findByIdNumber(12345678L))
            .thenReturn(Mono.error(new RuntimeException("Database find error")));

    // When & Then
    StepVerifier.create(adapter.editUser(Mono.just(validUser)))
            .expectError(DataPersistenceException.class)
            .verify();
    }

    @Test
    @DisplayName("Should handle findByIdNumber error in deleteUser")
    void shouldHandleFindByIdNumberErrorInDeleteUser() {
    // Given
    when(repository.findByIdNumber(12345678L))
            .thenReturn(Mono.error(new RuntimeException("Database find error")));

    // When & Then
    StepVerifier.create(adapter.deleteUser(12345678L))
            .expectError(DataPersistenceException.class)
            .verify();
    }

    @Test
    @DisplayName("Should return empty when email is null and idNumber is null")
    void shouldReturnEmptyWhenEmailIsNullAndIdNumberIsNull() {
        // When & Then
        StepVerifier.create(adapter.findByEmailOrIdNumber(null, null))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should return empty when email is whitespace only and idNumber is null")
    void shouldReturnEmptyWhenEmailIsWhitespaceAndIdNumberIsNull() {
        // When & Then
        StepVerifier.create(adapter.findByEmailOrIdNumber("   ", null))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should call repository when email is valid and idNumber is null")
    void shouldCallRepositoryWhenEmailIsValidAndIdNumberIsNull() {
        // Given
        when(repository.findByEmailOrIdNumber("test@example.com", null))
                .thenReturn(Mono.just(validUserEntity));
        when(mapper.map(any(UserEntity.class), any())).thenReturn(validUser);

        // When & Then
        StepVerifier.create(adapter.findByEmailOrIdNumber("test@example.com", null))
                .expectNext(validUser)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should call repository when email is null and idNumber is valid")
    void shouldCallRepositoryWhenEmailIsNullAndIdNumberIsValid() {
        // Given
        when(repository.findByEmailOrIdNumber(null, 12345678L))
                .thenReturn(Mono.just(validUserEntity));
        when(mapper.map(any(UserEntity.class), any())).thenReturn(validUser);

        // When & Then
        StepVerifier.create(adapter.findByEmailOrIdNumber(null, 12345678L))
                .expectNext(validUser)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should call repository when email is empty and idNumber is valid")
    void shouldCallRepositoryWhenEmailIsEmptyAndIdNumberIsValid() {
        // Given
        when(repository.findByEmailOrIdNumber("", 12345678L))
                .thenReturn(Mono.just(validUserEntity));
        when(mapper.map(any(UserEntity.class), any())).thenReturn(validUser);

        // When & Then
        StepVerifier.create(adapter.findByEmailOrIdNumber("", 12345678L))
                .expectNext(validUser)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle repository error in getByEmail")
    void shouldHandleRepositoryErrorInGetByEmail() {
        // Given
        when(repository.findByEmail("john.doe@example.com"))
                .thenReturn(Mono.error(new RuntimeException("Database connection error")));

        // When & Then
        StepVerifier.create(adapter.getByEmail("john.doe@example.com"))
                .expectError(DataRetrievalException.class)
                .verify();
    }

    @Test
    @DisplayName("Should handle mapper error in getByEmail")
    void shouldHandleMapperErrorInGetByEmail() {
        // Given
        when(repository.findByEmail("john.doe@example.com")).thenReturn(Mono.just(validUserEntity));
        when(mapper.map(any(UserEntity.class), any()))
                .thenThrow(new RuntimeException("Mapping error"));

        // When & Then
        StepVerifier.create(adapter.getByEmail("john.doe@example.com"))
                .expectError(DataRetrievalException.class)
                .verify();
    }

    @Test
    @DisplayName("Should handle general exception in getByEmail")
    void shouldHandleGeneralExceptionInGetByEmail() {
        // Given
        when(repository.findByEmail("john.doe@example.com"))
                .thenReturn(Mono.error(new IllegalArgumentException("Invalid email format")));

        // When & Then
        StepVerifier.create(adapter.getByEmail("john.doe@example.com"))
                .expectError(DataRetrievalException.class)
                .verify();
    }
}