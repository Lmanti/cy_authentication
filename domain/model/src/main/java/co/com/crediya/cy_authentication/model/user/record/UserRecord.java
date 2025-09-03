package co.com.crediya.cy_authentication.model.user.record;

import java.math.BigInteger;
import java.time.LocalDate;

import co.com.crediya.cy_authentication.model.idtype.IdType;
import co.com.crediya.cy_authentication.model.role.Role;

public record UserRecord (
    BigInteger id,
    Long idNumber,
    IdType idType,
    String name,
    String lastname,
    LocalDate birthDate,
    String address,
    String phone,
    String email,
    Double baseSalary,
    Role role,
    String password
) {}
