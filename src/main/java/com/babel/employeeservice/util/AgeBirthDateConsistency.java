package com.babel.employeeservice.util;

import java.time.LocalDate;
import java.time.Period;

public final class AgeBirthDateConsistency {

    private AgeBirthDateConsistency() {
    }

    public static boolean isConsistent(Integer age, LocalDate birthDate) {
        if (age == null || birthDate == null) {
            return true;
        }
        return age == Period.between(birthDate, LocalDate.now()).getYears();
    }
}
