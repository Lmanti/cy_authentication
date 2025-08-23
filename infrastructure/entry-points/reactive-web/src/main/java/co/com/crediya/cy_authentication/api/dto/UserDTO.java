package co.com.crediya.cy_authentication.api.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDTO {
    private Long idNumber;
    private Integer idType;
    private String name;
    private String lastname;
    private LocalDate birthDate;
    private String address;
    private String phone;
    private String eMail;
    private Double baseSalary;
}
