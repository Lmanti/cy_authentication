package co.com.crediya.cy_authentication.usecase.user;

import co.com.crediya.cy_authentication.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IUserUseCase {
    Mono<User> saveUser(User user);
    Flux<User> getAllUsers();
    Mono<User> getByIdNumber(Long idNumber);
    Mono<User> editUser(User user);
    void deleteUser(Long idNumber); 
}
