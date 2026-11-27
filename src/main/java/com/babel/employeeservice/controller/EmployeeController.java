package com.babel.employeeservice.controller;

import com.babel.employeeservice.constants.EmployeeConstants;
import com.babel.employeeservice.dto.request.EmployeeRequest;
import com.babel.employeeservice.dto.response.EmployeeResponse;
import com.babel.employeeservice.dto.request.EmployeeUpdateRequest;
import com.babel.employeeservice.dto.exception.ApiError;
import com.babel.employeeservice.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/v1/employees")
@Validated
@Tag(name = "Employees", description = "Operations for managing employees")
public class EmployeeController {

    private static final String NOT_FOUND_DESCRIPTION = "Employee not found";
    private static final String BAD_REQUEST_DESCRIPTION = "Validation error";

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    @Operation(summary = "List all employees (paginated)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employees listed successfully"),
            @ApiResponse(responseCode = "400", description = BAD_REQUEST_DESCRIPTION,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<Page<EmployeeResponse>> getAll(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "" + EmployeeConstants.DEFAULT_PAGE_SIZE)
            @Min(EmployeeConstants.MIN_PAGE_SIZE) @Max(EmployeeConstants.MAX_PAGE_SIZE) int size) {
        return ResponseEntity.ok(employeeService.findAll(PageRequest.of(page, size)));
    }

    @GetMapping("/search")
    @Operation(summary = "Search employees by partial name match (paginated)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search executed successfully"),
            @ApiResponse(responseCode = "400", description = BAD_REQUEST_DESCRIPTION,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<Page<EmployeeResponse>> search(
            @RequestParam @Parameter(description = "Partial name to search for") String name,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "" + EmployeeConstants.DEFAULT_PAGE_SIZE)
            @Min(EmployeeConstants.MIN_PAGE_SIZE) @Max(EmployeeConstants.MAX_PAGE_SIZE) int size) {
        return ResponseEntity.ok(employeeService.search(name, PageRequest.of(page, size)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an employee by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee found"),
            @ApiResponse(responseCode = "404", description = NOT_FOUND_DESCRIPTION,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<EmployeeResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create one or more employees")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Employees created successfully"),
            @ApiResponse(responseCode = "400", description = BAD_REQUEST_DESCRIPTION,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<List<EmployeeResponse>> create(@Valid @RequestBody List<@Valid EmployeeRequest> requests) {
        List<EmployeeResponse> created = employeeService.create(requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update all or some fields of an employee")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee updated successfully"),
            @ApiResponse(responseCode = "400", description = BAD_REQUEST_DESCRIPTION,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "404", description = NOT_FOUND_DESCRIPTION,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<EmployeeResponse> update(@PathVariable Long id,
                                                    @Valid @RequestBody EmployeeUpdateRequest request) {
        return ResponseEntity.ok(employeeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an employee by id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Employee deleted successfully", content = @Content()),
            @ApiResponse(responseCode = "404", description = NOT_FOUND_DESCRIPTION,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ApiError.class)))
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
