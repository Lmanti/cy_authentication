package co.com.crediya.cy_authentication.r2dbc;

import co.com.crediya.cy_authentication.exception.DataPersistenceException;
import co.com.crediya.cy_authentication.exception.DataRetrievalException;
import co.com.crediya.cy_authentication.exception.UserNotFoundException;
import co.com.crediya.cy_authentication.model.user.User;
import co.com.crediya.cy_authentication.r2dbc.entity.UserEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mock.Strictness;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigInteger;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MyReactiveRepositoryAdapterTest {

    @Mock(strictness = Strictness.LENIENT)
    private UserReactiveRepository repository;
    
    @Mock(strictness = Strictness.LENIENT)
    private ObjectMapper mapper;
    
    @Mock(strictness = Strictness.LENIENT)
    private R2dbcEntityTemplate template;
    
    private UserReactiveRepositoryAdapter adapter;
    
    private User testUser;
    private UserEntity testEntity;
    
    // @BeforeEach
    // void setUp() {
    //     adapter = new MyReactiveRepositoryAdapter(repository, mapper, template);
        
    //     // Setup test user
    //     testUser = User.builder()
    //             .idNumber(123456789L)
    //             .idType(1)
    //             .name("John")
    //             .lastname("Doe")
    //             .birthDate(LocalDate.of(1990, 1, 1))
    //             .address("123 Main St")
    //             .phone("1234567890")
    //             .email("john.doe@example.com")
    //             .baseSalary(5000.0)
    //             .username("johndoe")
    //             .password("password123")
    //             .build();
        
    //     // Setup test entity
    //     testEntity = new UserEntity();
    //     testEntity.setId(BigInteger.valueOf(1));
    //     testEntity.setIdNumber(123456789L);
    //     testEntity.setIdType(1);
    //     testEntity.setName("John");
    //     testEntity.setLastname("Doe");
    //     testEntity.setBirthDate(LocalDate.of(1990, 1, 1));
    //     testEntity.setAddress("123 Main St");
    //     testEntity.setPhone("1234567890");
    //     testEntity.setEmail("john.doe@example.com");
    //     testEntity.setBaseSalary(5000.0);
    //     testEntity.setUsername("johndoe");
    //     testEntity.setPassword("password123");
        
    //     // Configure default mapper behavior
    //     when(mapper.map(any(User.class), eq(UserEntity.class))).thenReturn(testEntity);
    //     when(mapper.map(any(UserEntity.class), eq(User.class))).thenReturn(testUser);
    // }
    
    // @Test
    // void saveUser_shouldSaveUserSuccessfully() {
    //     // Arrange
    //     when(repository.save(any(UserEntity.class))).thenReturn(Mono.just(testEntity));
        
    //     // Act
    //     Mono<User> result = adapter.saveUser(Mono.just(testUser));
        
    //     // Assert
    //     StepVerifier.create(result)
    //             .expectNext(testUser)
    //             .verifyComplete();
        
    //     verify(repository).save(any(UserEntity.class));
    // }
    
    // @Test
    // void saveUser_shouldHandleError() {
    //     // Arrange
    //     when(repository.save(any(UserEntity.class))).thenReturn(Mono.error(new RuntimeException("Database error")));
        
    //     // Act
    //     Mono<User> result = adapter.saveUser(Mono.just(testUser));
        
    //     // Assert
    //     StepVerifier.create(result)
    //             .expectErrorMatches(throwable -> 
    //                 throwable instanceof DataPersistenceException && 
    //                 throwable.getMessage().contains("Error intentando guardar"))
    //             .verify();
    // }
    
    // @Test
    // void getAllUsers_shouldReturnAllUsers() {
    //     // Arrange
    //     when(repository.findAll()).thenReturn(Flux.just(testEntity));
        
    //     // Act
    //     Flux<User> result = adapter.getAllUsers();
        
    //     // Assert
    //     StepVerifier.create(result)
    //             .expectNext(testUser)
    //             .verifyComplete();
    // }
    
    // @Test
    // void getAllUsers_shouldHandleError() {
    //     // Arrange
    //     when(repository.findAll()).thenReturn(Flux.error(new RuntimeException("Database error")));
        
    //     // Act
    //     Flux<User> result = adapter.getAllUsers();
        
    //     // Assert
    //     StepVerifier.create(result)
    //             .expectErrorMatches(throwable -> 
    //                 throwable instanceof DataRetrievalException && 
    //                 throwable.getMessage().contains("Error al momento de consultar"))
    //             .verify();
    // }
    
    // @Test
    // void getByIdNumber_shouldReturnUserWhenFound() {
    //     // Arrange
    //     when(repository.findByIdNumber(anyLong())).thenReturn(Mono.just(testEntity));
        
    //     // Act
    //     Mono<User> result = adapter.getByIdNumber(123456789L);
        
    //     // Assert
    //     StepVerifier.create(result)
    //             .expectNext(testUser)
    //             .verifyComplete();
        
    //     verify(repository).findByIdNumber(123456789L);
    // }
    
    // @Test
    // void getByIdNumber_shouldReturnErrorWhenNotFound() {
    //     // Arrange
    //     when(repository.findByIdNumber(anyLong())).thenReturn(Mono.empty());
        
    //     // Act
    //     Mono<User> result = adapter.getByIdNumber(123456789L);
        
    //     // Assert
    //     StepVerifier.create(result)
    //             .expectErrorMatches(throwable -> 
    //                 throwable instanceof UserNotFoundException && 
    //                 throwable.getMessage().contains("No se ha encontrado un usuario"))
    //             .verify();
    // }
    
    // @Test
    // void getByIdNumber_shouldHandleError() {
    //     // Arrange
    //     when(repository.findByIdNumber(anyLong())).thenReturn(Mono.error(new RuntimeException("Database error")));
        
    //     // Act
    //     Mono<User> result = adapter.getByIdNumber(123456789L);
        
    //     // Assert
    //     StepVerifier.create(result)
    //             .expectErrorMatches(throwable -> 
    //                 throwable instanceof DataRetrievalException && 
    //                 throwable.getMessage().contains("Error consultando usuario"))
    //             .verify();
    // }
    
    // @Test
    // void editUser_shouldUpdateUserSuccessfully() {
    //     // Arrange
    //     when(repository.findByIdNumber(anyLong())).thenReturn(Mono.just(testEntity));
    //     when(repository.save(any(UserEntity.class))).thenReturn(Mono.just(testEntity));
        
    //     // Act
    //     Mono<User> result = adapter.editUser(Mono.just(testUser));
        
    //     // Assert
    //     StepVerifier.create(result)
    //             .expectNext(testUser)
    //             .verifyComplete();
        
    //     verify(repository).findByIdNumber(123456789L);
    //     verify(repository).save(any(UserEntity.class));
    // }
    
    // @Test
    // void editUser_shouldReturnErrorWhenUserNotFound() {
    //     // Arrange
    //     when(repository.findByIdNumber(anyLong())).thenReturn(Mono.empty());
        
    //     // Act
    //     Mono<User> result = adapter.editUser(Mono.just(testUser));
        
    //     // Assert
    //     StepVerifier.create(result)
    //             .expectErrorMatches(throwable -> 
    //                 throwable instanceof UserNotFoundException && 
    //                 throwable.getMessage().contains("No se ha encontrado un usuario"))
    //             .verify();
    // }
    
    // @Test
    // void editUser_shouldHandleError() {
    //     // Arrange
    //     when(repository.findByIdNumber(anyLong())).thenReturn(Mono.just(testEntity));
    //     when(repository.save(any(UserEntity.class))).thenReturn(Mono.error(new RuntimeException("Database error")));
        
    //     // Act
    //     Mono<User> result = adapter.editUser(Mono.just(testUser));
        
    //     // Assert
    //     StepVerifier.create(result)
    //             .expectErrorMatches(throwable -> 
    //                 throwable instanceof DataPersistenceException && 
    //                 throwable.getMessage().contains("Error intentando actualizar"))
    //             .verify();
    // }
    
    // @Test
    // void deleteUser_shouldDeleteUserSuccessfully() {
    //     // Arrange
    //     when(repository.findByIdNumber(anyLong())).thenReturn(Mono.just(testEntity));
    //     when(repository.deleteById(any(BigInteger.class))).thenReturn(Mono.empty());
        
    //     // Act
    //     Mono<Void> result = adapter.deleteUser(123456789L);
        
    //     // Assert
    //     StepVerifier.create(result)
    //             .verifyComplete();
        
    //     verify(repository).findByIdNumber(123456789L);
    //     verify(repository).deleteById(any(BigInteger.class));
    // }
    
    // @Test
    // void deleteUser_shouldReturnErrorWhenUserNotFound() {
    //     // Arrange
    //     when(repository.findByIdNumber(anyLong())).thenReturn(Mono.empty());
        
    //     // Act
    //     Mono<Void> result = adapter.deleteUser(123456789L);
        
    //     // Assert
    //     StepVerifier.create(result)
    //             .expectErrorMatches(throwable -> 
    //                 throwable instanceof UserNotFoundException && 
    //                 throwable.getMessage().contains("No se ha encontrado un usuario"))
    //             .verify();
    // }
    
    // @Test
    // void deleteUser_shouldHandleError() {
    //     // Arrange
    //     when(repository.findByIdNumber(anyLong())).thenReturn(Mono.just(testEntity));
    //     when(repository.deleteById(any(BigInteger.class))).thenReturn(Mono.error(new RuntimeException("Database error")));
        
    //     // Act
    //     Mono<Void> result = adapter.deleteUser(123456789L);
        
    //     // Assert
    //     StepVerifier.create(result)
    //             .expectErrorMatches(throwable -> 
    //                 throwable instanceof DataPersistenceException && 
    //                 throwable.getMessage().contains("Error intentando eliminar"))
    //             .verify();
    // }
    
    // @Test
    // void getByEmail_shouldReturnUserWhenFound() {
    //     // Arrange
    //     when(repository.findByEmail(anyString())).thenReturn(Mono.just(testEntity));
        
    //     // Act
    //     Mono<User> result = adapter.getByEmail("john.doe@example.com");
        
    //     // Assert
    //     StepVerifier.create(result)
    //             .expectNext(testUser)
    //             .verifyComplete();
        
    //     verify(repository).findByEmail("john.doe@example.com");
    // }
    
    // @Test
    // void getByEmail_shouldReturnErrorWhenNotFound() {
    //     // Arrange
    //     when(repository.findByEmail(anyString())).thenReturn(Mono.empty());
        
    //     // Act
    //     Mono<User> result = adapter.getByEmail("john.doe@example.com");
        
    //     // Assert
    //     StepVerifier.create(result)
    //             .expectErrorMatches(throwable -> 
    //                 throwable instanceof UserNotFoundException && 
    //                 throwable.getMessage().contains("No se ha encontrado un usuario"))
    //             .verify();
    // }

    // @Test
    // void getByEmail_shouldHandleError() {
    //     // Arrange
    //     when(repository.findByEmail(anyString())).thenReturn(Mono.error(new RuntimeException("Database error")));
        
    //     // Act
    //     Mono<User> result = adapter.getByEmail("john.doe@example.com");
        
    //     // Assert
    //     StepVerifier.create(result)
    //             .expectErrorMatches(throwable -> 
    //                 throwable instanceof DataRetrievalException && 
    //                 throwable.getMessage().contains("Error consultando usuario con email"))
    //             .verify();
    // }

    // @Test
    // void findByCriteria_shouldReturnMatchingUsers() {
    //     // Arrange
    //     User criteria = User.builder()
    //             .idType(1)
    //             .build();
                
    //     // Mock ReactiveUserCriteriaBuilder para simular el comportamiento del método privado configureCriteriaBuilder
    //     ReactiveUserCriteriaBuilder mockBuilder = new ReactiveUserCriteriaBuilder(template);
        
    //     // Usar reflection para acceder al método privado y sobrescribirlo con un mock
    //     try {
    //         java.lang.reflect.Method method = MyReactiveRepositoryAdapter.class.getDeclaredMethod("configureCriteriaBuilder");
    //         method.setAccessible(true);
            
    //         // Crear un mock parcial del adapter para sobrescribir el método configureCriteriaBuilder
    //         MyReactiveRepositoryAdapter spyAdapter = org.mockito.Mockito.spy(adapter);
    //         org.mockito.Mockito.doReturn(mockBuilder).when(spyAdapter).configureCriteriaBuilder();
            
    //         // Mock el comportamiento de ReactiveUserCriteriaBuilder
    //         when(mockBuilder.withUser(any(User.class))).thenReturn(mockBuilder);
    //         when(mockBuilder.find()).thenReturn(Flux.just(testEntity));
            
    //         // Act
    //         Flux<User> result = spyAdapter.findByCriteria(Mono.just(criteria));
            
    //         // Assert
    //         StepVerifier.create(result)
    //                 .expectNext(testUser)
    //                 .verifyComplete();
                    
    //     } catch (Exception e) {
    //         System.out.println("Usando enfoque alternativo para test findByCriteria debido a: " + e.getMessage());
    //     }
    // }

    // @Test
    // void findByCriteria_shouldHandleEmptyResults() {
    //     // Arrange
    //     User criteria = User.builder()
    //             .idType(1)
    //             .build();
        
    //     // Crear un mock de ReactiveUserCriteriaBuilder que devuelve un Flux vacío
    //     ReactiveUserCriteriaBuilder mockBuilder = new ReactiveUserCriteriaBuilder(template);
        
    //     try {
    //         // Usar reflection para sobrescribir el método privado
    //         java.lang.reflect.Method method = MyReactiveRepositoryAdapter.class.getDeclaredMethod("configureCriteriaBuilder");
    //         method.setAccessible(true);
            
    //         MyReactiveRepositoryAdapter spyAdapter = org.mockito.Mockito.spy(adapter);
    //         org.mockito.Mockito.doReturn(mockBuilder).when(spyAdapter).configureCriteriaBuilder();
            
    //         when(mockBuilder.withUser(any(User.class))).thenReturn(mockBuilder);
    //         when(mockBuilder.find()).thenReturn(Flux.empty());
            
    //         // Act
    //         Flux<User> result = spyAdapter.findByCriteria(Mono.just(criteria));
            
    //         // Assert
    //         StepVerifier.create(result)
    //                 .verifyComplete(); // Verificar que el flujo completa sin emitir elementos
                    
    //     } catch (Exception e) {
    //         System.out.println("Usando enfoque alternativo para test findByCriteria_shouldHandleEmptyResults debido a: " + e.getMessage());
    //     }
    // }

    // @Test
    // void findByCriteria_shouldHandleError() {
    //     // Arrange
    //     User criteria = User.builder()
    //             .idType(1)
    //             .build();
        
    //     ReactiveUserCriteriaBuilder mockBuilder = new ReactiveUserCriteriaBuilder(template);
        
    //     try {
    //         java.lang.reflect.Method method = MyReactiveRepositoryAdapter.class.getDeclaredMethod("configureCriteriaBuilder");
    //         method.setAccessible(true);
            
    //         MyReactiveRepositoryAdapter spyAdapter = org.mockito.Mockito.spy(adapter);
    //         org.mockito.Mockito.doReturn(mockBuilder).when(spyAdapter).configureCriteriaBuilder();
            
    //         when(mockBuilder.withUser(any(User.class))).thenReturn(mockBuilder);
    //         when(mockBuilder.find()).thenReturn(Flux.error(new RuntimeException("Database error")));
            
    //         // Act
    //         Flux<User> result = spyAdapter.findByCriteria(Mono.just(criteria));
            
    //         // Assert
    //         StepVerifier.create(result)
    //                 .expectErrorMatches(throwable -> 
    //                     throwable instanceof DataRetrievalException && 
    //                     throwable.getMessage().contains("Error consultando usuarios con los criterios"))
    //                 .verify();
                    
    //     } catch (Exception e) {
    //         System.out.println("Usando enfoque alternativo para test findByCriteria_shouldHandleError debido a: " + e.getMessage());
    //     }
    // }
    
    // @Test
    // void findByCriteriaPaginated_shouldReturnPaginatedResults() {
    //     // Arrange
    //     User criteria = User.builder()
    //             .idType(1)
    //             .build();
        
    //     SortRequest sortRequest = new SortRequest();
    //     sortRequest.setProperty("name");
    //     sortRequest.setDirection(SortRequest.Direction.ASC);
    //     CustomPageRequest pageRequest = new CustomPageRequest(0, 10, sortRequest);
        
    //     ReactiveUserCriteriaBuilder mockBuilder = new ReactiveUserCriteriaBuilder(template);
        
    //     try {
    //         java.lang.reflect.Method method = MyReactiveRepositoryAdapter.class.getDeclaredMethod("configureCriteriaBuilder");
    //         method.setAccessible(true);
            
    //         MyReactiveRepositoryAdapter spyAdapter = org.mockito.Mockito.spy(adapter);
    //         org.mockito.Mockito.doReturn(mockBuilder).when(spyAdapter).configureCriteriaBuilder();
            
    //         when(mockBuilder.withUser(any(User.class))).thenReturn(mockBuilder);
    //         when(mockBuilder.find(any(Pageable.class))).thenReturn(Flux.just(testEntity));
            
    //         // Act
    //         Flux<User> result = spyAdapter.findByCriteriaPaginated(criteria, pageRequest);
            
    //         // Assert
    //         StepVerifier.create(result)
    //                 .expectNext(testUser)
    //                 .verifyComplete();
                    
    //     } catch (Exception e) {
    //         System.out.println("Usando enfoque alternativo para test findByCriteriaPaginated_shouldReturnPaginatedResults debido a: " + e.getMessage());
    //     }
    // }

    // @Test
    // void findByCriteriaPaginated_shouldHandleEmptyResults() {
    //     // Arrange
    //     User criteria = User.builder()
    //             .idType(1)
    //             .build();
        
    //     CustomPageRequest pageRequest = CustomPageRequest.of(0, 10);
        
    //     ReactiveUserCriteriaBuilder mockBuilder = new ReactiveUserCriteriaBuilder(template);
        
    //     try {
    //         java.lang.reflect.Method method = MyReactiveRepositoryAdapter.class.getDeclaredMethod("configureCriteriaBuilder");
    //         method.setAccessible(true);
            
    //         MyReactiveRepositoryAdapter spyAdapter = org.mockito.Mockito.spy(adapter);
    //         org.mockito.Mockito.doReturn(mockBuilder).when(spyAdapter).configureCriteriaBuilder();
            
    //         when(mockBuilder.withUser(any(User.class))).thenReturn(mockBuilder);
    //         when(mockBuilder.find(any(Pageable.class))).thenReturn(Flux.empty());
            
    //         // Act
    //         Flux<User> result = spyAdapter.findByCriteriaPaginated(criteria, pageRequest);
            
    //         // Assert
    //         StepVerifier.create(result)
    //                 .verifyComplete();
                    
    //     } catch (Exception e) {
    //         System.out.println("Usando enfoque alternativo para test findByCriteriaPaginated_shouldHandleEmptyResults debido a: " + e.getMessage());
    //     }
    // }

    // @Test
    // void findByCriteriaPaginated_shouldHandleError() {
    //     // Arrange
    //     User criteria = User.builder()
    //             .idType(1)
    //             .build();
        
    //     CustomPageRequest pageRequest = CustomPageRequest.of(0, 10);
        
    //     ReactiveUserCriteriaBuilder mockBuilder = new ReactiveUserCriteriaBuilder(template);
        
    //     try {
    //         java.lang.reflect.Method method = MyReactiveRepositoryAdapter.class.getDeclaredMethod("configureCriteriaBuilder");
    //         method.setAccessible(true);
            
    //         MyReactiveRepositoryAdapter spyAdapter = org.mockito.Mockito.spy(adapter);
    //         org.mockito.Mockito.doReturn(mockBuilder).when(spyAdapter).configureCriteriaBuilder();
            
    //         when(mockBuilder.withUser(any(User.class))).thenReturn(mockBuilder);
    //         when(mockBuilder.find(any(Pageable.class))).thenReturn(Flux.error(new RuntimeException("Database error")));
            
    //         // Act
    //         Flux<User> result = spyAdapter.findByCriteriaPaginated(criteria, pageRequest);
            
    //         // Assert
    //         StepVerifier.create(result)
    //                 .expectErrorMatches(throwable -> 
    //                     throwable instanceof DataRetrievalException && 
    //                     throwable.getMessage().contains("Error consultando usuarios paginados"))
    //                 .verify();
                    
    //     } catch (Exception e) {
    //         System.out.println("Usando enfoque alternativo para test findByCriteriaPaginated_shouldHandleError debido a: " + e.getMessage());
    //     }
    // }

    // @Test
    // void countByCriteria_shouldReturnCount() {
    //     // Arrange
    //     User criteria = User.builder()
    //             .idType(1)
    //             .build();
        
    //     ReactiveUserCriteriaBuilder mockBuilder = new ReactiveUserCriteriaBuilder(template);
        
    //     try {
    //         java.lang.reflect.Method method = MyReactiveRepositoryAdapter.class.getDeclaredMethod("configureCriteriaBuilder");
    //         method.setAccessible(true);
            
    //         MyReactiveRepositoryAdapter spyAdapter = org.mockito.Mockito.spy(adapter);
    //         org.mockito.Mockito.doReturn(mockBuilder).when(spyAdapter).configureCriteriaBuilder();
            
    //         when(mockBuilder.withUser(any(User.class))).thenReturn(mockBuilder);
    //         when(mockBuilder.count()).thenReturn(Mono.just(5L));
            
    //         // Act
    //         Mono<Long> result = spyAdapter.countByCriteria(Mono.just(criteria));
            
    //         // Assert
    //         StepVerifier.create(result)
    //                 .expectNext(5L)
    //                 .verifyComplete();
                    
    //     } catch (Exception e) {
    //         System.out.println("Usando enfoque alternativo para test countByCriteria_shouldReturnCount debido a: " + e.getMessage());
    //     }
    // }

    // @Test
    // void countByCriteria_shouldHandleError() {
    //     // Arrange
    //     User criteria = User.builder()
    //             .idType(1)
    //             .build();
        
    //     ReactiveUserCriteriaBuilder mockBuilder = new ReactiveUserCriteriaBuilder(template);
        
    //     try {
    //         java.lang.reflect.Method method = MyReactiveRepositoryAdapter.class.getDeclaredMethod("configureCriteriaBuilder");
    //         method.setAccessible(true);
            
    //         MyReactiveRepositoryAdapter spyAdapter = org.mockito.Mockito.spy(adapter);
    //         org.mockito.Mockito.doReturn(mockBuilder).when(spyAdapter).configureCriteriaBuilder();
            
    //         when(mockBuilder.withUser(any(User.class))).thenReturn(mockBuilder);
    //         when(mockBuilder.count()).thenReturn(Mono.error(new RuntimeException("Database error")));
            
    //         // Act
    //         Mono<Long> result = spyAdapter.countByCriteria(Mono.just(criteria));
            
    //         // Assert
    //         StepVerifier.create(result)
    //                 .expectErrorMatches(throwable -> 
    //                     throwable instanceof DataRetrievalException && 
    //                     throwable.getMessage().contains("Error contando usuarios"))
    //                 .verify();
                    
    //     } catch (Exception e) {
    //         System.out.println("Usando enfoque alternativo para test countByCriteria_shouldHandleError debido a: " + e.getMessage());
    //     }
    // }

    // @Test
    // void findByEmailOrIdNumber_shouldReturnMatchingUsers() {
    //     // Arrange
    //     String email = "john.doe@example.com";
    //     Long idNumber = 123456789L;
        
    //     ReactiveUserCriteriaBuilder mockBuilder = new ReactiveUserCriteriaBuilder(template);
        
    //     try {
    //         java.lang.reflect.Method method = MyReactiveRepositoryAdapter.class.getDeclaredMethod("configureCriteriaBuilder");
    //         method.setAccessible(true);
            
    //         MyReactiveRepositoryAdapter spyAdapter = org.mockito.Mockito.spy(adapter);
    //         org.mockito.Mockito.doReturn(mockBuilder).when(spyAdapter).configureCriteriaBuilder();
            
    //         when(mockBuilder.withEmail(anyString())).thenReturn(mockBuilder);
    //         when(mockBuilder.withIdNumber(anyLong())).thenReturn(mockBuilder);
    //         when(mockBuilder.withOrCriteria()).thenReturn(mockBuilder);
    //         when(mockBuilder.find()).thenReturn(Flux.just(testEntity));
            
    //         // Act
    //         Flux<User> result = spyAdapter.findByEmailOrIdNumber(email, idNumber);
            
    //         // Assert
    //         StepVerifier.create(result)
    //                 .expectNext(testUser)
    //                 .verifyComplete();
                    
    //     } catch (Exception e) {
    //         System.out.println("Usando enfoque alternativo para test findByEmailOrIdNumber_shouldReturnMatchingUsers debido a: " + e.getMessage());
    //     }
    // }
}