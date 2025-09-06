package co.com.crediya.cy_authentication.model.security;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class TokenData {
    private final String subject;
    private final List<String> roles;
}
