package com.babel.employeeservice.dto.request;

import com.babel.employeeservice.constants.EmployeeConstants;
import com.babel.employeeservice.entity.Sex;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Payload used to create a new employee")
public class EmployeeRequest {

    @NotBlank(message = EmployeeConstants.FIRST_NAME_REQUIRED)
    @Schema(example = "Maria")
    private String firstName;

    @Schema(example = "Guadalupe")
    private String middleName;

    @NotBlank(message = EmployeeConstants.PATERNAL_LAST_NAME_REQUIRED)
    @Schema(example = "Gonzalez")
    private String paternalLastName;

    @Schema(example = "Perez")
    private String maternalLastName;

    @NotNull(message = EmployeeConstants.AGE_REQUIRED)
    @Min(value = EmployeeConstants.MIN_AGE, message = EmployeeConstants.AGE_RANGE)
    @Max(value = EmployeeConstants.MAX_AGE, message = EmployeeConstants.AGE_RANGE)
    @Schema(example = "32")
    private Integer age;

    @Schema(description = "Defaults to UNSPECIFIED when not provided", example = "FEMALE")
    private Sex sex;

    @NotNull(message = EmployeeConstants.BIRTH_DATE_REQUIRED)
    @Past(message = EmployeeConstants.BIRTH_DATE_PAST)
    @JsonFormat(pattern = EmployeeConstants.BIRTH_DATE_PATTERN)
    @Schema(example = "15-04-1992", pattern = EmployeeConstants.BIRTH_DATE_PATTERN, type = "string")
    private LocalDate birthDate;

    @NotBlank(message = EmployeeConstants.POSITION_REQUIRED)
    @Schema(example = "Software Engineer")
    private String position;

    @Schema(description = "Defaults to true when not provided", example = "true")
    private Boolean active;
}
