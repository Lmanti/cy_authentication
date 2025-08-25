package co.com.crediya.cy_authentication.model.user.gateways;

import java.util.Map;

import co.com.crediya.cy_authentication.common.CustomPageRequest;
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
    Flux<User> findByCriteria(Mono<User> userCriteria);
    Flux<User> findByCriteriaPaginated(User criteria, CustomPageRequest pageable);
    Mono<Long> countByCriteria(Mono<User> criteria);
    Flux<User> findByEmailOrIdNumber(String email, Long idNumber);
}
