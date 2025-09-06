package co.com.crediya.cy_authentication.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.crediya.cy_authentication.api.dto.CreateUserDTO;
import co.com.crediya.cy_authentication.api.dto.EditUserDTO;
import co.com.crediya.cy_authentication.api.dto.LoginRequest;
import co.com.crediya.cy_authentication.api.dto.UserDTO;
import co.com.crediya.cy_authentication.model.idtype.IdType;
import co.com.crediya.cy_authentication.model.role.Role;
import co.com.crediya.cy_authentication.model.security.JwtResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;

@Configuration
public class RouterRest {
    private final String userBaseRoute = "/api/v1/usuarios";
    private final String idTypesBaseRoute = "/parameters/idTypes";
    private final String rolesBaseRoute = "/parameters/roles";

    @Bean
    @RouterOperations({
        @RouterOperation(
            path = userBaseRoute, 
            method = RequestMethod.GET,
            operation = @Operation(
                operationId = "getAllUsers",
                tags = {"Usuarios"},
                summary = "Obtener todos los usuarios",
                description = "Retorna una lista con todos los usuarios registrados",
                responses = {
                    @ApiResponse(
                        responseCode = "200", 
                        description = "Lista de usuarios obtenida exitosamente",
                        content = @Content(schema = @Schema(implementation = UserDTO.class))
                    )
                }
            )
        ),
        @RouterOperation(
            path = userBaseRoute, 
            method = RequestMethod.POST,
            operation = @Operation(
                operationId = "createUser",
                tags = {"Usuarios"},
                summary = "Crear un nuevo usuario",
                description = "Crea un nuevo usuario en el sistema",
                requestBody = @RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateUserDTO.class))
                ),
                responses = {
                    @ApiResponse(
                        responseCode = "201", 
                        description = "Usuario creado exitosamente",
                        content = @Content(schema = @Schema(implementation = UserDTO.class))
                    ),
                    @ApiResponse(
                        responseCode = "400", 
                        description = "Datos de usuario inválidos"
                    )
                }
            )
        ),
        @RouterOperation(
            path = userBaseRoute, 
            method = RequestMethod.PUT,
            operation = @Operation(
                operationId = "updateUser",
                tags = {"Usuarios"},
                summary = "Actualizar un usuario",
                description = "Actualiza los datos de un usuario existente",
                requestBody = @RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = EditUserDTO.class))
                ),
                responses = {
                    @ApiResponse(
                        responseCode = "200", 
                        description = "Usuario actualizado exitosamente",
                        content = @Content(schema = @Schema(implementation = UserDTO.class))
                    ),
                    @ApiResponse(
                        responseCode = "404", 
                        description = "Usuario no encontrado"
                    )
                }
            )
        ),
        @RouterOperation(
            path = userBaseRoute + "/{idNumber}", 
            method = RequestMethod.DELETE,
            operation = @Operation(
                operationId = "deleteUser",
                tags = {"Usuarios"},
                summary = "Eliminar un usuario",
                description = "Elimina un usuario por su número de identificación",
                parameters = {
                    @Parameter(
                        name = "idNumber", 
                        description = "Número de identificación del usuario",
                        in = ParameterIn.PATH,
                        required = true,
                        schema = @Schema(type = "integer", format = "int64")
                    )
                },
                responses = {
                    @ApiResponse(
                        responseCode = "200", 
                        description = "Usuario eliminado exitosamente"
                    ),
                    @ApiResponse(
                        responseCode = "404", 
                        description = "Usuario no encontrado"
                    )
                }
            )
        ),
        @RouterOperation(
            path = userBaseRoute + "/exists/{idNumber}", 
            method = RequestMethod.GET,
            operation = @Operation(
                operationId = "existsByIdNumber",
                tags = {"Usuarios"},
                summary = "Valida si existe un usuario",
                description = "Valida si existe un usuario por su número de identificación",
                parameters = {
                    @Parameter(
                        name = "idNumber", 
                        description = "Número de identificación del usuario",
                        in = ParameterIn.PATH,
                        required = true,
                        schema = @Schema(type = "integer", format = "int64")
                    )
                },
                responses = {
                    @ApiResponse(
                        responseCode = "200", 
                        description = "Validación exitosa"
                    )
                }
            )
        ),
        @RouterOperation(
            path = userBaseRoute + idTypesBaseRoute, 
            method = RequestMethod.GET,
            operation = @Operation(
                operationId = "getAllIdTypes",
                tags = {"Parámetros"},
                summary = "Obtener todos los tipos de identificación",
                description = "Retorna una lista con todos los tipos de identificación registrados",
                responses = {
                    @ApiResponse(
                        responseCode = "200", 
                        description = "Lista de tipos de identificación obtenida exitosamente",
                        content = @Content(schema = @Schema(implementation = IdType.class))
                    )
                }
            )
        ),
        @RouterOperation(
            path = userBaseRoute + rolesBaseRoute, 
            method = RequestMethod.GET,
            operation = @Operation(
                operationId = "getAllRoles",
                tags = {"Parámetros"},
                summary = "Obtener todos los roles",
                description = "Retorna una lista con todos los roles registrados",
                responses = {
                    @ApiResponse(
                        responseCode = "200", 
                        description = "Lista de roles obtenida exitosamente",
                        content = @Content(schema = @Schema(implementation = Role.class))
                    )
                }
            )
        ),
        @RouterOperation(
            path = userBaseRoute + "/login", 
            method = RequestMethod.POST,
            operation = @Operation(
                operationId = "login",
                tags = {"Authentication"},
                summary = "Iniciar sesión",
                description = "Iniciar sesión con un usuario registrado en el sistema",
                requestBody = @RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginRequest.class))
                ),
                responses = {
                    @ApiResponse(
                        responseCode = "200", 
                        description = "Usuario ha iniciado sesión exitosamente",
                        content = @Content(schema = @Schema(implementation = JwtResponse.class))
                    ),
                    @ApiResponse(
                        responseCode = "401", 
                        description = "Credenciales inválidas"
                    )
                }
            )
        )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(GET(userBaseRoute), handler::getAllUsers)
            .andRoute(POST(userBaseRoute), handler::createUser)
            .andRoute(PUT(userBaseRoute), handler::updateUser)
            .andRoute(DELETE(userBaseRoute.concat("/{idNumber}")), handler::deleteUser)
            .andRoute(GET(userBaseRoute.concat("/exists/{idNumber}")), handler::existsByIdNumber)
            .andRoute(GET(userBaseRoute + idTypesBaseRoute), handler::getAllIdTypes)
            .andRoute(GET(userBaseRoute + rolesBaseRoute), handler::getAllRoles)
            .andRoute(POST(userBaseRoute.concat("/login")), handler::login);
    }
}
