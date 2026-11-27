package com.babel.employeeservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Boots the real embedded server (H2 datasource, via src/test/resources/application.yml) to
 * verify the actual SecurityFilterChain wiring end-to-end, not just that it compiles.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SecurityIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void employeesEndpoint_withoutCredentials_returns401() {
        ResponseEntity<String> response = restTemplate.getForEntity("/v1/employees", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void employeesEndpoint_withWrongCredentials_returns401() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("test", "wrong-password")
                .getForEntity("/v1/employees", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void employeesEndpoint_withCorrectCredentials_returns200() {
        ResponseEntity<String> response = restTemplate
                .withBasicAuth("test", "test")
                .getForEntity("/v1/employees", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void healthEndpoint_withoutCredentials_isOpen() {
        ResponseEntity<String> response = restTemplate.getForEntity("/actuator/health", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void swaggerDocs_withoutCredentials_isOpen() {
        ResponseEntity<String> response = restTemplate.getForEntity("/v3/api-docs", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
