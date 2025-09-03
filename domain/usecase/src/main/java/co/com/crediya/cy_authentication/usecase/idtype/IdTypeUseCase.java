package co.com.crediya.cy_authentication.usecase.idtype;

import co.com.crediya.cy_authentication.model.idtype.IdType;
import co.com.crediya.cy_authentication.model.idtype.gateways.IdTypeRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class IdTypeUseCase {
    private final IdTypeRepository idTypeRepository;

    public Flux<IdType> getAllIdTypes() {
        return idTypeRepository.getAllIdTypes();
    }

    public Mono<IdType> getIdTypeById(Integer roleId) {
        return idTypeRepository.getIdTypeById(roleId);
    }
}
