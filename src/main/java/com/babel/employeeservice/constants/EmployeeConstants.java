package com.babel.employeeservice.constants;

public final class EmployeeConstants {

    private EmployeeConstants() {
    }

    public static final String BIRTH_DATE_PATTERN = "dd-MM-yyyy";

    public static final int MIN_AGE = 0;
    public static final int MAX_AGE = 120;

    public static final String FIRST_NAME_REQUIRED = "firstName is required";
    public static final String PATERNAL_LAST_NAME_REQUIRED = "paternalLastName is required";
    public static final String AGE_REQUIRED = "age is required";
    public static final String AGE_RANGE = "age must be between " + MIN_AGE + " and " + MAX_AGE;
    public static final String BIRTH_DATE_REQUIRED = "birthDate is required";
    public static final String BIRTH_DATE_PAST = "birthDate must be in the past";
    public static final String POSITION_REQUIRED = "position is required";
    public static final String EMPLOYEE_LIST_NOT_EMPTY = "at least one employee must be provided";
    public static final String SEARCH_NAME_REQUIRED = "name query parameter is required";
    public static final String AGE_BIRTH_DATE_MISMATCH = "age does not match the number of full years since birthDate";

    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MIN_PAGE_SIZE = 1;
    public static final int MAX_PAGE_SIZE = 100;
}
