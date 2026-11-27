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
import org.openapitools.jackson.nullable.JsonNullable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Past;
import java.time.LocalDate;

/**
 * Every field is optional: only non-null fields are applied to the stored employee,
 * allowing full or partial updates through PUT /employees/{id}. middleName and
 * maternalLastName use JsonNullable so an explicit {@code null} (clear the field) can be
 * told apart from the key simply being absent (leave the field untouched).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Payload used to update an employee; any subset of fields may be sent")
public class EmployeeUpdateRequest {

    private String firstName;

    @Builder.Default
    @Schema(type = "string", description = "Omit to leave unchanged, send null to clear it")
    private JsonNullable<String> middleName = JsonNullable.undefined();

    private String paternalLastName;

    @Builder.Default
    @Schema(type = "string", description = "Omit to leave unchanged, send null to clear it")
    private JsonNullable<String> maternalLastName = JsonNullable.undefined();

    @Min(value = EmployeeConstants.MIN_AGE, message = EmployeeConstants.AGE_RANGE)
    @Max(value = EmployeeConstants.MAX_AGE, message = EmployeeConstants.AGE_RANGE)
    private Integer age;

    private Sex sex;

    @Past(message = EmployeeConstants.BIRTH_DATE_PAST)
    @JsonFormat(pattern = EmployeeConstants.BIRTH_DATE_PATTERN)
    @Schema(pattern = EmployeeConstants.BIRTH_DATE_PATTERN, type = "string")
    private LocalDate birthDate;

    private String position;

    private Boolean active;
}
