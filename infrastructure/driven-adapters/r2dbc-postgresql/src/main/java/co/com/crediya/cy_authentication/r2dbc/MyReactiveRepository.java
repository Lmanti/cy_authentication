package co.com.crediya.cy_authentication.r2dbc;

import java.math.BigInteger;

import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import co.com.crediya.cy_authentication.r2dbc.entity.UserEntity;
import reactor.core.publisher.Mono;

public interface MyReactiveRepository extends ReactiveCrudRepository<UserEntity, BigInteger>, ReactiveQueryByExampleExecutor<UserEntity> {
    Mono<UserEntity> findByIdNumber(Long idNumber);
}
