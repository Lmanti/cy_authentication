package co.com.crediya.cy_authentication.r2dbc;

import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import co.com.crediya.cy_authentication.r2dbc.entity.IdTypeEntity;

public interface IdTypeReactiveRepository extends ReactiveCrudRepository<IdTypeEntity, Integer>, ReactiveQueryByExampleExecutor<IdTypeEntity> {
    
}
