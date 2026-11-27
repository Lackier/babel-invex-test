package com.babel.employeeservice.service;

import com.babel.employeeservice.dto.exception.EmployeeNotFoundException;
import com.babel.employeeservice.dto.exception.InvalidEmployeeDataException;
import com.babel.employeeservice.dto.request.EmployeeRequest;
import com.babel.employeeservice.dto.request.EmployeeUpdateRequest;
import com.babel.employeeservice.dto.response.EmployeeResponse;
import com.babel.employeeservice.entity.Employee;
import com.babel.employeeservice.entity.Sex;
import com.babel.employeeservice.mapper.EmployeeMapper;
import com.babel.employeeservice.repository.EmployeeRepository;
import com.babel.employeeservice.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.Period;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    private final EmployeeMapper employeeMapper = new EmployeeMapper();

    private EmployeeServiceImpl employeeService;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeServiceImpl(employeeRepository, employeeMapper);
        LocalDate birthDate = LocalDate.of(1994, 1, 10);
        employee = Employee.builder()
                .id(1L)
                .firstName("Maria")
                .paternalLastName("Gonzalez")
                .age(Period.between(birthDate, LocalDate.now()).getYears())
                .sex(Sex.FEMALE)
                .birthDate(birthDate)
                .position("Developer")
                .active(true)
                .build();
    }

    @Test
    void findAll_returnsMappedEmployees() {
        Pageable pageable = PageRequest.of(0, 20);
        when(employeeRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(employee)));

        Page<EmployeeResponse> result = employeeService.findAll(pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getFirstName()).isEqualTo("Maria");
    }

    @Test
    void findById_whenFound_returnsEmployee() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        EmployeeResponse result = employeeService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void findById_whenNotFound_throwsException() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.findById(99L))
                .isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void create_withEmptyList_throwsException() {
        assertThatThrownBy(() -> employeeService.create(Collections.emptyList()))
                .isInstanceOf(InvalidEmployeeDataException.class);

        verify(employeeRepository, never()).saveAll(anyList());
    }

    @Test
    void create_withValidRequests_savesAndReturnsEmployees() {
        LocalDate birthDate = LocalDate.of(1999, 5, 5);
        EmployeeRequest request = EmployeeRequest.builder()
                .firstName("Juan")
                .paternalLastName("Perez")
                .age(Period.between(birthDate, LocalDate.now()).getYears())
                .sex(Sex.MALE)
                .birthDate(birthDate)
                .position("Analyst")
                .build();
        when(employeeRepository.saveAll(anyList())).thenReturn(List.of(employee));

        List<EmployeeResponse> result = employeeService.create(List.of(request));

        assertThat(result).hasSize(1);
        verify(employeeRepository, times(1)).saveAll(anyList());
    }

    @Test
    void toEntity_withoutSex_defaultsToUnspecified() {
        EmployeeRequest request = EmployeeRequest.builder()
                .firstName("Juan")
                .paternalLastName("Perez")
                .age(25)
                .birthDate(LocalDate.of(1999, 5, 5))
                .position("Analyst")
                .build();

        Employee entity = employeeMapper.toEntity(request);

        assertThat(entity.getSex()).isEqualTo(Sex.UNSPECIFIED);
    }

    @Test
    void create_withAgeNotMatchingBirthDate_throwsException() {
        EmployeeRequest request = EmployeeRequest.builder()
                .firstName("Juan")
                .paternalLastName("Perez")
                .age(1)
                .sex(Sex.MALE)
                .birthDate(LocalDate.of(1999, 5, 5))
                .position("Analyst")
                .build();

        assertThatThrownBy(() -> employeeService.create(List.of(request)))
                .isInstanceOf(InvalidEmployeeDataException.class);

        verify(employeeRepository, never()).saveAll(anyList());
    }

    @Test
    void update_withPartialFields_mergesOnlyProvidedFields() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        EmployeeUpdateRequest update = EmployeeUpdateRequest.builder()
                .position("Senior Developer")
                .build();

        EmployeeResponse result = employeeService.update(1L, update);

        assertThat(result.getPosition()).isEqualTo("Senior Developer");
        assertThat(result.getFirstName()).isEqualTo("Maria");
    }

    @Test
    void update_withAbsentMiddleName_leavesFieldUnchanged() {
        employee.setMiddleName("Elena");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        EmployeeUpdateRequest update = EmployeeUpdateRequest.builder()
                .position("Senior Developer")
                .build();

        EmployeeResponse result = employeeService.update(1L, update);

        assertThat(result.getMiddleName()).isEqualTo("Elena");
    }

    @Test
    void update_withExplicitNullMiddleName_clearsField() {
        employee.setMiddleName("Elena");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        EmployeeUpdateRequest update = EmployeeUpdateRequest.builder()
                .middleName(JsonNullable.of(null))
                .build();

        EmployeeResponse result = employeeService.update(1L, update);

        assertThat(result.getMiddleName()).isNull();
    }

    @Test
    void update_withAgeNotMatchingExistingBirthDate_throwsException() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        EmployeeUpdateRequest update = EmployeeUpdateRequest.builder()
                .age(1)
                .build();

        assertThatThrownBy(() -> employeeService.update(1L, update))
                .isInstanceOf(InvalidEmployeeDataException.class);

        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void update_whenNotFound_throwsException() {
        when(employeeRepository.findById(eq(99L))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.update(99L, EmployeeUpdateRequest.builder().build()))
                .isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void delete_whenFound_deletesEmployee() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        employeeService.delete(1L);

        verify(employeeRepository, times(1)).delete(employee);
    }

    @Test
    void delete_whenNotFound_throwsException() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.delete(99L))
                .isInstanceOf(EmployeeNotFoundException.class);
    }

    @Test
    void search_withBlankName_throwsException() {
        assertThatThrownBy(() -> employeeService.search("  ", PageRequest.of(0, 20)))
                .isInstanceOf(InvalidEmployeeDataException.class);
    }

    @Test
    void search_withValidName_returnsMatches() {
        Pageable pageable = PageRequest.of(0, 20);
        when(employeeRepository.searchByNamePart("Mar", pageable)).thenReturn(new PageImpl<>(List.of(employee)));

        Page<EmployeeResponse> result = employeeService.search("Mar", pageable);

        assertThat(result.getContent()).hasSize(1);
    }
}
