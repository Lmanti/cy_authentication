package co.com.crediya.cy_authentication.usecase.role;

import co.com.crediya.cy_authentication.model.role.Role;
import co.com.crediya.cy_authentication.model.role.gateways.RoleRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RoleUseCase {
    private final RoleRepository roleRepository;

    public Flux<Role> getAllRoles() {
        return roleRepository.getAllRoles();
    }

    public Mono<Role> getRoleById(Integer roleId) {
        return roleRepository.getRoleById(roleId);
    }

}
