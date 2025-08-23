package co.com.crediya.cy_authentication.usecase.user;

import co.com.crediya.cy_authentication.model.user.User;
import co.com.crediya.cy_authentication.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase implements IUserUseCase {
    private final UserRepository userRepository;

    @Override
    public Mono<User> saveUser(Mono<User> user) {
        return userRepository.saveUser(user);
    }

    @Override
    public Flux<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    @Override
    public Mono<User> getByIdNumber(Long idNumber) {
        return userRepository.getByIdNumber(idNumber);
    }

    @Override
    public Mono<User> editUser(Mono<User> user) {
        return userRepository.editUser(user);
    }

    @Override
    public Mono<Void> deleteUser(Long idNumber) {
        return userRepository.deleteUser(idNumber);
    }
}
