package co.com.crediya.cy_authentication.model.idtype;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class IdTypeTest {

    @Test
    @DisplayName("Should create IdType with no-args constructor")
    void shouldCreateIdTypeWithNoArgsConstructor() {
        // When
        IdType idType = new IdType();

        // Then
        assertNotNull(idType);
        assertNull(idType.getId());
        assertNull(idType.getName());
        assertNull(idType.getDescription());
    }

    @Test
    @DisplayName("Should create IdType with all-args constructor")
    void shouldCreateIdTypeWithAllArgsConstructor() {
        // Given
        Integer id = 1;
        String name = "CC";
        String description = "Cédula de Ciudadanía";

        // When
        IdType idType = new IdType(id, name, description);

        // Then
        assertEquals(id, idType.getId());
        assertEquals(name, idType.getName());
        assertEquals(description, idType.getDescription());
    }

    @Test
    @DisplayName("Should create IdType with builder")
    void shouldCreateIdTypeWithBuilder() {
        // Given
        Integer id = 2;
        String name = "TI";
        String description = "Tarjeta de Identidad";

        // When
        IdType idType = IdType.builder()
                .id(id)
                .name(name)
                .description(description)
                .build();

        // Then
        assertEquals(id, idType.getId());
        assertEquals(name, idType.getName());
        assertEquals(description, idType.getDescription());
    }

    @Test
    @DisplayName("Should create copy with toBuilder")
    void shouldCreateCopyWithToBuilder() {
        // Given
        IdType original = IdType.builder()
                .id(1)
                .name("CC")
                .description("Original description")
                .build();

        // When
        IdType copy = original.toBuilder()
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
        IdType idType = new IdType();

        // When
        idType.setId(3);
        idType.setName("CE");
        idType.setDescription("Cédula de Extranjería");

        // Then
        assertEquals(Integer.valueOf(3), idType.getId());
        assertEquals("CE", idType.getName());
        assertEquals("Cédula de Extranjería", idType.getDescription());
    }

    @Test
    @DisplayName("Should handle null values")
    void shouldHandleNullValues() {
        // Given
        IdType idType = IdType.builder()
                .id(1)
                .name("TEST")
                .description("Test")
                .build();

        // When
        idType.setId(null);
        idType.setName(null);
        idType.setDescription(null);

        // Then
        assertNull(idType.getId());
        assertNull(idType.getName());
        assertNull(idType.getDescription());
    }
}