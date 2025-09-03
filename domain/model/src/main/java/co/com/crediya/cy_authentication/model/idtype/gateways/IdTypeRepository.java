package co.com.crediya.cy_authentication.model.idtype.gateways;

import co.com.crediya.cy_authentication.model.idtype.IdType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IdTypeRepository {
    Flux<IdType> getAllIdTypes();
    Mono<IdType> getIdTypeById(Integer idTypeId);
}
