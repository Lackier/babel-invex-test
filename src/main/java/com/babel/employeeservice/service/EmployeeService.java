package com.babel.employeeservice.service;

import com.babel.employeeservice.dto.request.EmployeeRequest;
import com.babel.employeeservice.dto.response.EmployeeResponse;
import com.babel.employeeservice.dto.request.EmployeeUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeService {

    Page<EmployeeResponse> findAll(Pageable pageable);

    EmployeeResponse findById(Long id);

    List<EmployeeResponse> create(List<EmployeeRequest> requests);

    EmployeeResponse update(Long id, EmployeeUpdateRequest request);

    void delete(Long id);

    Page<EmployeeResponse> search(String name, Pageable pageable);
}
