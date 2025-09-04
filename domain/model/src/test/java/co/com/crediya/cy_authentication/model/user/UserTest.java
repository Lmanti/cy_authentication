package co.com.crediya.cy_authentication.model.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigInteger;
import java.time.LocalDate;

class UserTest {

    @Test
    @DisplayName("Should create User with no-args constructor")
    void shouldCreateUserWithNoArgsConstructor() {
        // When
        User user = new User();

        // Then
        assertNotNull(user);
        assertNull(user.getId());
        assertNull(user.getIdNumber());
        assertNull(user.getIdTypeId());
        assertNull(user.getName());
        assertNull(user.getLastname());
        assertNull(user.getBirthDate());
        assertNull(user.getAddress());
        assertNull(user.getPhone());
        assertNull(user.getEmail());
        assertNull(user.getBaseSalary());
        assertNull(user.getRoleId());
        assertNull(user.getPassword());
    }

    @Test
    @DisplayName("Should create User with all-args constructor")
    void shouldCreateUserWithAllArgsConstructor() {
        // Given
        BigInteger id = BigInteger.valueOf(1);
        Long idNumber = 12345678L;
        Integer idTypeId = 1;
        String name = "John";
        String lastname = "Doe";
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        String address = "123 Main St";
        String phone = "555-1234";
        String email = "john.doe@example.com";
        Double baseSalary = 50000.0;
        Integer roleId = 2;
        String password = "password123";

        // When
        User user = new User(id, idNumber, idTypeId, name, lastname, birthDate, 
                           address, phone, email, baseSalary, roleId, password);

        // Then
        assertEquals(id, user.getId());
        assertEquals(idNumber, user.getIdNumber());
        assertEquals(idTypeId, user.getIdTypeId());
        assertEquals(name, user.getName());
        assertEquals(lastname, user.getLastname());
        assertEquals(birthDate, user.getBirthDate());
        assertEquals(address, user.getAddress());
        assertEquals(phone, user.getPhone());
        assertEquals(email, user.getEmail());
        assertEquals(baseSalary, user.getBaseSalary());
        assertEquals(roleId, user.getRoleId());
        assertEquals(password, user.getPassword());
    }

    @Test
    @DisplayName("Should create User with builder")
    void shouldCreateUserWithBuilder() {
        // Given
        BigInteger id = BigInteger.valueOf(1);
        Long idNumber = 12345678L;
        String name = "Jane";
        String email = "jane@example.com";
        Double baseSalary = 60000.0;

        // When
        User user = User.builder()
                .id(id)
                .idNumber(idNumber)
                .name(name)
                .email(email)
                .baseSalary(baseSalary)
                .build();

        // Then
        assertEquals(id, user.getId());
        assertEquals(idNumber, user.getIdNumber());
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(baseSalary, user.getBaseSalary());
        assertNull(user.getLastname());
        assertNull(user.getBirthDate());
    }

    @Test
    @DisplayName("Should create copy with toBuilder")
    void shouldCreateCopyWithToBuilder() {
        // Given
        User original = User.builder()
                .id(BigInteger.valueOf(1))
                .name("Original")
                .email("original@example.com")
                .baseSalary(50000.0)
                .build();

        // When
        User copy = original.toBuilder()
                .name("Modified")
                .baseSalary(60000.0)
                .build();

        // Then
        assertEquals(original.getId(), copy.getId());
        assertEquals(original.getEmail(), copy.getEmail());
        assertEquals("Modified", copy.getName());
        assertEquals(60000.0, copy.getBaseSalary());
        assertEquals("Original", original.getName());
        assertEquals(50000.0, original.getBaseSalary());
    }

    @Test
    @DisplayName("Should set and get properties")
    void shouldSetAndGetProperties() {
        // Given
        User user = new User();
        BigInteger id = BigInteger.valueOf(999);
        String name = "Test";
        String email = "test@example.com";
        LocalDate birthDate = LocalDate.of(2000, 12, 31);

        // When
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setBirthDate(birthDate);

        // Then
        assertEquals(id, user.getId());
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(birthDate, user.getBirthDate());
    }

    @Test
    @DisplayName("Should handle null values")
    void shouldHandleNullValues() {
        // Given
        User user = User.builder()
                .name("Test")
                .email("test@example.com")
                .build();

        // When
        user.setName(null);
        user.setEmail(null);
        user.setBirthDate(null);

        // Then
        assertNull(user.getName());
        assertNull(user.getEmail());
        assertNull(user.getBirthDate());
    }

    @Test
    @DisplayName("Should handle toString")
    void shouldHandleToString() {
        // Given
        User user = User.builder()
                .id(BigInteger.valueOf(1))
                .name("John")
                .email("john@example.com")
                .build();

        // When
        String toString = user.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains("User"));
        assertTrue(toString.contains("name=John"));
        assertTrue(toString.contains("email=john@example.com"));
    }
}