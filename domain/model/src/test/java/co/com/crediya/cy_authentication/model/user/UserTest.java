package co.com.crediya.cy_authentication.model.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

class UserTest {

    // @Test
    // void shouldCreateUserWithBuilder() {
    //     // Arrange & Act
    //     User user = User.builder()
    //             .idNumber(123456789L)
    //             .idType(1)
    //             .name("John")
    //             .lastname("Doe")
    //             .birthDate(LocalDate.of(1990, 1, 1))
    //             .address("123 Main St")
    //             .phone("1234567890")
    //             .email("john.doe@example.com")
    //             .baseSalary(5000.0)
    //             .username("johndoe")
    //             .password("password123")
    //             .build();

    //     // Assert
    //     assertEquals(123456789L, user.getIdNumber());
    //     assertEquals(1, user.getIdType());
    //     assertEquals("John", user.getName());
    //     assertEquals("Doe", user.getLastname());
    //     assertEquals(LocalDate.of(1990, 1, 1), user.getBirthDate());
    //     assertEquals("123 Main St", user.getAddress());
    //     assertEquals("1234567890", user.getPhone());
    //     assertEquals("john.doe@example.com", user.getEmail());
    //     assertEquals(5000.0, user.getBaseSalary());
    //     assertEquals("johndoe", user.getUsername());
    //     assertEquals("password123", user.getPassword());
    // }

    // @Test
    // void shouldCreateUserWithAllArgsConstructor() {
    //     // Arrange & Act
    //     User user = new User(
    //             123456789L,
    //             1,
    //             "John",
    //             "Doe",
    //             LocalDate.of(1990, 1, 1),
    //             "123 Main St",
    //             "1234567890",
    //             "john.doe@example.com",
    //             5000.0,
    //             "johndoe",
    //             "password123"
    //     );

    //     // Assert
    //     assertEquals(123456789L, user.getIdNumber());
    //     assertEquals(1, user.getIdType());
    //     assertEquals("John", user.getName());
    //     assertEquals("Doe", user.getLastname());
    //     assertEquals(LocalDate.of(1990, 1, 1), user.getBirthDate());
    //     assertEquals("123 Main St", user.getAddress());
    //     assertEquals("1234567890", user.getPhone());
    //     assertEquals("john.doe@example.com", user.getEmail());
    //     assertEquals(5000.0, user.getBaseSalary());
    //     assertEquals("johndoe", user.getUsername());
    //     assertEquals("password123", user.getPassword());
    // }

    // @Test
    // void shouldCreateUserWithNoArgsConstructorAndSetters() {
    //     // Arrange
    //     User user = new User();
        
    //     // Act
    //     user.setIdNumber(123456789L);
    //     user.setIdType(1);
    //     user.setName("John");
    //     user.setLastname("Doe");
    //     user.setBirthDate(LocalDate.of(1990, 1, 1));
    //     user.setAddress("123 Main St");
    //     user.setPhone("1234567890");
    //     user.setEmail("john.doe@example.com");
    //     user.setBaseSalary(5000.0);
    //     user.setUsername("johndoe");
    //     user.setPassword("password123");

    //     // Assert
    //     assertEquals(123456789L, user.getIdNumber());
    //     assertEquals(1, user.getIdType());
    //     assertEquals("John", user.getName());
    //     assertEquals("Doe", user.getLastname());
    //     assertEquals(LocalDate.of(1990, 1, 1), user.getBirthDate());
    //     assertEquals("123 Main St", user.getAddress());
    //     assertEquals("1234567890", user.getPhone());
    //     assertEquals("john.doe@example.com", user.getEmail());
    //     assertEquals(5000.0, user.getBaseSalary());
    //     assertEquals("johndoe", user.getUsername());
    //     assertEquals("password123", user.getPassword());
    // }

    // @Test
    // void shouldCreateCopyWithToBuilder() {
    //     // Arrange
    //     User originalUser = User.builder()
    //             .idNumber(123456789L)
    //             .idType(1)
    //             .name("John")
    //             .lastname("Doe")
    //             .email("john.doe@example.com")
    //             .username("johndoe")
    //             .build();

    //     // Act
    //     User modifiedUser = originalUser.toBuilder()
    //             .email("new.email@example.com")
    //             .phone("9876543210")
    //             .build();

    //     // Assert
    //     assertEquals(123456789L, modifiedUser.getIdNumber());
    //     assertEquals("John", modifiedUser.getName());
    //     assertEquals("new.email@example.com", modifiedUser.getEmail());
    //     assertEquals("9876543210", modifiedUser.getPhone());
    // }

    // @Test
    // void toStringShouldContainAllFields() {
    //     // Arrange
    //     User user = User.builder()
    //             .idNumber(123456789L)
    //             .name("John")
    //             .email("john.doe@example.com")
    //             .build();

    //     // Act
    //     String toString = user.toString();

    //     // Assert
    //     assertTrue(toString.contains("idNumber=123456789"));
    //     assertTrue(toString.contains("name=John"));
    //     assertTrue(toString.contains("email=john.doe@example.com"));
    // }
}