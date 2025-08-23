package co.com.crediya.cy_authentication.api;

import lombok.RequiredArgsConstructor;

import java.net.URI;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.crediya.cy_authentication.api.dto.CreateUserDTO;
import co.com.crediya.cy_authentication.api.dto.EditUserDTO;
import co.com.crediya.cy_authentication.api.mapper.UserDTOMapper;
import co.com.crediya.cy_authentication.usecase.user.IUserUseCase;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {
    private final IUserUseCase userUseCase;
    private final UserDTOMapper userMapper;

    public Mono<ServerResponse> getAllUsers(ServerRequest serverRequest) {
        return userUseCase.getAllUsers().collectList()
            .flatMap(userList -> ServerResponse.ok().bodyValue(userMapper.toResponseList(userList)));
    }

    public Mono<ServerResponse> updateUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(EditUserDTO.class)
            .map(userMapper::toModel)
            .transform(userMono -> userUseCase.editUser(userMono))
            .flatMap(updatedUser -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userMapper.toResponse(updatedUser)))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> createUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(CreateUserDTO.class)
            .map(userMapper::toModel)
            .transform(userMono -> userUseCase.saveUser(userMono))
            .flatMap(createdUser -> ServerResponse.created(URI.create("/userDetails/" + createdUser.getIdNumber()))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userMapper.toResponse(createdUser)))
            .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> deleteUser(ServerRequest serverRequest) {
        userUseCase.deleteUser(Long.parseLong(serverRequest.pathVariable("idNumber")));
        return ServerResponse.ok().build();
    }
}
