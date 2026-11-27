package com.babel.employeeservice.repository;

import com.babel.employeeservice.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT e FROM Employee e WHERE "
            + "LOWER(e.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR "
            + "LOWER(e.middleName) LIKE LOWER(CONCAT('%', :name, '%')) OR "
            + "LOWER(e.paternalLastName) LIKE LOWER(CONCAT('%', :name, '%')) OR "
            + "LOWER(e.maternalLastName) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Employee> searchByNamePart(@Param("name") String name, Pageable pageable);
}
