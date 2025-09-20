package co.com.crediya.cy_authentication.r2dbc;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import org.reactivecommons.utils.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;

import co.com.crediya.cy_authentication.exception.DataPersistenceException;
import co.com.crediya.cy_authentication.exception.DataRetrievalException;
import co.com.crediya.cy_authentication.exception.UserNotFoundException;
import co.com.crediya.cy_authentication.model.user.User;
import co.com.crediya.cy_authentication.model.user.gateways.UserRepository;
import co.com.crediya.cy_authentication.r2dbc.entity.UserEntity;
import co.com.crediya.cy_authentication.r2dbc.helper.ReactiveAdapterOperations;
import io.r2dbc.spi.Row;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class UserReactiveRepositoryAdapter extends ReactiveAdapterOperations<
    User,
    UserEntity,
    BigInteger,
    UserReactiveRepository
> implements UserRepository {

    private final TransactionalOperator writeTransactional;
    private final TransactionalOperator readOnlyTransactional;
    private final DatabaseClient databaseClient;

    public UserReactiveRepositoryAdapter(
        UserReactiveRepository repository,
        ObjectMapper mapper,
        TransactionalOperator transactionalOperator,
        @Qualifier("readOnlyTransactionalOperator") TransactionalOperator readOnlyTransactional,
        DatabaseClient databaseClient
    ) {
        super(repository, mapper, d -> mapper.map(d, User.class));
        this.writeTransactional = transactionalOperator;
        this.readOnlyTransactional = readOnlyTransactional;
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<User> saveUser(Mono<User> user) {
        return user
            .doOnNext(userData -> log.info("Attempting to save user: {}", userData.getIdNumber()))
            .flatMap(userData -> 
                repository.save(toData(userData))
                    .doOnNext(savedEntity -> log.debug("User entity saved: {}", savedEntity.getIdNumber()))
                    .map(this::toEntity)
                    .doOnNext(savedUser -> log.info("User successfully saved: {}", savedUser.getIdNumber()))
                    .onErrorMap(ex -> {
                        log.error("Error saving user: {}", ex.getMessage(), ex);
                        return new DataPersistenceException("Error intentando guardar el usuario", ex);
                    })
            )
            .as(writeTransactional::transactional);
    }

    @Override
    public Flux<User> getAllUsers() {
        log.info("Retrieving all users");
    
        return findAll()
            .doOnNext(user -> log.debug("Retrieved users successfully"))
            .doOnComplete(() -> log.info("Finished retrieving all users"))
            .onErrorMap(ex -> {
                log.error("Error retrieving all users", ex);
                return new DataRetrievalException("Error al momento de consultar los usuarios", ex);
            })
            .doOnError(ex -> log.error("Failed to retrieve users", ex))
            .as(readOnlyTransactional::transactional); 
    }

    @Override
    public Mono<User> getByIdNumber(Long idNumber) {
        log.info("Searching for user with ID number: {}", idNumber);
    
        return repository.findByIdNumber(idNumber)
            .doOnNext(entity -> log.debug("Found user entity with ID number {}: {}", idNumber, entity))
            .map(this::toEntity)
            .doOnNext(user -> log.info("Successfully mapped user with ID number {}: {}", idNumber))
            .switchIfEmpty(Mono.defer(() -> {
                log.warn("User with ID number {} not found", idNumber);
                return Mono.error(new UserNotFoundException("No se ha encontrado un usuario con identificación " + idNumber));
            }))
            .onErrorMap(ex -> {
                if (ex instanceof UserNotFoundException) {
                    return ex;
                }
                log.error("Error retrieving user with ID number {}: {}", idNumber, ex.getMessage(), ex);
                return new DataRetrievalException("Error consultando usuario con identificación " + idNumber, ex);
            })
            .as(readOnlyTransactional::transactional); 
    }

    @Override
    public Mono<User> getById(BigInteger id) {
        log.info("Searching for user with ID: {}", id);
    
        return findById(id)
            .doOnNext(entity -> log.debug("Found user entity with ID {}: {}", id, entity))
            .switchIfEmpty(Mono.defer(() -> {
                log.warn("User with ID {} not found", id);
                return Mono.error(new UserNotFoundException("No se ha encontrado un usuario con ID " + id));
            }))
            .onErrorMap(ex -> {
                if (ex instanceof UserNotFoundException) {
                    return ex;
                }
                log.error("Error retrieving user with ID {}: {}", id, ex.getMessage(), ex);
                return new DataRetrievalException("Error consultando usuario con ID " + id, ex);
            })
            .as(readOnlyTransactional::transactional); 
    }

    @Override
    public Mono<User> editUser(Mono<User> user) {
        return user
            .doOnNext(userData -> log.info("Attempting to update user with ID: {}", userData.getIdNumber()))
            .flatMap(userData ->
                repository.save(toData(userData))
                    .doOnNext(updatedEntity -> log.info("User entity with ID {} successfully updated", updatedEntity.getIdNumber()))
                    .map(this::toEntity)
                    .doOnNext(updatedUser -> log.info("User with ID {} successfully updated", updatedUser.getIdNumber()))
                    .onErrorMap(ex -> {
                        log.error("Error updating user: {}", ex.getMessage(), ex);
                        return new DataPersistenceException("Error intentando actualizar el usuario", ex);
                    })
            )
            .as(writeTransactional::transactional);
    }

    @Override
    public Mono<Void> deleteUser(Long idNumber) {
        log.info("Attempting to delete user with ID number: {}", idNumber);
    
        return repository.findByIdNumber(idNumber)
            .switchIfEmpty(Mono.defer(() -> {
                log.warn("User with ID number {} not found for deletion", idNumber);
                return Mono.error(new UserNotFoundException("No se ha encontrado un usuario con identificación " + idNumber));
            }))
            .flatMap(existingUser -> {
                log.info("Deleting user with ID: {}", existingUser.getId());
                return repository.deleteById(existingUser.getId());
            })
            .then()
            .doOnSuccess(v -> log.info("Successfully deleted user with ID number: {}", idNumber))
            .onErrorMap(ex -> {
                if (ex instanceof UserNotFoundException) {
                    return ex;
                }
                log.error("Error deleting user with ID number {}: {}", idNumber, ex.getMessage(), ex);
                return new DataPersistenceException("Error intentando eliminar el usuario con número de identificación " + idNumber, ex);
            })
            .as(writeTransactional::transactional);
    }

    @Override
    public Mono<User> getByEmail(String email) {
        log.info("Searching for user with email: {}", email);
    
        return repository.findByEmail(email)
            .doOnNext(entity -> log.debug("Found user entity with email {}: {}", email, entity))
            .map(this::toEntity)
            .doOnNext(user -> log.info("Successfully mapped user with email {}: {}", email))
            .switchIfEmpty(Mono.defer(() -> {
                log.warn("User with ID number {} not found", email);
                return Mono.error(new UserNotFoundException("No se ha encontrado un usuario con email " + email));
            }))
            .onErrorMap(ex -> {
                if (ex instanceof UserNotFoundException) {
                    return ex;
                }
                log.error("Error retrieving user with email {}: {}", email, ex.getMessage(), ex);
                return new DataRetrievalException("Error consultando usuario con email " + email, ex);
            })
            .as(readOnlyTransactional::transactional); 
    }
    
    @Override
    public Mono<User> findByEmailOrIdNumber(String email, Long idNumber) {
        log.info("Starting search for users with email: {} OR idNumber: {}", email, idNumber);
        
        if ((email == null || email.trim().isEmpty()) && idNumber == null) {
            log.warn("Both email and idNumber are null or empty, returning empty result");
            return Mono.empty();
        }

        return repository.findByEmailOrIdNumber(email, idNumber)
            .switchIfEmpty(Mono.defer(() -> {
                log.info("No user found with email {} or idNumber {}", email, idNumber);
                return Mono.empty();
            }))
            .doOnNext(entity -> log.debug("Found user entity with email {} or idNumber {}", email, idNumber))
            .map(this::toEntity)
            .doOnNext(user -> log.info("Successfully mapped user with email {} or idNumber {}", email, idNumber))
            .onErrorMap(ex -> {
                log.error("Error retrieving user with email {} or idNumber {}: {}", email, idNumber, ex.getMessage(), ex);
                return new DataRetrievalException("Error consultando usuario con email " + email + "o número de identificación " + idNumber, ex);
            })
            .as(readOnlyTransactional::transactional); 
    }

    @Override
    public Mono<Boolean> existByIdNumber(Long idNumber) {
        log.info("Starting search for users with idNumber: {}", idNumber);
        return repository.existsByIdNumber(idNumber)
            .onErrorMap(ex -> {
                log.error("Error retrieving user with idNumber {}", idNumber, ex.getMessage(), ex);
                return new DataRetrievalException("Error consultando usuario con número de identificación " + idNumber, ex);
            })
            .as(readOnlyTransactional::transactional);
    }

    @Override
    public Flux<User> findUsersByEmails(List<String> userEmails) {
        log.info("Retrieving users by emails: {}", userEmails);
        
        if (userEmails == null || userEmails.isEmpty()) {
            return Flux.empty();
        }
        
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < userEmails.size(); i++) {
            if (i > 0) {
                placeholders.append(", ");
            }
            placeholders.append("$").append(i + 1);
        }
        
        String sql = String.format("SELECT * FROM users WHERE email IN (%s)", placeholders);
        
        return executeQuery(sql, userEmails)
            .map(this::toEntity)
            .doOnNext(user -> log.debug("Retrieved user: {}", user.getEmail()))
            .doOnComplete(() -> log.info("Finished retrieving users by emails"))
            .onErrorMap(ex -> {
                log.error("Error retrieving users by emails", ex);
                return new DataRetrievalException("Error al momento de consultar los usuarios por email", ex);
            })
            .as(readOnlyTransactional::transactional);
    }

    private Flux<UserEntity> executeQuery(String sql, List<String> params) {
        DatabaseClient.GenericExecuteSpec spec = databaseClient.sql(sql);
        for (int i = 0; i < params.size(); i++) {
            spec = spec.bind(i, params.get(i));
        }
        return spec.map((row, rowMetadata) -> mapRow(row)).all();
    }

    private UserEntity mapRow(Row row) {
        UserEntity entity = new UserEntity();
        entity.setId(row.get("id", BigInteger.class));
        entity.setIdNumber(row.get("id_number", Long.class));
        entity.setIdTypeId(row.get("id_type_id", Integer.class));
        entity.setName(row.get("name", String.class));
        entity.setLastname(row.get("lastname", String.class));
        entity.setBirthDate(row.get("birth_date", LocalDate.class));
        entity.setAddress(row.get("address", String.class));
        entity.setPhone(row.get("phone", String.class));
        entity.setEmail(row.get("email", String.class));
        entity.setBaseSalary(row.get("base_salary", Double.class));
        entity.setRoleId(row.get("role_id", Integer.class));
        entity.setPassword(row.get("password", String.class));
        return entity;
    }

}
