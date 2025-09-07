package co.com.crediya.cy_authentication.api;

import co.com.crediya.cy_authentication.api.dto.CreateUserDTO;
import co.com.crediya.cy_authentication.api.dto.EditUserDTO;
import co.com.crediya.cy_authentication.api.dto.UserDTO;
import co.com.crediya.cy_authentication.api.mapper.UserDTOMapper;
import co.com.crediya.cy_authentication.model.idtype.IdType;
import co.com.crediya.cy_authentication.model.role.Role;
import co.com.crediya.cy_authentication.model.user.User;
import co.com.crediya.cy_authentication.model.user.record.UserRecord;
import co.com.crediya.cy_authentication.usecase.authenticateuser.AuthenticateUserUseCase;
import co.com.crediya.cy_authentication.usecase.idtype.IdTypeUseCase;
import co.com.crediya.cy_authentication.usecase.role.RoleUseCase;
import co.com.crediya.cy_authentication.usecase.user.UserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class HandlerTest {

    @Mock
    private UserUseCase userUseCase;
    
    @Mock
    private IdTypeUseCase idTypeUseCase;
    
    @Mock
    private RoleUseCase roleUseCase;
    
    @Mock
    private UserDTOMapper userMapper;
    
    @Mock
    private ServerRequest serverRequest;

    @Mock
    private AuthenticateUserUseCase authenticateUserUseCase;

    private Handler handler;

    private UserRecord userRecord;
    private UserDTO userDTO;
    private CreateUserDTO createUserDTO;
    private EditUserDTO editUserDTO;
    private User user;
    private IdType idType;
    private Role role;

    @BeforeEach
    void setUp() {
        handler = new Handler(userUseCase, idTypeUseCase, roleUseCase, userMapper, authenticateUserUseCase);

        // Setup test data
        idType = IdType.builder()
                .id(1)
                .name("CC")
                .description("Cédula de Ciudadanía")
                .build();

        role = Role.builder()
                .id(1)
                .name("USER")
                .description("Standard user")
                .build();

        user = User.builder()
                .id(BigInteger.valueOf(1))
                .idNumber(12345678L)
                .idTypeId(1)
                .name("John")
                .lastname("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .email("john.doe@example.com")
                .baseSalary(3000000.0)
                .roleId(1)
                .password("password123")
                .build();

        userRecord = new UserRecord(
                user.getId(),
                user.getIdNumber(),
                idType,
                user.getName(),
                user.getLastname(),
                user.getBirthDate(),
                user.getAddress(),
                user.getPhone(),
                user.getEmail(),
                user.getBaseSalary(),
                role,
                user.getPassword()
        );

        userDTO = new UserDTO();
        userDTO.setIdNumber(user.getIdNumber());
        userDTO.setIdType(idType);
        userDTO.setName(user.getName());
        userDTO.setLastname(user.getLastname());
        userDTO.setBirthDate(user.getBirthDate());
        userDTO.setEmail(user.getEmail());
        userDTO.setBaseSalary(user.getBaseSalary());
        userDTO.setRole(role);

        createUserDTO = new CreateUserDTO();
        createUserDTO.setIdNumber(user.getIdNumber());
        createUserDTO.setIdTypeId(user.getIdTypeId());
        createUserDTO.setName(user.getName());
        createUserDTO.setLastname(user.getLastname());
        createUserDTO.setBirthDate(user.getBirthDate());
        createUserDTO.setEmail(user.getEmail());
        createUserDTO.setBaseSalary(user.getBaseSalary());
        createUserDTO.setRoleId(user.getRoleId());
        createUserDTO.setPassword(user.getPassword());

        editUserDTO = new EditUserDTO();
        editUserDTO.setIdNumber(user.getIdNumber());
        editUserDTO.setIdTypeId(user.getIdTypeId());
        editUserDTO.setName(user.getName());
        editUserDTO.setLastname(user.getLastname());
        editUserDTO.setBirthDate(user.getBirthDate());
        editUserDTO.setEmail(user.getEmail());
        editUserDTO.setBaseSalary(user.getBaseSalary());
        editUserDTO.setRoleId(user.getRoleId());
        editUserDTO.setPassword(user.getPassword());
    }

    @Test
    @DisplayName("Should get all users successfully")
    void shouldGetAllUsersSuccessfully() {
        // Given
        List<UserRecord> userRecords = Arrays.asList(userRecord);
        List<UserDTO> userDTOs = Arrays.asList(userDTO);

        when(userUseCase.getAllUsers()).thenReturn(Flux.fromIterable(userRecords));
        when(userMapper.toResponseList(userRecords)).thenReturn(userDTOs);

        // When
        Mono<ServerResponse> response = handler.getAllUsers(serverRequest);

        // Then
        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode() == HttpStatus.OK
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should create user successfully")
    void shouldCreateUserSuccessfully() {
        // Given
        when(serverRequest.bodyToMono(CreateUserDTO.class)).thenReturn(Mono.just(createUserDTO));
        when(userMapper.toModel(createUserDTO)).thenReturn(user);
        when(userUseCase.saveUser(any())).thenReturn(Mono.just(userRecord));
        when(userMapper.toResponse(userRecord)).thenReturn(userDTO);

        // When
        Mono<ServerResponse> response = handler.createUser(serverRequest);

        // Then
        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode() == HttpStatus.CREATED
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUserSuccessfully() {
        // Given
        when(serverRequest.bodyToMono(EditUserDTO.class)).thenReturn(Mono.just(editUserDTO));
        when(userMapper.toModel(editUserDTO)).thenReturn(user);
        when(userUseCase.editUser(any())).thenReturn(Mono.just(userRecord));
        when(userMapper.toResponse(userRecord)).thenReturn(userDTO);

        // When
        Mono<ServerResponse> response = handler.updateUser(serverRequest);

        // Then
        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode() == HttpStatus.OK
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUserSuccessfully() {
        // Given
        when(serverRequest.pathVariable("idNumber")).thenReturn("12345678");
        when(userUseCase.deleteUser(12345678L)).thenReturn(Mono.empty());

        // When
        Mono<ServerResponse> response = handler.deleteUser(serverRequest);

        // Then
        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode() == HttpStatus.OK
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should get all id types successfully")
    void shouldGetAllIdTypesSuccessfully() {
        // Given
        List<IdType> idTypes = Arrays.asList(idType);
        when(idTypeUseCase.getAllIdTypes()).thenReturn(Flux.fromIterable(idTypes));

        // When
        Mono<ServerResponse> response = handler.getAllIdTypes(serverRequest);

        // Then
        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode() == HttpStatus.OK
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Should get all roles successfully")
    void shouldGetAllRolesSuccessfully() {
        // Given
        List<Role> roles = Arrays.asList(role);
        when(roleUseCase.getAllRoles()).thenReturn(Flux.fromIterable(roles));

        // When
        Mono<ServerResponse> response = handler.getAllRoles(serverRequest);

        // Then
        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> 
                    serverResponse.statusCode() == HttpStatus.OK
                )
                .verifyComplete();
    }
}