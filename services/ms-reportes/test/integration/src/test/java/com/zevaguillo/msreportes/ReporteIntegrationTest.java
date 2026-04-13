package com.zevaguillo.msreportes;

import com.zevaguillo.msreportes.infrastructure.MsReportesApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = MsReportesApplication.class)
public class ReporteIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldReturnNotFoundForNonExistentCliente() {
        String url = String.format("/reportes?clienteId=%s&fechaInicio=%s&fechaFin=%s",
            "550e8400-e29b-41d4-a716-446655440000",
            LocalDate.of(2026, 1, 1),
            LocalDate.of(2026, 12, 31));

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturnBadRequestForInvalidDateRange() {
        String url = String.format("/reportes?clienteId=%s&fechaInicio=%s&fechaFin=%s",
            "550e8400-e29b-41d4-a716-446655440000",
            LocalDate.of(2026, 12, 31),
            LocalDate.of(2026, 1, 1));

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldReturnBadRequestForInvalidPagination() {
        String url = String.format("/reportes?clienteId=%s&fechaInicio=%s&fechaFin=%s&size=%d",
            "550e8400-e29b-41d4-a716-446655440000",
            LocalDate.of(2026, 1, 1),
            LocalDate.of(2026, 12, 31),
            0);

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}