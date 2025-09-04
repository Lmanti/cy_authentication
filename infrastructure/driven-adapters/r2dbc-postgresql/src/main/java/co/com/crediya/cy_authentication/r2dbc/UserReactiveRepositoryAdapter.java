package co.com.crediya.cy_authentication.r2dbc;

import java.math.BigInteger;

import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import co.com.crediya.cy_authentication.exception.DataPersistenceException;
import co.com.crediya.cy_authentication.exception.DataRetrievalException;
import co.com.crediya.cy_authentication.exception.UserNotFoundException;
import co.com.crediya.cy_authentication.model.user.User;
import co.com.crediya.cy_authentication.model.user.gateways.UserRepository;
import co.com.crediya.cy_authentication.r2dbc.entity.UserEntity;
import co.com.crediya.cy_authentication.r2dbc.helper.ReactiveAdapterOperations;
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

    public UserReactiveRepositoryAdapter(UserReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, User.class));
    }

    @Override
    @Transactional(rollbackFor = DataPersistenceException.class)
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
            );
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<User> getAllUsers() {
        log.info("Retrieving all users");
    
        return findAll()
            .doOnNext(user -> log.debug("Retrieved users successfully"))
            .doOnComplete(() -> log.info("Finished retrieving all users"))
            .onErrorMap(ex -> {
                log.error("Error retrieving all users", ex);
                return new DataRetrievalException("Error al momento de consultar los usuarios", ex);
            })
            .doOnError(ex -> log.error("Failed to retrieve users", ex));
    }

    @Override
    @Transactional(readOnly = true)
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
            });
    }

    @Override
    @Transactional(rollbackFor = DataPersistenceException.class)
    public Mono<User> editUser(Mono<User> user) {
        return user
            .doOnNext(userData -> log.info("Attempting to edit user with ID: {}", userData.getIdNumber()))
            .flatMap(userData -> repository.findByIdNumber(userData.getIdNumber())
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("User with ID {} not found for edit", userData.getIdNumber());
                    return Mono.error(new UserNotFoundException("No se ha encontrado un usuario con identificación " + userData.getIdNumber()));
                }))
                .flatMap(existingEntity -> {
                    log.debug("Found existing user: {}", existingEntity.getIdNumber());

                    existingEntity.setIdNumber(userData.getIdNumber());
                    existingEntity.setIdTypeId(userData.getIdTypeId());
                    existingEntity.setName(userData.getName());
                    existingEntity.setLastname(userData.getLastname());
                    existingEntity.setBirthDate(userData.getBirthDate());
                    existingEntity.setAddress(userData.getAddress());
                    existingEntity.setPhone(userData.getPhone());
                    existingEntity.setEmail(userData.getEmail());
                    existingEntity.setBaseSalary(userData.getBaseSalary());
                    existingEntity.setRoleId(userData.getRoleId());
                    existingEntity.setPassword(userData.getPassword());
                    
                    log.debug("Saving updated user entity");
                    return repository.save(existingEntity);
                })
                .map(this::toEntity)
                .doOnNext(updatedUser -> log.info("User with ID {} successfully updated", updatedUser.getIdNumber()))
                .onErrorMap(ex -> {
                    if (ex instanceof UserNotFoundException) {
                        return ex;
                    }
                    log.error("Error updating user: {}", ex.getMessage(), ex);
                    return new DataPersistenceException("Error intentando actualizar el usuario", ex);
                })
            );
    }

    @Override
    @Transactional
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
            });
    }

    @Override
    @Transactional(readOnly = true)
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
            });
    }
    
    @Override
    @Transactional(readOnly = true)
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
            });
    }

}
