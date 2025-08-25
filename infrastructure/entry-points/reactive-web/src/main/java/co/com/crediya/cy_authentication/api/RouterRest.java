package co.com.crediya.cy_authentication.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import co.com.crediya.cy_authentication.api.dto.CreateUserDTO;
import co.com.crediya.cy_authentication.api.dto.EditUserDTO;
import co.com.crediya.cy_authentication.api.dto.UserDTO;
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
    private String baseRoute = "/api/v1/usuarios";

    @Bean
    @RouterOperations({
        @RouterOperation(
            path = "/api/v1/usuarios", 
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
            path = "/api/v1/usuarios", 
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
            path = "/api/v1/usuarios", 
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
            path = "/api/v1/usuarios/{idNumber}", 
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
        )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(GET(baseRoute), handler::getAllUsers)
                .andRoute(POST(baseRoute), handler::createUser)
                .andRoute(PUT(baseRoute), handler::updateUser)
                .andRoute(DELETE(baseRoute.concat("/{idNumber}")), handler::deleteUser);
                // .and(route(GET("/api/otherusercase/path"), handler::listenGETOtherUseCase));
    }
}
