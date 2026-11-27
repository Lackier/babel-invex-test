package com.babel.employeeservice.mapper;

import com.babel.employeeservice.dto.request.EmployeeRequest;
import com.babel.employeeservice.dto.response.EmployeeResponse;
import com.babel.employeeservice.dto.request.EmployeeUpdateRequest;
import com.babel.employeeservice.entity.Employee;
import com.babel.employeeservice.entity.Sex;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EmployeeMapper {

    public Employee toEntity(EmployeeRequest request) {
        return Employee.builder()
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .paternalLastName(request.getPaternalLastName())
                .maternalLastName(request.getMaternalLastName())
                .age(request.getAge())
                .sex(request.getSex() == null ? Sex.UNSPECIFIED : request.getSex())
                .birthDate(request.getBirthDate())
                .position(request.getPosition())
                .active(request.getActive() == null || request.getActive())
                .build();
    }

    public void applyUpdate(Employee employee, EmployeeUpdateRequest update) {
        if (update.getFirstName() != null) {
            employee.setFirstName(update.getFirstName());
        }
        if (update.getMiddleName().isPresent()) {
            employee.setMiddleName(update.getMiddleName().get());
        }
        if (update.getPaternalLastName() != null) {
            employee.setPaternalLastName(update.getPaternalLastName());
        }
        if (update.getMaternalLastName().isPresent()) {
            employee.setMaternalLastName(update.getMaternalLastName().get());
        }
        if (update.getAge() != null) {
            employee.setAge(update.getAge());
        }
        if (update.getSex() != null) {
            employee.setSex(update.getSex());
        }
        if (update.getBirthDate() != null) {
            employee.setBirthDate(update.getBirthDate());
        }
        if (update.getPosition() != null) {
            employee.setPosition(update.getPosition());
        }
        if (update.getActive() != null) {
            employee.setActive(update.getActive());
        }
    }

    public EmployeeResponse toResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .middleName(employee.getMiddleName())
                .paternalLastName(employee.getPaternalLastName())
                .maternalLastName(employee.getMaternalLastName())
                .age(employee.getAge())
                .sex(employee.getSex())
                .birthDate(employee.getBirthDate())
                .position(employee.getPosition())
                .registrationDate(employee.getRegistrationDate())
                .active(employee.isActive())
                .build();
    }

    public List<EmployeeResponse> toResponseList(List<Employee> employees) {
        return employees.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public Page<EmployeeResponse> toResponsePage(Page<Employee> employees) {
        return employees.map(this::toResponse);
    }
}
