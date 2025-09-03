package co.com.crediya.cy_authentication.api.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "DTO para editar un usuario")
public class EditUserDTO {
    @Schema(description = "Número de identificación del usuario", example = "1234567890")
    private Long idNumber;
    
    @Schema(description = "Tipo de identificación (1: CC, 2: CE, etc.)", example = "1")
    private Integer idTypeId;
    
    @Schema(description = "Nombre del usuario", example = "Juan")
    private String name;
    
    @Schema(description = "Apellido del usuario", example = "Pérez")
    private String lastname;
    
    @Schema(description = "Fecha de nacimiento", example = "1990-01-01")
    private LocalDate birthDate;
    
    @Schema(description = "Dirección del usuario", example = "Calle 123 #45-67")
    private String address;
    
    @Schema(description = "Teléfono del usuario", example = "3001234567")
    private String phone;
    
    @Schema(description = "Correo electrónico del usuario", example = "juan.perez@example.com")
    private String email;
    
    @Schema(description = "Salario base del usuario", example = "2500000.0")
    private Double baseSalary;

    @Schema(description = "Rol del usuario (1: Admin, 2: Asesor, etc.)", example = "1")
    private Integer roleId;

    @Schema(description = "Contraseña del usuario", example = "123456")
    private String password;
}
