package co.com.crediya.cy_authentication.r2dbc.entity;

import java.math.BigInteger;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;
    @Column(unique = true)
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
