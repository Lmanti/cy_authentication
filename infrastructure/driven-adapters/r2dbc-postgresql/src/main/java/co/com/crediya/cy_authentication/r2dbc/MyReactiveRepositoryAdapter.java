package co.com.crediya.cy_authentication.r2dbc;

import java.math.BigInteger;

import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import co.com.crediya.cy_authentication.common.CustomPageRequest;
import co.com.crediya.cy_authentication.common.SortRequest;
import co.com.crediya.cy_authentication.exception.DataPersistenceException;
import co.com.crediya.cy_authentication.exception.DataRetrievalException;
import co.com.crediya.cy_authentication.exception.UserNotFoundException;
import co.com.crediya.cy_authentication.model.user.User;
import co.com.crediya.cy_authentication.model.user.gateways.UserRepository;
import co.com.crediya.cy_authentication.r2dbc.entity.UserEntity;
import co.com.crediya.cy_authentication.r2dbc.helper.ReactiveAdapterOperations;
import co.com.crediya.cy_authentication.r2dbc.helper.ReactiveUserCriteriaBuilder;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class MyReactiveRepositoryAdapter extends ReactiveAdapterOperations<
    User,
    UserEntity,
    BigInteger,
    MyReactiveRepository
> implements UserRepository {

    private final R2dbcEntityTemplate template;

    public MyReactiveRepositoryAdapter(MyReactiveRepository repository, ObjectMapper mapper, R2dbcEntityTemplate template) {
        super(repository, mapper, d -> mapper.map(d, User.class));
        this.template = template;
    }

    public ReactiveUserCriteriaBuilder configureCriteriaBuilder() {
        return new ReactiveUserCriteriaBuilder(template);
    }

    @Override
    @Transactional(rollbackFor = DataPersistenceException.class)
    public Mono<User> saveUser(Mono<User> user) {
        return user
            .doOnNext(userData -> log.info("Attempting to save user: {}", userData.getIdNumber()))
            .flatMap(userData -> 
                repository.save(toData(userData))
                    .doOnNext(savedEntity -> log.debug("User entity saved: {}", savedEntity.getId()))
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
                    log.debug("Found existing user: {}", existingEntity.getId());

                    existingEntity.setIdNumber(userData.getIdNumber());
                    existingEntity.setIdType(userData.getIdType());
                    existingEntity.setName(userData.getName());
                    existingEntity.setLastname(userData.getLastname());
                    existingEntity.setBirthDate(userData.getBirthDate());
                    existingEntity.setAddress(userData.getAddress());
                    existingEntity.setPhone(userData.getPhone());
                    existingEntity.setEmail(userData.getEmail());
                    existingEntity.setBaseSalary(userData.getBaseSalary());
                    
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
    public Flux<User> findByCriteria(Mono<User> userCriteria) {
        return userCriteria
            .doOnNext(criteria -> log.info("Searching for users with criteria: {}", criteria))
            .flatMapMany(criteria -> {
                log.debug("Building criteria query for: {}", criteria);
                
                return new ReactiveUserCriteriaBuilder(template)
                    .withUser(criteria)
                    .find()
                    .doOnNext(entity -> log.debug("Found user entity: {}", entity))
                    .map(this::toEntity)
                    .doOnNext(user -> log.debug("Mapped to domain user: {}", user))
                    .doOnComplete(() -> log.info("Completed search with criteria: {}", criteria));
            })
            .switchIfEmpty(Flux.defer(() -> {
                log.warn("No users found matching criteria");
                return Flux.empty();
            }))
            .onErrorMap(ex -> {
                log.error("Error searching users with criteria: {}", ex.getMessage(), ex);
                return new DataRetrievalException("Error consultando usuarios con los criterios proporcionados", ex);
            })
            .doOnComplete(() -> log.info("Completed user search operation"));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<User> findByCriteriaPaginated(User criteria, CustomPageRequest pageable) {
        log.info("Starting paginated search with criteria: {} and pagination: {}", criteria, pageable);
        
        Pageable springPageable = convertToPageable(pageable);
        log.debug("Converted to Spring Pageable: page={}, size={}, sort={}", 
                springPageable.getPageNumber(), springPageable.getPageSize(), 
                springPageable.getSort());
        
        ReactiveUserCriteriaBuilder criteriaBuilder = configureCriteriaBuilder();
        log.debug("Criteria builder initialized");
        
        criteriaBuilder.withUser(criteria);
        log.debug("User criteria applied: {}", criteria);
        
        return criteriaBuilder.find(springPageable)
            .doOnSubscribe(s -> log.debug("Executing paginated database query"))
            .doOnNext(entity -> log.debug("Found entity: id={}", entity.getId()))
            .map(entity -> {
                log.trace("Mapping entity to domain: {}", entity);
                User user = this.toEntity(entity);
                log.debug("Mapped to domain user: {}", user);
                return user;
            })
            .doOnComplete(() -> log.info("Completed paginated search, criteria: {}, page: {}, size: {}", 
                    criteria, pageable.getPage(), pageable.getSize()))
            .switchIfEmpty(Flux.defer(() -> {
                log.warn("No users found matching criteria with pagination: {}", pageable);
                return Flux.empty();
            }))
            .onErrorMap(ex -> {
                log.error("Error during paginated search: {}", ex.getMessage(), ex);
                return new DataRetrievalException(
                    "Error consultando usuarios paginados con los criterios proporcionados", ex);
            })
            .doFinally(signalType -> log.info("Paginated search operation finalized with signal: {}", signalType));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Mono<Long> countByCriteria(Mono<User> criteria) {
        log.info("Starting count operation");
        
        return criteria
            .doOnNext(userCriteria -> log.info("Received criteria for count operation: {}", userCriteria))
            .flatMap(userCriteria -> {
                log.debug("Processing count criteria: {}", userCriteria);
                
                ReactiveUserCriteriaBuilder builder = configureCriteriaBuilder();
                log.debug("Criteria builder initialized for count operation");
                
                builder.withUser(userCriteria);
                log.debug("User criteria applied to count operation");
                
                return builder.count()
                    .doOnSubscribe(s -> log.debug("Executing count database query"))
                    .doOnNext(count -> log.info("Count result: {} users found matching criteria", count))
                    .doOnError(e -> log.error("Error during count operation: {}", e.getMessage(), e));
            })
            .defaultIfEmpty(0L)
            .doOnNext(count -> log.debug("Returning count result: {}", count))
            .onErrorMap(ex -> {
                log.error("Error during count operation: {}", ex.getMessage(), ex);
                return new DataRetrievalException("Error contando usuarios con los criterios proporcionados", ex);
            })
            .doFinally(signalType -> log.info("Count operation finalized with signal: {}", signalType));
    }
    
    @Override
    @Transactional(readOnly = true)
    public Flux<User> findByEmailOrIdNumber(String email, Long idNumber) {
        log.info("Starting search for users with email: {} OR idNumber: {}", email, idNumber);
        
        if ((email == null || email.trim().isEmpty()) && idNumber == null) {
            log.warn("Both email and idNumber are null or empty, returning empty result");
            return Flux.empty();
        }
        
        ReactiveUserCriteriaBuilder builder = configureCriteriaBuilder();
        log.debug("Criteria builder initialized for OR search");
        

        builder.withEmail(email);
        log.debug("Email criterion added: {}", email);
        builder.withIdNumber(idNumber);
        log.debug("IdNumber criterion added: {}", idNumber);
        
        builder.withOrCriteria();
        log.debug("Criteria combined with OR operator");
        
        return builder.find()
            .doOnSubscribe(s -> log.debug("Executing OR search database query"))
            .doOnNext(entity -> log.debug("Found entity: id={}, idNumber={}, email={}", 
                    entity.getId(), entity.getIdNumber(), entity.getEmail()))
            .map(entity -> {
                log.trace("Mapping entity to domain: {}", entity);
                User user = this.toEntity(entity);
                log.debug("Mapped to domain user: idNumber={}, email={}", 
                        user.getIdNumber(), user.getEmail());
                return user;
            })
            .doOnComplete(() -> log.info("Completed OR search for email: {} and idNumber: {}", 
                    email, idNumber))
            .switchIfEmpty(Flux.defer(() -> {
                log.warn("No users found with email: {} OR idNumber: {}", email, idNumber);
                return Flux.empty();
            }))
            .onErrorMap(ex -> {
                log.error("Error during OR search: {}", ex.getMessage(), ex);
                return new DataRetrievalException(
                    "Error consultando usuarios por email o número de identificación", ex);
            })
            .doFinally(signalType -> log.info("OR search operation finalized with signal: {}", signalType));
    }

    private Pageable convertToPageable(CustomPageRequest pageRequest) {
        Sort springSort = Sort.unsorted();
        
        if (pageRequest.getSort() != null) {
            springSort = Sort.by(
                pageRequest.getSort().getDirection() == SortRequest.Direction.ASC ? 
                    Sort.Direction.ASC : 
                    Sort.Direction.DESC,
                pageRequest.getSort().getProperty()
            );
        }
        
        return PageRequest.of(
            pageRequest.getPage(), 
            pageRequest.getSize(), 
            springSort
        );
    }

}
