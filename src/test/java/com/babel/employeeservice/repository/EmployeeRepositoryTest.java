package com.babel.employeeservice.repository;

import com.babel.employeeservice.entity.Employee;
import com.babel.employeeservice.entity.Sex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    void setUp() {
        employeeRepository.saveAll(List.of(
                Employee.builder()
                        .firstName("Maria")
                        .middleName("Elena")
                        .paternalLastName("Gonzalez")
                        .maternalLastName("Lopez")
                        .age(30)
                        .sex(Sex.FEMALE)
                        .birthDate(LocalDate.of(1994, 1, 10))
                        .position("Developer")
                        .active(true)
                        .build(),
                Employee.builder()
                        .firstName("Juan")
                        .paternalLastName("Martinez")
                        .maternalLastName("Ramirez")
                        .age(40)
                        .sex(Sex.MALE)
                        .birthDate(LocalDate.of(1984, 3, 20))
                        .position("Manager")
                        .active(true)
                        .build()
        ));
    }

    @Test
    void searchByNamePart_matchesFirstName() {
        List<Employee> result = employeeRepository.searchByNamePart("mar", PageRequest.of(0, 20)).getContent();

        assertThat(result).extracting(Employee::getFirstName)
                .containsExactlyInAnyOrder("Maria", "Juan");
    }

    @Test
    void searchByNamePart_matchesPaternalLastName() {
        List<Employee> result = employeeRepository.searchByNamePart("gonzalez", PageRequest.of(0, 20)).getContent();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFirstName()).isEqualTo("Maria");
    }

    @Test
    void searchByNamePart_noMatch_returnsEmpty() {
        List<Employee> result = employeeRepository.searchByNamePart("xyz", PageRequest.of(0, 20)).getContent();

        assertThat(result).isEmpty();
    }

    @Test
    void prePersist_setsRegistrationDate() {
        List<Employee> all = employeeRepository.findAll();

        assertThat(all).allMatch(e -> e.getRegistrationDate() != null);
    }
}
