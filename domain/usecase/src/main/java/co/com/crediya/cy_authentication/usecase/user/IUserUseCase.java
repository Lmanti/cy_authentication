package co.com.crediya.cy_authentication.usecase.user;

import co.com.crediya.cy_authentication.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IUserUseCase {
    Mono<User> saveUser(Mono<User> user);
    Flux<User> getAllUsers();
    Mono<User> getByIdNumber(Long idNumber);
    Mono<User> editUser(Mono<User> user);
    Mono<Void> deleteUser(Long idNumber);
}
