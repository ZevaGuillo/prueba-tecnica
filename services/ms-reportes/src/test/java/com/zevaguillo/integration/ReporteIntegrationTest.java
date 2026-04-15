package com.zevaguillo.integration;

import com.zevaguillo.infrastructure.MsReportesApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = MsReportesApplication.class)
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = {
        "cliente-events",
        "cuenta-events",
        "cuenta-creada",
        "cuenta-actualizada",
        "movimiento-events",
        "movimiento-registrado"
})
class ReporteIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void shouldReturnNotFoundForNonExistentCliente() throws Exception {
        mockMvc.perform(get("/api/reportes")
                        .param("clienteId", "550e8400-e29b-41d4-a716-446655440000")
                        .param("fechaInicio", "2026-01-01")
                        .param("fechaFin", "2026-12-31"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestForInvalidDateRange() throws Exception {
        mockMvc.perform(get("/api/reportes")
                        .param("clienteId", "550e8400-e29b-41d4-a716-446655440000")
                        .param("fechaInicio", "2026-12-31")
                        .param("fechaFin", "2026-01-01"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestForInvalidPagination() throws Exception {
        mockMvc.perform(get("/api/reportes")
                        .param("clienteId", "550e8400-e29b-41d4-a716-446655440000")
                        .param("fechaInicio", "2026-01-01")
                        .param("fechaFin", "2026-12-31")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());
    }
}
