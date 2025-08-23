package co.com.crediya.cy_authentication.model.user;
import lombok.Builder;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private Long idNumber;
    private Integer idType;
    private String name;
    private String lastname;
    private LocalDateTime birthDate;
    private String address;
    private String phone;
    private String eMail;
    private Double baseSalary;
}
