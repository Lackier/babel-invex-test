package com.babel.employeeservice.util;

import java.time.LocalDate;
import java.time.Period;

public final class AgeBirthDateConsistency {

    private AgeBirthDateConsistency() {
    }

    public static boolean isNotConsistent(Integer age, LocalDate birthDate) {
        if (age == null || birthDate == null) {
            return false;
        }
        return age != Period.between(birthDate, LocalDate.now()).getYears();
    }
}
