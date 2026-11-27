package com.babel.employeeservice.service.impl;

import com.babel.employeeservice.constants.EmployeeConstants;
import com.babel.employeeservice.dto.exception.EmployeeNotFoundException;
import com.babel.employeeservice.dto.exception.InvalidEmployeeDataException;
import com.babel.employeeservice.dto.request.EmployeeRequest;
import com.babel.employeeservice.dto.request.EmployeeUpdateRequest;
import com.babel.employeeservice.dto.response.EmployeeResponse;
import com.babel.employeeservice.entity.Employee;
import com.babel.employeeservice.mapper.EmployeeMapper;
import com.babel.employeeservice.repository.EmployeeRepository;
import com.babel.employeeservice.service.EmployeeService;
import com.babel.employeeservice.util.AgeBirthDateConsistency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.babel.employeeservice.constants.EmployeeConstants.AGE_BIRTH_DATE_MISMATCH;
import static com.babel.employeeservice.constants.EmployeeConstants.EMPLOYEE_LIST_NOT_EMPTY;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponse> findAll(Pageable pageable) {
        return employeeMapper.toResponsePage(employeeRepository.findAll(pageable));
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse findById(Long id) {
        return employeeMapper.toResponse(getEmployeeOrThrow(id));
    }

    @Override
    public List<EmployeeResponse> create(List<EmployeeRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new InvalidEmployeeDataException(EMPLOYEE_LIST_NOT_EMPTY);
        }

        requests.forEach(this::validateAgeMatchesBirthDate);

        List<Employee> employees = requests.stream()
                .map(employeeMapper::toEntity)
                .collect(Collectors.toList());

        List<Employee> saved = employeeRepository.saveAll(employees);
        log.info("Created {} employee(s)", saved.size());

        return employeeMapper.toResponseList(saved);
    }

    @Override
    public EmployeeResponse update(Long id, EmployeeUpdateRequest request) {
        Employee employee = getEmployeeOrThrow(id);

        employeeMapper.applyUpdate(employee, request);

        if (AgeBirthDateConsistency.isNotConsistent(employee.getAge(), employee.getBirthDate())) {
            throw new InvalidEmployeeDataException(AGE_BIRTH_DATE_MISMATCH);
        }

        Employee updated = employeeRepository.save(employee);
        log.info("Updated employee with id {}", id);

        return employeeMapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        Employee employee = getEmployeeOrThrow(id);

        employeeRepository.delete(employee);
        log.info("Deleted employee with id {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponse> search(String name, Pageable pageable) {
        if (!StringUtils.hasText(name)) {
            throw new InvalidEmployeeDataException(EmployeeConstants.SEARCH_NAME_REQUIRED);
        }

        return employeeMapper.toResponsePage(employeeRepository.searchByNamePart(name.trim(), pageable));
    }

    private Employee getEmployeeOrThrow(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    private void validateAgeMatchesBirthDate(EmployeeRequest request) {
        if (AgeBirthDateConsistency.isNotConsistent(request.getAge(), request.getBirthDate())) {
            throw new InvalidEmployeeDataException(AGE_BIRTH_DATE_MISMATCH);
        }
    }
}
