package co.com.crediya.cy_authentication.api.config;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import co.com.crediya.cy_authentication.model.security.Attempt;
import co.com.crediya.cy_authentication.model.security.gateways.AttempLimiter;
import reactor.core.publisher.Mono;

@Component
public class LoginAttemptLimiter implements AttempLimiter {
    private final ConcurrentHashMap<String, Attempt> attempts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> locks = new ConcurrentHashMap<>();

    private final int maxAttempts = 3;
    private final long windowMillis = Duration.ofMinutes(15).toMillis();
    private final long lockMillis = Duration.ofMinutes(15).toMillis();

    private String key(String username, String ip) {
        return "u:" + Objects.toString(username, "unknown") + "|ip:" + Objects.toString(ip, "unknown");
    }

    @Override
    public Mono<Void> assertAllowed(String username, String ip) {
        return Mono.fromRunnable(() -> {
            String k = key(username, ip);
            Long until = locks.get(k);
            long now = System.currentTimeMillis();
            if (until != null) {
                if (until > now) throw new RuntimeException("Cuenta bloqueada temporalmente por múltiples intentos fallidos");
                // lock expirado, limpiar
                locks.remove(k, until);
            }
        });
    }

    @Override
    public Mono<Void> onFailure(String username, String ip) {
        return Mono.fromRunnable(() -> {
            String k = key(username, ip);
            long now = System.currentTimeMillis();
            attempts.compute(k, (kk, st) -> {
                if (st == null || now - st.getFirstAttemptAt() > windowMillis) {
                    st = new Attempt(1, now);
                } else {
                    st.setCount(st.getCount() + 1);
                }
                if (st.getCount() >= maxAttempts) {
                    locks.put(k, now + lockMillis);
                    return null; // resetea ventana luego del bloqueo
                }
                return st;
            });
        });
    }

    @Override
    public Mono<Void> onSuccess(String username, String ip) {
        return Mono.fromRunnable(() -> {
            String k = key(username, ip);
            attempts.remove(k);
            locks.remove(k);
        });
    }  
}
