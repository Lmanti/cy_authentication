package co.com.crediya.cy_authentication.model.role.gateways;

import co.com.crediya.cy_authentication.model.role.Role;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface RoleRepository {
    Flux<Role> getAllRoles();
    Mono<Role> getRoleById(Integer roleId);
}
