package com.babel.employeeservice.dto.request;

import com.babel.employeeservice.entity.Sex;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;

import static com.babel.employeeservice.constants.EmployeeConstants.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Payload used to create a new employee")
public class EmployeeRequest {

    @NotBlank(message = FIRST_NAME_REQUIRED)
    @Schema(example = "Maria")
    private String firstName;

    @Schema(example = "Guadalupe")
    private String middleName;

    @NotBlank(message = PATERNAL_LAST_NAME_REQUIRED)
    @Schema(example = "Gonzalez")
    private String paternalLastName;

    @Schema(example = "Perez")
    private String maternalLastName;

    @NotNull(message = AGE_REQUIRED)
    @Min(value = MIN_AGE, message = AGE_RANGE)
    @Max(value = MAX_AGE, message = AGE_RANGE)
    @Schema(example = "32")
    private Integer age;

    @Schema(description = "Defaults to UNSPECIFIED when not provided", example = "FEMALE")
    private Sex sex;

    @NotNull(message = BIRTH_DATE_REQUIRED)
    @Past(message = BIRTH_DATE_PAST)
    @JsonFormat(pattern = BIRTH_DATE_PATTERN)
    @Schema(example = "15-04-1992", pattern = BIRTH_DATE_PATTERN, type = "string")
    private LocalDate birthDate;

    @NotBlank(message = POSITION_REQUIRED)
    @Schema(example = "Software Engineer")
    private String position;

    @Schema(description = "Defaults to true when not provided", example = "true")
    private Boolean active;
}
