package co.com.crediya.cy_authentication.model.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class JwtToken {
    private final String token;
}
