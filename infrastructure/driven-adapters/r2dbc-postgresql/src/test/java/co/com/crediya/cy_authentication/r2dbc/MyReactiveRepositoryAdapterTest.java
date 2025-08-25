package co.com.crediya.cy_authentication.r2dbc;

import co.com.crediya.cy_authentication.model.user.User;
import co.com.crediya.cy_authentication.r2dbc.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigInteger;
import java.time.LocalDate;

import static org.mockito.Mockito.when;

class MyReactiveRepositoryAdapterTest {

    @Mock
    private MyReactiveRepository repository;

    @InjectMocks
    private MyReactiveRepositoryAdapter repositoryAdapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findByIdTest() {
        // Crear datos de prueba
        UserEntity userEntity = createUserEntity();
        BigInteger id = BigInteger.valueOf(1);

        // Configurar el mock
        when(repository.findById(id)).thenReturn(Mono.just(userEntity));

        // Ejecutar el método a probar
        Mono<User> result = repositoryAdapter.findById(id);

        // Verificar el resultado
        StepVerifier.create(result)
                .expectNextMatches(user -> user.getIdNumber().equals(12345678L))
                .verifyComplete();
    }

    @Test
    void findAllTest() {
        // Crear datos de prueba
        UserEntity userEntity = createUserEntity();

        // Configurar el mock
        when(repository.findAll()).thenReturn(Flux.just(userEntity));

        // Ejecutar el método a probar
        Flux<User> result = repositoryAdapter.findAll();

        // Verificar el resultado
        StepVerifier.create(result)
                .expectNextMatches(user -> user.getIdNumber().equals(12345678L))
                .verifyComplete();
    }

    @Test
    void findByExampleTest() {
        // Crear datos de prueba
        User user = createUser();
        UserEntity userEntity = createUserEntity();

        // Configurar el mock para el método findAll
        when(repository.findAll()).thenReturn(Flux.just(userEntity));

        // Ejecutar el método a probar
        Flux<User> result = repositoryAdapter.findByExample(user);

        // Verificar el resultado
        StepVerifier.create(result)
                .expectNextMatches(u -> u.getIdNumber().equals(12345678L))
                .verifyComplete();
    }

    @Test
    void saveTest() {
        // Crear datos de prueba
        User user = createUser();
        UserEntity userEntity = createUserEntity();
        UserEntity savedEntity = createUserEntity(); // Simular entidad guardada

        // Configurar el mock
        when(repository.save(userEntity)).thenReturn(Mono.just(savedEntity));

        // Ejecutar el método a probar
        Mono<User> result = repositoryAdapter.save(user);

        // Verificar el resultado
        StepVerifier.create(result)
                .expectNextMatches(u -> u.getIdNumber().equals(12345678L))
                .verifyComplete();
    }

    // Métodos auxiliares para crear objetos de prueba
    private UserEntity createUserEntity() {
        UserEntity entity = new UserEntity();
        entity.setId(BigInteger.valueOf(1));
        entity.setIdNumber(12345678L);
        entity.setIdType(1);
        entity.setName("Test Name");
        entity.setLastname("Test Lastname");
        entity.setBirthDate(LocalDate.of(1990, 1, 1));
        entity.setAddress("Test Address");
        entity.setPhone("1234567890");
        entity.setEmail("test@example.com");
        entity.setBaseSalary(2500000.0);
        return entity;
    }

    private User createUser() {
        return User.builder()
                .idNumber(12345678L)
                .idType(1)
                .name("Test Name")
                .lastname("Test Lastname")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address("Test Address")
                .phone("1234567890")
                .email("test@example.com")
                .baseSalary(2500000.0)
                .build();
    }
}