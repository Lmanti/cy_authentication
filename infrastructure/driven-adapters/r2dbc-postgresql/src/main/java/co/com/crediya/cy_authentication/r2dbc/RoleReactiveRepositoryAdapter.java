package co.com.crediya.cy_authentication.r2dbc;

import org.reactivecommons.utils.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;

import co.com.crediya.cy_authentication.exception.DataRetrievalException;
import co.com.crediya.cy_authentication.model.role.Role;
import co.com.crediya.cy_authentication.model.role.gateways.RoleRepository;
import co.com.crediya.cy_authentication.r2dbc.entity.RoleEntity;
import co.com.crediya.cy_authentication.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class RoleReactiveRepositoryAdapter extends ReactiveAdapterOperations<
    Role,
    RoleEntity,
    Integer,
    RoleReactiveReository
> implements RoleRepository {

    private final TransactionalOperator readOnlyTransactional;

    public RoleReactiveRepositoryAdapter(
        RoleReactiveReository repository,
        ObjectMapper mapper,
        @Qualifier("readOnlyTransactionalOperator") TransactionalOperator readOnlyTransactional
    ) {
        super(repository, mapper, d -> mapper.map(d, Role.class));
        this.readOnlyTransactional = readOnlyTransactional;
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<Role> getAllRoles() {
        log.info("Retrieving all roles");

        return findAll()
            .doOnNext(role
                -> log.debug("Retrieved roles successfully"))
            .doOnComplete(() -> log.info("Finished retrieving all roles"))
            .onErrorMap(ex -> {
                log.error("Error retrieving all roles", ex);
                return new DataRetrievalException("Error al momento de consultar los roles", ex);
            })
            .doOnError(ex -> log.error("Failed to retrieve all the roles", ex))
            .as(readOnlyTransactional::transactional);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Role> getRoleById(Integer roleId) {
        log.info("Searching role with ID number: {}", roleId);
    
        return repository.findById(roleId)
            .doOnNext(entity -> log.debug("Found role entity ID number {}", roleId))
            .map(this::toEntity)
            .doOnNext(entity -> log.info("Successfully mapped role ID number {}", roleId))
            .onErrorMap(ex -> {
                log.error("Error retrieving role with ID number {}: {}", roleId, ex.getMessage(), ex);
                return new DataRetrievalException("Error consultando rol con ID " + roleId, ex);
            })
            .as(readOnlyTransactional::transactional);
    }
    
}
