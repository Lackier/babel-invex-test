package com.babel.employeeservice.dto.response;

import com.babel.employeeservice.constants.EmployeeConstants;
import com.babel.employeeservice.entity.Sex;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Employee as returned by the API")
public class EmployeeResponse {

    private Long id;
    private String firstName;
    private String middleName;
    private String paternalLastName;
    private String maternalLastName;
    private Integer age;
    private Sex sex;

    @JsonFormat(pattern = EmployeeConstants.BIRTH_DATE_PATTERN)
    @Schema(pattern = EmployeeConstants.BIRTH_DATE_PATTERN, type = "string")
    private LocalDate birthDate;

    private String position;

    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime registrationDate;

    private boolean active;
}
