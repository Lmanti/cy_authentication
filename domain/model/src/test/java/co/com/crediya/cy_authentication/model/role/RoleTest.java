package co.com.crediya.cy_authentication.model.role;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    @DisplayName("Should create Role with no-args constructor")
    void shouldCreateRoleWithNoArgsConstructor() {
        // When
        Role role = new Role();

        // Then
        assertNotNull(role);
        assertNull(role.getId());
        assertNull(role.getName());
        assertNull(role.getDescription());
    }

    @Test
    @DisplayName("Should create Role with all-args constructor")
    void shouldCreateRoleWithAllArgsConstructor() {
        // Given
        Integer id = 1;
        String name = "ADMIN";
        String description = "Administrator role";

        // When
        Role role = new Role(id, name, description);

        // Then
        assertEquals(id, role.getId());
        assertEquals(name, role.getName());
        assertEquals(description, role.getDescription());
    }

    @Test
    @DisplayName("Should create Role with builder")
    void shouldCreateRoleWithBuilder() {
        // Given
        Integer id = 2;
        String name = "USER";
        String description = "Standard user role";

        // When
        Role role = Role.builder()
                .id(id)
                .name(name)
                .description(description)
                .build();

        // Then
        assertEquals(id, role.getId());
        assertEquals(name, role.getName());
        assertEquals(description, role.getDescription());
    }

    @Test
    @DisplayName("Should create copy with toBuilder")
    void shouldCreateCopyWithToBuilder() {
        // Given
        Role original = Role.builder()
                .id(1)
                .name("ADMIN")
                .description("Original description")
                .build();

        // When
        Role copy = original.toBuilder()
                .description("Modified description")
                .build();

        // Then
        assertEquals(original.getId(), copy.getId());
        assertEquals(original.getName(), copy.getName());
        assertEquals("Modified description", copy.getDescription());
        assertEquals("Original description", original.getDescription());
    }

    @Test
    @DisplayName("Should set and get properties")
    void shouldSetAndGetProperties() {
        // Given
        Role role = new Role();

        // When
        role.setId(3);
        role.setName("MANAGER");
        role.setDescription("Manager role");

        // Then
        assertEquals(Integer.valueOf(3), role.getId());
        assertEquals("MANAGER", role.getName());
        assertEquals("Manager role", role.getDescription());
    }

    @Test
    @DisplayName("Should handle null values")
    void shouldHandleNullValues() {
        // Given
        Role role = Role.builder()
                .id(1)
                .name("TEST")
                .description("Test")
                .build();

        // When
        role.setId(null);
        role.setName(null);
        role.setDescription(null);

        // Then
        assertNull(role.getId());
        assertNull(role.getName());
        assertNull(role.getDescription());
    }
}