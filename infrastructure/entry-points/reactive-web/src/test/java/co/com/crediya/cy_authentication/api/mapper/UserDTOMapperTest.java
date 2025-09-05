package co.com.crediya.cy_authentication.api.mapper;

import co.com.crediya.cy_authentication.api.dto.CreateUserDTO;
import co.com.crediya.cy_authentication.api.dto.EditUserDTO;
import co.com.crediya.cy_authentication.api.dto.UserDTO;
import co.com.crediya.cy_authentication.model.idtype.IdType;
import co.com.crediya.cy_authentication.model.role.Role;
import co.com.crediya.cy_authentication.model.user.User;
import co.com.crediya.cy_authentication.model.user.record.UserRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mapstruct.factory.Mappers;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserDTOMapperTest {

    private UserDTOMapper mapper;

    private UserRecord userRecord;
    private CreateUserDTO createUserDTO;
    private EditUserDTO editUserDTO;
    private IdType idType;
    private Role role;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(UserDTOMapper.class);

        idType = IdType.builder()
                .id(1)
                .name("CC")
                .description("Cédula de Ciudadanía")
                .build();

        role = Role.builder()
                .id(1)
                .name("USER")
                .description("Standard user")
                .build();

        userRecord = new UserRecord(
                BigInteger.valueOf(1),
                12345678L,
                idType,
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1),
                "123 Main St",
                "555-1234",
                "john.doe@example.com",
                3000000.0,
                role,
                "password123"
        );

        createUserDTO = new CreateUserDTO();
        createUserDTO.setIdNumber(12345678L);
        createUserDTO.setIdTypeId(1);
        createUserDTO.setName("John");
        createUserDTO.setLastname("Doe");
        createUserDTO.setBirthDate(LocalDate.of(1990, 1, 1));
        createUserDTO.setAddress("123 Main St");
        createUserDTO.setPhone("555-1234");
        createUserDTO.setEmail("john.doe@example.com");
        createUserDTO.setBaseSalary(3000000.0);
        createUserDTO.setRoleId(1);
        createUserDTO.setPassword("password123");

        editUserDTO = new EditUserDTO();
        editUserDTO.setIdNumber(12345678L);
        editUserDTO.setIdTypeId(1);
        editUserDTO.setName("John");
        editUserDTO.setLastname("Doe");
        editUserDTO.setBirthDate(LocalDate.of(1990, 1, 1));
        editUserDTO.setAddress("123 Main St");
        editUserDTO.setPhone("555-1234");
        editUserDTO.setEmail("john.doe@example.com");
        editUserDTO.setBaseSalary(3000000.0);
        editUserDTO.setRoleId(1);
        editUserDTO.setPassword("password123");
    }

    @Test
    @DisplayName("Should map UserRecord to UserDTO")
    void shouldMapUserRecordToUserDTO() {
        // When
        UserDTO result = mapper.toResponse(userRecord);

        // Then
        assertNotNull(result);
        assertEquals(userRecord.idNumber(), result.getIdNumber());
        assertEquals(userRecord.idType(), result.getIdType());
        assertEquals(userRecord.name(), result.getName());
        assertEquals(userRecord.lastname(), result.getLastname());
        assertEquals(userRecord.birthDate(), result.getBirthDate());
        assertEquals(userRecord.address(), result.getAddress());
        assertEquals(userRecord.phone(), result.getPhone());
        assertEquals(userRecord.email(), result.getEmail());
        assertEquals(userRecord.baseSalary(), result.getBaseSalary());
        assertEquals(userRecord.role(), result.getRole());
        assertEquals(userRecord.password(), result.getPassword());
    }

    @Test
    @DisplayName("Should map list of UserRecord to list of UserDTO")
    void shouldMapListOfUserRecordToListOfUserDTO() {
        // Given
        List<UserRecord> userRecords = Arrays.asList(userRecord);

        // When
        List<UserDTO> result = mapper.toResponseList(userRecords);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        
        UserDTO userDTO = result.get(0);
        assertEquals(userRecord.idNumber(), userDTO.getIdNumber());
        assertEquals(userRecord.name(), userDTO.getName());
        assertEquals(userRecord.email(), userDTO.getEmail());
    }

    @Test
    @DisplayName("Should map CreateUserDTO to User model")
    void shouldMapCreateUserDTOToUserModel() {
        // When
        User result = mapper.toModel(createUserDTO);

        // Then
        assertNotNull(result);
        assertNull(result.getId()); // Should be ignored
        assertEquals(createUserDTO.getIdNumber(), result.getIdNumber());
        assertEquals(createUserDTO.getIdTypeId(), result.getIdTypeId());
        assertEquals(createUserDTO.getName(), result.getName());
        assertEquals(createUserDTO.getLastname(), result.getLastname());
        assertEquals(createUserDTO.getBirthDate(), result.getBirthDate());
        assertEquals(createUserDTO.getAddress(), result.getAddress());
        assertEquals(createUserDTO.getPhone(), result.getPhone());
        assertEquals(createUserDTO.getEmail(), result.getEmail());
        assertEquals(createUserDTO.getBaseSalary(), result.getBaseSalary());
        assertEquals(createUserDTO.getRoleId(), result.getRoleId());
        assertEquals(createUserDTO.getPassword(), result.getPassword());
    }

    @Test
    @DisplayName("Should map EditUserDTO to User model")
    void shouldMapEditUserDTOToUserModel() {
        // When
        User result = mapper.toModel(editUserDTO);

        // Then
        assertNotNull(result);
        assertNull(result.getId()); // Should be ignored
        assertEquals(editUserDTO.getIdNumber(), result.getIdNumber());
        assertEquals(editUserDTO.getIdTypeId(), result.getIdTypeId());
        assertEquals(editUserDTO.getName(), result.getName());
        assertEquals(editUserDTO.getLastname(), result.getLastname());
        assertEquals(editUserDTO.getBirthDate(), result.getBirthDate());
        assertEquals(editUserDTO.getAddress(), result.getAddress());
        assertEquals(editUserDTO.getPhone(), result.getPhone());
        assertEquals(editUserDTO.getEmail(), result.getEmail());
        assertEquals(editUserDTO.getBaseSalary(), result.getBaseSalary());
        assertEquals(editUserDTO.getRoleId(), result.getRoleId());
        assertEquals(editUserDTO.getPassword(), result.getPassword());
    }

    @Test
    @DisplayName("Should handle null UserRecord")
    void shouldHandleNullUserRecord() {
        // When
        UserDTO result = mapper.toResponse(null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should handle empty list")
    void shouldHandleEmptyList() {
        // Given
        List<UserRecord> emptyList = Arrays.asList();

        // When
        List<UserDTO> result = mapper.toResponseList(emptyList);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle null CreateUserDTO")
    void shouldHandleNullCreateUserDTO() {
        // When
        User result = mapper.toModel((CreateUserDTO) null);

        // Then
        assertNull(result);
    }

    @Test
    @DisplayName("Should handle null EditUserDTO")
    void shouldHandleNullEditUserDTO() {
        // When
        User result = mapper.toModel((EditUserDTO) null);

        // Then
        assertNull(result);
    }
}