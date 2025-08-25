package co.com.crediya.cy_authentication.usecase.user;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import co.com.crediya.cy_authentication.exception.InvalidUserDataException;
import co.com.crediya.cy_authentication.model.user.User;
import co.com.crediya.cy_authentication.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase implements IUserUseCase {
    private final UserRepository userRepository;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );
    private static final double MIN_SALARY = 0.0;
    private static final double MAX_SALARY = 15000000.0;

    @Override
    public Mono<User> saveUser(Mono<User> user) {
        return user.flatMap(this::validateUserData)
            .flatMap(this::validateUserUniqueness)
            .transform(userRepository::saveUser);
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
        return user.flatMap(this::validateUserData)
            .flatMap(this::validateUserUniqueness)
            .transform(userRepository::editUser);
    }

    @Override
    public Mono<Void> deleteUser(Long idNumber) {
        return userRepository.deleteUser(idNumber);
    }

    private Mono<User> validateUserData(User user) {
        if (user.getIdNumber() == null || user.getIdNumber().equals(0L)) {
            return Mono.error(new InvalidUserDataException("El número de identificación es requerido"));
        }

        if (user.getName() == null || user.getName().trim().isEmpty()) {
            return Mono.error(new InvalidUserDataException("El nombre es requerido"));
        }
        
        if (user.getLastname() == null || user.getLastname().trim().isEmpty()) {
            return Mono.error(new InvalidUserDataException("El apellido es requerido"));
        }
        
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return Mono.error(new InvalidUserDataException("El correo electrónico es requerido"));
        }

        if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            return Mono.error(new InvalidUserDataException("El formato del correo electrónico no es válido"));
        }
        
        if (user.getBaseSalary() == null) {
            return Mono.error(new InvalidUserDataException("El salario base es requerido"));
        }
        
        if (user.getBaseSalary() < MIN_SALARY || user.getBaseSalary() > MAX_SALARY) {
            return Mono.error(new InvalidUserDataException(
                String.format("El salario base debe estar entre %,.2f y %,.2f", MIN_SALARY, MAX_SALARY)));
        }
        
        return Mono.just(user);
    }

    private Mono<User> validateUserUniqueness(User user) {
        return userRepository.findByEmailOrIdNumber(user.getEmail(), user.getIdNumber())
            .collectList()
            .flatMap(existingUsers -> {
                List<User> conflictingUsers = existingUsers.stream()
                    .filter(existingUser -> {
                        // Si es el mismo usuario (update), no hay conflicto
                        if (existingUser.getIdNumber().equals(user.getIdNumber()) && 
                            existingUser.getEmail().equals(user.getEmail())) {
                            return false;
                        }
                        
                        // Si tiene el mismo email o el mismo idNumber, puede haber conflicto
                        return existingUser.getEmail().equals(user.getEmail()) || 
                            existingUser.getIdNumber().equals(user.getIdNumber());
                    })
                    .collect(Collectors.toList());
                
                if (conflictingUsers.isEmpty()) {
                    return Mono.just(user);
                }
                
                for (User conflictingUser : conflictingUsers) {
                    if (conflictingUser.getEmail().equals(user.getEmail())) {
                        return Mono.error(new InvalidUserDataException(
                            "El correo electrónico ya está registrado por otro usuario"));
                    }
                    if (conflictingUser.getIdNumber().equals(user.getIdNumber())) {
                        return Mono.error(new InvalidUserDataException(
                            "El número de identificación ya está registrado por otro usuario"));
                    }
                }
                
                return Mono.just(user);
            });
    }
}
