package co.com.crediya.cy_authentication.api.config;

import co.com.crediya.cy_authentication.model.security.TokenData;
import io.jsonwebtoken.security.WeakKeyException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenGeneratorTest {

  // 32 bytes (256 bits) en ASCII -> válido para HS256
  private static final String SECRET = "0123456789ABCDEF0123456789ABCDEF";

  @Test
  @DisplayName("Genera y verifica token correctamente")
  void shouldGenerateAndVerifyToken() {
    JwtTokenGenerator gen = new JwtTokenGenerator(SECRET);

    String token = gen.generate("user-123", List.of("ADMIN", "CLIENTE"), Duration.ofHours(1));
    assertNotNull(token);
    assertTrue(token.split("\\.").length == 3, "El JWT debe tener 3 segmentos");

    Optional<TokenData> dataOpt = gen.verify(token);
    assertTrue(dataOpt.isPresent());
    TokenData data = dataOpt.get();

    assertEquals("user-123", data.getSubject());
    // Ignorar orden de roles
    var roles = data.getRoles();
    assertEquals(2, roles.size());
    assertTrue(roles.contains("ADMIN"));
    assertTrue(roles.contains("CLIENTE"));
  }

  @Test
  @DisplayName("Devuelve empty para token inválido (no JWT)")
  void shouldReturnEmptyForInvalidToken() {
    JwtTokenGenerator gen = new JwtTokenGenerator(SECRET);

    assertTrue(gen.verify("not-a-jwt").isEmpty());
    assertTrue(gen.verify("abc.def.ghi").isEmpty());
  }

  @Test
  @DisplayName("Devuelve empty para token expirado")
  void shouldReturnEmptyForExpiredToken() {
    JwtTokenGenerator gen = new JwtTokenGenerator(SECRET);

    // TTL en el pasado
    String token = gen.generate("user-1", List.of("ADMIN"), Duration.ofSeconds(-1));
    assertTrue(gen.verify(token).isEmpty());
  }

  @Test
  @DisplayName("Devuelve empty para token manipulado")
  void shouldReturnEmptyForTamperedToken() {
    JwtTokenGenerator gen = new JwtTokenGenerator(SECRET);

    String token = gen.generate("user-1", List.of("ADMIN"), Duration.ofMinutes(5));
    // Modificar un carácter del token para romper la firma
    char[] chars = token.toCharArray();
    int pos = token.length() - 2; // penúltimo carácter
    chars[pos] = (chars[pos] == 'a') ? 'b' : 'a';
    String tampered = new String(chars);

    assertTrue(gen.verify(tampered).isEmpty());
  }

  @Test
  @DisplayName("Lanza WeakKeyException si el secret es débil (< 256 bits)")
  void shouldThrowForWeakSecret() {
    // 144 bits aprox -> inválido para HS256
    String weak = "too-short-secret-18bytes";
    assertThrows(WeakKeyException.class, () -> new JwtTokenGenerator(weak));
  }
}
