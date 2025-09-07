package co.com.crediya.cy_authentication.api.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BCryptPasswordHasherTest {

  @Test
  @DisplayName("hash y matches funcionan con BCrypt real")
  void hashAndMatchesWithRealBCrypt() {
    PasswordEncoder realEncoder = new BCryptPasswordEncoder(10);
    BCryptPasswordHasher hasher = new BCryptPasswordHasher(realEncoder);

    String raw = "SuperSecret123!";
    String hash1 = hasher.hash(raw);
    String hash2 = hasher.hash(raw);

    assertNotNull(hash1);
    assertNotEquals(raw, hash1, "El hash no debe ser igual al raw");
    assertTrue(hasher.matches(raw, hash1), "Debe coincidir con el hash generado");
    assertTrue(hasher.matches(raw, hash2), "Debe coincidir con otro hash del mismo raw");
    assertNotEquals(hash1, hash2, "Bcrypt usa salt, hashes distintos para mismo raw");
    assertFalse(hasher.matches("otra", hash1), "No debe coincidir con raw distinto");
  }

  @Test
  @DisplayName("Delegación a PasswordEncoder (mock) en hash y matches")
  void delegatesToPasswordEncoder() {
    PasswordEncoder encoder = Mockito.mock(PasswordEncoder.class);
    when(encoder.encode("a")).thenReturn("ENC");
    when(encoder.matches("a", "H")).thenReturn(true);
    when(encoder.matches("b", "H")).thenReturn(false);

    BCryptPasswordHasher hasher = new BCryptPasswordHasher(encoder);

    String hashed = hasher.hash("a");
    assertEquals("ENC", hashed);
    verify(encoder, times(1)).encode("a");

    assertTrue(hasher.matches("a", "H"));
    assertFalse(hasher.matches("b", "H"));
    verify(encoder, times(1)).matches("a", "H");
    verify(encoder, times(1)).matches("b", "H");

    // No se llama a encode/ matches con otros argumentos
    verify(encoder, times(1)).encode(any());
  }
}
