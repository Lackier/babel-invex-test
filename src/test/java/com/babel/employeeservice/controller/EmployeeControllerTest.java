package com.babel.employeeservice.controller;

import com.babel.employeeservice.config.JacksonConfig;
import com.babel.employeeservice.dto.exception.EmployeeNotFoundException;
import com.babel.employeeservice.dto.response.EmployeeResponse;
import com.babel.employeeservice.entity.Sex;
import com.babel.employeeservice.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
@Import(JacksonConfig.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    private EmployeeResponse sampleResponse() {
        return EmployeeResponse.builder()
                .id(1L)
                .firstName("Maria")
                .paternalLastName("Gonzalez")
                .age(30)
                .sex(Sex.FEMALE)
                .birthDate(LocalDate.of(1994, 1, 10))
                .position("Developer")
                .active(true)
                .build();
    }

    private Page<EmployeeResponse> samplePage() {
        return new PageImpl<>(List.of(sampleResponse()));
    }

    @Test
    void getAll_returnsOkWithList() throws Exception {
        when(employeeService.findAll(any(Pageable.class))).thenReturn(samplePage());

        mockMvc.perform(get("/v1/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].firstName").value("Maria"));
    }

    @Test
    void getAll_withNegativePage_returns400() throws Exception {
        mockMvc.perform(get("/v1/employees").param("page", "-1010"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAll_withOversizedPageSize_returns400() throws Exception {
        mockMvc.perform(get("/v1/employees").param("size", "1000000"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getById_whenFound_returnsOk() throws Exception {
        when(employeeService.findById(1L)).thenReturn(sampleResponse());

        mockMvc.perform(get("/v1/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getById_whenNotFound_returns404() throws Exception {
        when(employeeService.findById(99L)).thenThrow(new EmployeeNotFoundException(99L));

        mockMvc.perform(get("/v1/employees/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getById_withNonNumericId_returns400() throws Exception {
        mockMvc.perform(get("/v1/employees/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_withValidPayload_returns201() throws Exception {
        when(employeeService.create(any())).thenReturn(List.of(sampleResponse()));

        String body = "[{"
                + "\"firstName\":\"Maria\","
                + "\"paternalLastName\":\"Gonzalez\","
                + "\"age\":30,"
                + "\"sex\":\"FEMALE\","
                + "\"birthDate\":\"10-01-1994\","
                + "\"position\":\"Developer\"}]";

        mockMvc.perform(post("/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].firstName").value("Maria"));
    }

    @Test
    void create_withMissingRequiredField_returns400() throws Exception {
        String body = "[{\"age\":30}]";

        mockMvc.perform(post("/v1/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_withPartialPayload_returnsOk() throws Exception {
        when(employeeService.update(eq(1L), any())).thenReturn(sampleResponse());

        mockMvc.perform(put("/v1/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"position\":\"Senior Developer\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void update_withoutMiddleNameKey_passesUndefinedToService() throws Exception {
        when(employeeService.update(eq(1L), any())).thenReturn(sampleResponse());

        mockMvc.perform(put("/v1/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"position\":\"Senior Developer\"}"))
                .andExpect(status().isOk());

        verify(employeeService).update(eq(1L), argThat(req -> !req.getMiddleName().isPresent()));
    }

    @Test
    void update_withExplicitNullMiddleName_passesPresentNullToService() throws Exception {
        when(employeeService.update(eq(1L), any())).thenReturn(sampleResponse());

        mockMvc.perform(put("/v1/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"middleName\":null}"))
                .andExpect(status().isOk());

        verify(employeeService).update(eq(1L),
                argThat(req -> req.getMiddleName().isPresent() && req.getMiddleName().get() == null));
    }

    @Test
    void delete_whenFound_returns204() throws Exception {
        mockMvc.perform(delete("/v1/employees/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void search_withNameParam_returnsOk() throws Exception {
        when(employeeService.search(org.mockito.ArgumentMatchers.eq("Mar"), any(Pageable.class)))
                .thenReturn(samplePage());

        mockMvc.perform(get("/v1/employees/search").param("name", "Mar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].firstName").value("Maria"));
    }
}
