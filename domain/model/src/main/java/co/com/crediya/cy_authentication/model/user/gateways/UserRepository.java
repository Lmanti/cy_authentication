package co.com.crediya.cy_authentication.model.user.gateways;

import co.com.crediya.cy_authentication.model.user.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<User> saveUser(Mono<User> user);
    Flux<User> getAllUsers();
    Mono<User> getByIdNumber(Long idNumber);
    Mono<User> getByEmail(String email);
    Mono<User> editUser(Mono<User> user);
    Mono<Void> deleteUser(Long idNumber);
    Mono<User> findByEmailOrIdNumber(String email, Long idNumber);
    Mono<Boolean> existByIdNumber(Long idNumber);
}
