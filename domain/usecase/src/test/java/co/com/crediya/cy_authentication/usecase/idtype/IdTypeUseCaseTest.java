package co.com.crediya.cy_authentication.usecase.idtype;

import co.com.crediya.cy_authentication.model.idtype.IdType;
import co.com.crediya.cy_authentication.model.idtype.gateways.IdTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

class IdTypeUseCaseTest {

    @Mock
    private IdTypeRepository idTypeRepository;

    private IdTypeUseCase idTypeUseCase;

    private IdType cedulaIdType;
    private IdType tarjetaIdType;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        idTypeUseCase = new IdTypeUseCase(idTypeRepository);

        cedulaIdType = IdType.builder()
                .id(1)
                .name("CC")
                .description("Cédula de Ciudadanía")
                .build();

        tarjetaIdType = IdType.builder()
                .id(2)
                .name("TI")
                .description("Tarjeta de Identidad")
                .build();
    }

    @Test
    @DisplayName("Should get all id types successfully")
    void shouldGetAllIdTypesSuccessfully() {
        // Given
        when(idTypeRepository.getAllIdTypes()).thenReturn(Flux.just(cedulaIdType, tarjetaIdType));

        // When & Then
        StepVerifier.create(idTypeUseCase.getAllIdTypes())
                .expectNext(cedulaIdType)
                .expectNext(tarjetaIdType)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should get empty flux when no id types exist")
    void shouldGetEmptyFluxWhenNoIdTypesExist() {
        // Given
        when(idTypeRepository.getAllIdTypes()).thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(idTypeUseCase.getAllIdTypes())
                .verifyComplete();
    }

    @Test
    @DisplayName("Should get id type by id successfully")
    void shouldGetIdTypeByIdSuccessfully() {
        // Given
        when(idTypeRepository.getIdTypeById(1)).thenReturn(Mono.just(cedulaIdType));

        // When & Then
        StepVerifier.create(idTypeUseCase.getIdTypeById(1))
                .expectNext(cedulaIdType)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should get empty mono when id type not found")
    void shouldGetEmptyMonoWhenIdTypeNotFound() {
        // Given
        when(idTypeRepository.getIdTypeById(999)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(idTypeUseCase.getIdTypeById(999))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle repository error")
    void shouldHandleRepositoryError() {
        // Given
        when(idTypeRepository.getAllIdTypes()).thenReturn(Flux.error(new RuntimeException("Database error")));

        // When & Then
        StepVerifier.create(idTypeUseCase.getAllIdTypes())
                .expectError(RuntimeException.class)
                .verify();
    }
}