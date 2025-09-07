package co.com.crediya.cy_authentication.model.security;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TokenDataTest {

  @Test
  void constructorAndGettersWork() {
    List<String> roles = new ArrayList<>(List.of("ADMIN", "USER"));
    TokenData td = new TokenData("sub-123", roles);

    assertEquals("sub-123", td.getSubject());
    assertEquals(roles, td.getRoles());
    // misma referencia (no hay copia defensiva)
    assertSame(roles, td.getRoles());
  }

  @Test
  void allowsNullValues() {
    TokenData td = new TokenData(null, null);
    assertNull(td.getSubject());
    assertNull(td.getRoles());
  }

  @Test
  void noSettersGeneratedForFinalFields() {
    Method[] methods = TokenData.class.getMethods();
    boolean hasSetSubject = Arrays.stream(methods).anyMatch(m -> m.getName().equals("setSubject"));
    boolean hasSetRoles = Arrays.stream(methods).anyMatch(m -> m.getName().equals("setRoles"));
    assertFalse(hasSetSubject, "TokenData no debe exponer setSubject() porque el campo es final");
    assertFalse(hasSetRoles, "TokenData no debe exponer setRoles() porque el campo es final");
  }

  @Test
  void differentInstancesAreNotEqual() {
    TokenData a = new TokenData("id", List.of("ADMIN"));
    TokenData b = new TokenData("id", List.of("ADMIN"));
    assertNotEquals(a, b, "Sin @EqualsAndHashCode, objetos distintos no deben ser equals");
  }
}
