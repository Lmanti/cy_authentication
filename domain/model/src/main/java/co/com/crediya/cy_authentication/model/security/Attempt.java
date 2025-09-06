package co.com.crediya.cy_authentication.model.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class Attempt {
    private int count;
    private long firstAttemptAt;
}
