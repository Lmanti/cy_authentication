package co.com.crediya.cy_authentication.api.dto;

public record UserBasicInfo(
    Long idNumber,
    String name,
    String lastname,
    String email,
    Double baseSalary
) {}
