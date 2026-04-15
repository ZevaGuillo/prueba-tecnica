package com.zevaguillo.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ReporteClienteTest {

    @Test
    void constructorConArgumentos() {
        ReporteCliente r = new ReporteCliente("c1", "N", "id", "e@e.com", "1");
        assertThat(r.getClienteId()).isEqualTo("c1");
        assertThat(r.getNombre()).isEqualTo("N");
        assertThat(r.getIdentificacion()).isEqualTo("id");
        assertThat(r.getEmail()).isEqualTo("e@e.com");
        assertThat(r.getTelefono()).isEqualTo("1");
        assertThat(r.getId()).isNotNull();
        assertThat(r.getFechaCreacion()).isNotNull();
    }

    @Test
    void gettersYSetters() {
        ReporteCliente r = new ReporteCliente();
        UUID id = UUID.randomUUID();
        LocalDateTime t = LocalDateTime.of(2026, 6, 1, 12, 0);
        r.setId(id);
        r.setClienteId("x");
        r.setNombre("n");
        r.setIdentificacion("i");
        r.setEmail("e");
        r.setTelefono("t");
        r.setFechaCreacion(t);
        r.setFechaActualizacion(t.plusDays(1));
        assertThat(r.getId()).isEqualTo(id);
        assertThat(r.getFechaActualizacion()).isEqualTo(t.plusDays(1));
    }
}
