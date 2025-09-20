package co.com.crediya.cy_authentication.api;

import lombok.RequiredArgsConstructor;

import java.math.BigInteger;
import java.net.URI;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.crediya.cy_authentication.api.dto.CreateUserDTO;
import co.com.crediya.cy_authentication.api.dto.EditUserDTO;
import co.com.crediya.cy_authentication.api.dto.LoginRequest;
import co.com.crediya.cy_authentication.api.mapper.UserDTOMapper;
import co.com.crediya.cy_authentication.exception.InvalidCredentialsException;
import co.com.crediya.cy_authentication.usecase.authenticateuser.AuthenticateUserUseCase;
import co.com.crediya.cy_authentication.usecase.idtype.IdTypeUseCase;
import co.com.crediya.cy_authentication.usecase.role.RoleUseCase;
import co.com.crediya.cy_authentication.usecase.user.UserUseCase;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {
    private final UserUseCase userUseCase;
    private final IdTypeUseCase idTypeUseCase;
    private final RoleUseCase roleUseCase;
    private final UserDTOMapper userMapper;
    private final AuthenticateUserUseCase authenticateUserUseCase;

    public Mono<ServerResponse> getAllUsers(ServerRequest serverRequest) {
        return userUseCase.getAllUsers().collectList()
            .flatMap(userList -> ServerResponse.ok().bodyValue(userMapper.toResponseList(userList)));
    }

    public Mono<ServerResponse> getUsersBasicInfo(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(new ParameterizedTypeReference<List<String>>() {})
            .flatMap(emails -> userUseCase.getUsersByEmail(emails)
                .collectList()
                .flatMap(userList -> ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(userMapper.toResponseBasicList(userList))));
    }

    public Mono<ServerResponse> updateUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(EditUserDTO.class)
            .map(userMapper::toModel)
            .transform(userMono -> userUseCase.editUser(userMono))
            .flatMap(updatedUser -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userMapper.toResponse(updatedUser)));
    }

    public Mono<ServerResponse> createUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CreateUserDTO.class)
            .map(userMapper::toModel)
            .transform(userMono -> userUseCase.saveUser(userMono))
            .flatMap(createdUser -> ServerResponse.created(URI.create("/detallesUsuario/" + createdUser.idNumber()))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userMapper.toResponse(createdUser)));
    }

    public Mono<ServerResponse> deleteUser(ServerRequest serverRequest) {
        return userUseCase.deleteUser(Long.parseLong(serverRequest.pathVariable("idNumber")))
            .then(ServerResponse.ok().build());
    }

    public Mono<ServerResponse> getByidNumber(ServerRequest serverRequest) {
        return userUseCase.getByIdNumber(Long.parseLong(serverRequest.pathVariable("idNumber")))
            .flatMap(user -> ServerResponse.ok().bodyValue(userMapper.toResponseBasic(user)));
    }

    public Mono<ServerResponse> getByEmail(ServerRequest serverRequest) {      
        return userUseCase.getUserByEmail(serverRequest.pathVariable("userEmail"))
            .flatMap(user -> ServerResponse.ok().bodyValue(userMapper.toResponseBasic(user)));
    }

    public Mono<ServerResponse> getUserByToken(ServerRequest serverRequest) {      
        String authHeader = serverRequest.headers().firstHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                .bodyValue(new InvalidCredentialsException("Token de autorización no proporcionado o inválido"));
        }

        String token = authHeader.substring(7);
        return authenticateUserUseCase.extractTokenData(token)
            .flatMap(tokenData -> userUseCase.getUserById(BigInteger.valueOf(Long.parseLong(tokenData.getSubject())))
                .flatMap(user -> ServerResponse.ok().bodyValue(userMapper.toResponseBasic(user))));
    }

    public Mono<ServerResponse> getAllIdTypes(ServerRequest serverRequest) {
        return idTypeUseCase.getAllIdTypes().collectList()
            .flatMap(idTypeList -> ServerResponse.ok().bodyValue(idTypeList));
    }

    public Mono<ServerResponse> getAllRoles(ServerRequest serverRequest) {
        return roleUseCase.getAllRoles().collectList()
            .flatMap(rolesList -> ServerResponse.ok().bodyValue(rolesList));
    }

    public Mono<ServerResponse> login(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(LoginRequest.class)
            .flatMap(loginRequest ->
                authenticateUserUseCase.handle(loginRequest.getUsername(), loginRequest.getPassword())
                .flatMap(jwt -> ServerResponse.ok().bodyValue(jwt))
            );
    }
}
