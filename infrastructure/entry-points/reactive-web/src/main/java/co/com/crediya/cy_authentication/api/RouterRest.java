package co.com.crediya.cy_authentication.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    private String baseRoute = "/api/v1/usuarios";

    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(GET(baseRoute), handler::getAllUsers)
                .andRoute(POST(baseRoute), handler::updateUser)
                .andRoute(PUT(baseRoute), handler::createUser)
                .andRoute(DELETE(baseRoute.concat("/{idNumber}")), handler::createUser);
                // .and(route(GET("/api/otherusercase/path"), handler::listenGETOtherUseCase));
    }
}
