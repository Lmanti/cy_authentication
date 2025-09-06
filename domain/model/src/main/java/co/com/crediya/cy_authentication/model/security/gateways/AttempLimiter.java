package co.com.crediya.cy_authentication.model.security.gateways;

import reactor.core.publisher.Mono;

public interface AttempLimiter {
    Mono<Void> assertAllowed(String username, String ip);
    Mono<Void> onFailure(String username, String ip);
    Mono<Void> onSuccess(String username, String ip);
}
