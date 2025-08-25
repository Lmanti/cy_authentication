package co.com.crediya.cy_authentication.r2dbc.entity;

import java.math.BigInteger;
import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Table("users")
public class UserEntity {
    @Id
    private BigInteger id;
    @Column("id_number")
    private Long idNumber;
    @Column("id_type")
    private Integer idType;
    @Column
    private String name;
    @Column
    private String lastname;
    @Column("birth_date")
    private LocalDate birthDate;
    @Column
    private String address;
    @Column
    private String phone;
    @Column
    private String email;
    @Column("base_salary")
    private Double baseSalary;
}