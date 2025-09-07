package co.com.crediya.cy_authentication.model.security;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenTest {

  @Test
  void constructorAndGetterWork() {
    JwtToken jwt = new JwtToken("abc123");
    assertEquals("abc123", jwt.getToken());
    // Llamado extra para ejecutar el getter más de una vez
    assertEquals("abc123", jwt.getToken());
  }

  @Test
  void allowsNullToken() {
    JwtToken jwt = new JwtToken(null);
    assertNull(jwt.getToken());
  }

  @Test
  void noSetterGeneratedForFinalField() {
    Method[] methods = JwtToken.class.getMethods();
    boolean hasSetter = Arrays.stream(methods).anyMatch(m -> m.getName().equals("setToken"));
    assertFalse(hasSetter, "JwtToken no debe exponer setToken() porque el campo es final");
  }

  @Test
  void differentInstancesAreNotEqual() {
    JwtToken a = new JwtToken("x");
    JwtToken b = new JwtToken("x");
    assertNotEquals(a, b, "Sin @EqualsAndHashCode, objetos distintos no deben ser equals");
  }
}
