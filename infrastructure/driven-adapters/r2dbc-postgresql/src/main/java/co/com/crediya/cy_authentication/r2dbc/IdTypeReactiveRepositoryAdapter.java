package co.com.crediya.cy_authentication.r2dbc;

import org.reactivecommons.utils.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;

import co.com.crediya.cy_authentication.exception.DataRetrievalException;
import co.com.crediya.cy_authentication.model.idtype.IdType;
import co.com.crediya.cy_authentication.model.idtype.gateways.IdTypeRepository;
import co.com.crediya.cy_authentication.r2dbc.entity.IdTypeEntity;
import co.com.crediya.cy_authentication.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Slf4j
public class IdTypeReactiveRepositoryAdapter extends ReactiveAdapterOperations<
    IdType,
    IdTypeEntity,
    Integer,
    IdTypeReactiveRepository
> implements IdTypeRepository {

    private final TransactionalOperator readOnlyTransactional;

    public IdTypeReactiveRepositoryAdapter(
        IdTypeReactiveRepository repository,
        ObjectMapper mapper,
        @Qualifier("readOnlyTransactionalOperator") TransactionalOperator readOnlyTransactional
    ) {
        super(repository, mapper, d -> mapper.map(d, IdType.class));
        this.readOnlyTransactional = readOnlyTransactional;
    }

    @Override
    public Flux<IdType> getAllIdTypes() {
        log.info("Retrieving all id types");

        return findAll()
            .doOnNext(idType
                -> log.debug("Retrieved id types successfully"))
            .doOnComplete(() -> log.info("Finished retrieving all id types"))
            .onErrorMap(ex -> {
                log.error("Error retrieving all id types", ex);
                return new DataRetrievalException("Error al momento de consultar los tipos de identificación", ex);
            })
            .doOnError(ex -> log.error("Failed to retrieve all the id types", ex))
            .as(readOnlyTransactional::transactional);
    }

    @Override
    public Mono<IdType> getIdTypeById(Integer idTypeId) {
        log.info("Searching id type with ID number: {}", idTypeId);
    
        return repository.findById(idTypeId)
            .doOnNext(entity -> log.debug("Found id type entity ID number {}", idTypeId))
            .map(this::toEntity)
            .doOnNext(entity -> log.info("Successfully mapped id type ID number {}", idTypeId))
            .onErrorMap(ex -> {
                log.error("Error retrieving id type with ID number {}: {}", idTypeId, ex.getMessage(), ex);
                return new DataRetrievalException("Error consultando tipo de identificación con ID " + idTypeId, ex);
            })
            .as(readOnlyTransactional::transactional);
    }
    
}
