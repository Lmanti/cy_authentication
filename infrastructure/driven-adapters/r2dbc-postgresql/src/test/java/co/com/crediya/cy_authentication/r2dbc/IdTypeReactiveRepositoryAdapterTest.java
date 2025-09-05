package co.com.crediya.cy_authentication.r2dbc;

import co.com.crediya.cy_authentication.exception.DataRetrievalException;
import co.com.crediya.cy_authentication.model.idtype.IdType;
import co.com.crediya.cy_authentication.r2dbc.entity.IdTypeEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.transaction.reactive.TransactionalOperator;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class IdTypeReactiveRepositoryAdapterTest {

    @Mock
    private IdTypeReactiveRepository repository;
    
    @Mock
    private ObjectMapper mapper;

    @Mock
    private TransactionalOperator readOnlyTransactional;

    private IdTypeReactiveRepositoryAdapter adapter;

    private IdType validIdType;
    private IdTypeEntity validIdTypeEntity;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        when(readOnlyTransactional.transactional(any(Flux.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(readOnlyTransactional.transactional(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        adapter = new IdTypeReactiveRepositoryAdapter(repository, mapper, readOnlyTransactional);

        validIdType = IdType.builder()
                .id(1)
                .name("CC")
                .description("Cédula de Ciudadanía")
                .build();

        validIdTypeEntity = new IdTypeEntity();
        validIdTypeEntity.setId(1);
        validIdTypeEntity.setName("CC");
        validIdTypeEntity.setDescription("Cédula de Ciudadanía");
    }

    @Test
    @DisplayName("Should get all id types successfully")
    void shouldGetAllIdTypesSuccessfully() {
        // Given
        when(repository.findAll()).thenReturn(Flux.just(validIdTypeEntity));
        when(mapper.map(any(IdTypeEntity.class), any())).thenReturn(validIdType);

        // When & Then
        StepVerifier.create(adapter.getAllIdTypes())
                .expectNext(validIdType)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should get empty flux when no id types exist")
    void shouldGetEmptyFluxWhenNoIdTypesExist() {
        // Given
        when(repository.findAll()).thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(adapter.getAllIdTypes())
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle get all id types error")
    void shouldHandleGetAllIdTypesError() {
        // Given
        when(repository.findAll()).thenReturn(Flux.error(new RuntimeException("Database error")));

        // When & Then
        StepVerifier.create(adapter.getAllIdTypes())
                .expectError(DataRetrievalException.class)
                .verify();
    }

    @Test
    @DisplayName("Should get id type by id successfully")
    void shouldGetIdTypeByIdSuccessfully() {
        // Given
        when(repository.findById(1)).thenReturn(Mono.just(validIdTypeEntity));
        when(mapper.map(any(IdTypeEntity.class), any())).thenReturn(validIdType);

        // When & Then
        StepVerifier.create(adapter.getIdTypeById(1))
                .expectNext(validIdType)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should get empty mono when id type not found")
    void shouldGetEmptyMonoWhenIdTypeNotFound() {
        // Given
        when(repository.findById(999)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(adapter.getIdTypeById(999))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle get id type by id error")
    void shouldHandleGetIdTypeByIdError() {
        // Given
        when(repository.findById(anyInt())).thenReturn(Mono.error(new RuntimeException("Database error")));

        // When & Then
        StepVerifier.create(adapter.getIdTypeById(1))
                .expectError(DataRetrievalException.class)
                .verify();
    }

    @Test
    @DisplayName("Should get multiple id types successfully")
    void shouldGetMultipleIdTypesSuccessfully() {
        // Given
        IdType tiIdType = IdType.builder()
                .id(2)
                .name("TI")
                .description("Tarjeta de Identidad")
                .build();

        IdTypeEntity tiIdTypeEntity = new IdTypeEntity();
        tiIdTypeEntity.setId(2);
        tiIdTypeEntity.setName("TI");
        tiIdTypeEntity.setDescription("Tarjeta de Identidad");

        when(repository.findAll()).thenReturn(Flux.just(validIdTypeEntity, tiIdTypeEntity));
        when(mapper.map(validIdTypeEntity, IdType.class)).thenReturn(validIdType);
        when(mapper.map(tiIdTypeEntity, IdType.class)).thenReturn(tiIdType);

        // When & Then
        StepVerifier.create(adapter.getAllIdTypes())
                .expectNext(validIdType)
                .expectNext(tiIdType)
                .verifyComplete();
    }
}