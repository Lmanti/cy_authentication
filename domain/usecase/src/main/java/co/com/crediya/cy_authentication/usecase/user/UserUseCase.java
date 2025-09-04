package co.com.crediya.cy_authentication.usecase.user;

import java.util.Map;
import java.util.regex.Pattern;

import co.com.crediya.cy_authentication.exception.InvalidUserDataException;
import co.com.crediya.cy_authentication.model.idtype.IdType;
import co.com.crediya.cy_authentication.model.idtype.gateways.IdTypeRepository;
import co.com.crediya.cy_authentication.model.role.Role;
import co.com.crediya.cy_authentication.model.role.gateways.RoleRepository;
import co.com.crediya.cy_authentication.model.user.User;
import co.com.crediya.cy_authentication.model.user.gateways.UserRepository;
import co.com.crediya.cy_authentication.model.user.record.UserRecord;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {
    private final UserRepository userRepository;
    private final IdTypeRepository idTypeRepository;
    private final RoleRepository roleRepository;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );
    private static final double MIN_SALARY = 0.0;
    private static final double MAX_SALARY = 15000000.0;

    private enum Mode {
        CREATE, UPDATE
    }

    public Mono<UserRecord> saveUser(Mono<User> user) {
        return user.flatMap(toValidate -> validateUserData(Mono.just(toValidate)))
            .flatMap(validUser -> validateUserUniqueness(Mono.just(validUser), Mode.CREATE))
            .flatMap(toSave -> 
                idTypeRepository.getIdTypeById(toSave.getIdTypeId())
                    .switchIfEmpty(Mono.error(new InvalidUserDataException("No existe un tipo identificación con id " + toSave.getIdTypeId())))
                    .zipWith(roleRepository.getRoleById(toSave.getRoleId())
                        .switchIfEmpty(Mono.error(new InvalidUserDataException("No existe un rol con id " + toSave.getRoleId()))))
                    .flatMap(params -> {
                        IdType idType = params.getT1();
                        Role role = params.getT2();

                        return userRepository.saveUser(Mono.just(toSave))
                            .map(savedUser -> new UserRecord(
                                savedUser.getId(),
                                savedUser.getIdNumber(),
                                idType,
                                savedUser.getName(),
                                savedUser.getLastname(),
                                savedUser.getBirthDate(),
                                savedUser.getAddress(),
                                savedUser.getPhone(),
                                savedUser.getEmail(),
                                savedUser.getBaseSalary(),
                                role,
                                savedUser.getPassword()));
                    })
            );
    }

    public Flux<UserRecord> getAllUsers() {
        return Flux.zip(
            idTypeRepository.getAllIdTypes().collectMap(IdType::getId),
            roleRepository.getAllRoles().collectMap(Role::getId))
            .flatMap(params -> {
                Map<Integer, IdType> idTypeMap = params.getT1();
                Map<Integer, Role> roleMap = params.getT2();

                return userRepository.getAllUsers()
                    .map(user -> new UserRecord(
                        user.getId(),
                        user.getIdNumber(),
                        idTypeMap.get(user.getIdTypeId()),
                        user.getName(),
                        user.getLastname(),
                        user.getBirthDate(),
                        user.getAddress(),
                        user.getPhone(),
                        user.getEmail(),
                        user.getBaseSalary(),
                        roleMap.get(user.getRoleId()),
                        user.getPassword()));
            });
    }

    public Mono<UserRecord> getByIdNumber(Long idNumber) {
        return userRepository.getByIdNumber(idNumber)
            .flatMap(user ->
                idTypeRepository.getIdTypeById(user.getIdTypeId())
                    .zipWith(roleRepository.getRoleById(user.getRoleId()))
                    .map(params -> new UserRecord(
                        user.getId(),
                        user.getIdNumber(),
                        params.getT1(),
                        user.getName(),
                        user.getLastname(),
                        user.getBirthDate(),
                        user.getAddress(),
                        user.getPhone(),
                        user.getEmail(),
                        user.getBaseSalary(),
                        params.getT2(),
                        user.getPassword()
                    ))
        );
    }

    public Mono<UserRecord> editUser(Mono<User> user) {
        return user.flatMap(toValidate -> validateUserData(Mono.just(toValidate)))
            .flatMap(validUser -> validateUserUniqueness(Mono.just(validUser), Mode.UPDATE))
            .flatMap(toEdit -> 
                idTypeRepository.getIdTypeById(toEdit.getIdTypeId())
                    .switchIfEmpty(Mono.error(new InvalidUserDataException("No existe un tipo identificación con id " + toEdit.getIdTypeId())))
                    .zipWith(roleRepository.getRoleById(toEdit.getRoleId())
                        .switchIfEmpty(Mono.error(new InvalidUserDataException("No existe un rol con id " + toEdit.getRoleId()))))
                    .flatMap(params -> {
                        IdType idType = params.getT1();
                        Role role = params.getT2();

                        return userRepository.editUser(Mono.just(toEdit))
                            .map(updatedUser -> new UserRecord(
                                updatedUser.getId(),
                                updatedUser.getIdNumber(),
                                idType,
                                updatedUser.getName(),
                                updatedUser.getLastname(),
                                updatedUser.getBirthDate(),
                                updatedUser.getAddress(),
                                updatedUser.getPhone(),
                                updatedUser.getEmail(),
                                updatedUser.getBaseSalary(),
                                role,
                                updatedUser.getPassword()));
                    })
            );
    }

    public Mono<Void> deleteUser(Long idNumber) {
        return userRepository.deleteUser(idNumber);
    }

    private Mono<User> validateUserData(Mono<User> userMono) {
        return userMono.flatMap(user -> {
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

            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                return Mono.error(new InvalidUserDataException("La contraseña es requerida"));
            }
            
            return Mono.just(user);
        });
    }

    private Mono<User> validateUserUniqueness(Mono<User> userMono, Mode mode) {
        return userMono.flatMap(user -> 
            userRepository.findByEmailOrIdNumber(user.getEmail(), user.getIdNumber())
                .flatMap(existingUser -> {
                    Boolean existingEmail = existingUser.getEmail().equals(user.getEmail());
                    Boolean existingIdNumber = existingUser.getIdNumber().equals(user.getIdNumber());

                    if (existingEmail && existingIdNumber && mode.equals(Mode.CREATE)) {
                        return Mono.error(new InvalidUserDataException("El correo electrónico y el número de identificación ya han sido registrados por otro usuario"));
                    } else if (!existingEmail && !existingIdNumber && mode.equals(Mode.UPDATE)) {
                        return Mono.error(new InvalidUserDataException("No se pueden cambiar el correo electrónico ni el número de identificación que ya han sido registrados por el usuario"));
                    } else if (existingEmail && mode.equals(Mode.CREATE)) {
                        return Mono.error(new InvalidUserDataException("El correo electrónico ya ha sido registrado por otro usuario"));
                    } else if (existingIdNumber && mode.equals(Mode.CREATE)) {
                        return Mono.error(new InvalidUserDataException("El número de identificación ya ha sido registrado por otro usuario"));
                    } else if (!existingEmail && existingIdNumber && mode.equals(Mode.UPDATE)) {
                        return Mono.error(new InvalidUserDataException("No se puede cambiar el correo electrónico registrado por el usuario"));
                    } else if (existingEmail && !existingIdNumber && mode.equals(Mode.UPDATE)) {
                        return Mono.error(new InvalidUserDataException("No se puede cambiar el número de identificación registrado por el usuario"));
                    } else {
                        return Mono.just(mode.equals(Mode.UPDATE) ? user : existingUser);
                    }
                })
                .switchIfEmpty(Mono.defer(() -> Mono.just(user)))
        );
    }
}
