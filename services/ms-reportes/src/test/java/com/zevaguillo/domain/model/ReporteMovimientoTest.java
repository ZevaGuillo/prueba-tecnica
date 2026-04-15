package com.zevaguillo.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ReporteMovimientoTest {

    @Test
    void constructorAsignaFechaProcesamiento() {
        LocalDateTime f = LocalDateTime.of(2026, 3, 1, 8, 0);
        ReporteMovimiento m = new ReporteMovimiento(
                "m1", "cu", "cli", "D", BigDecimal.ONE, BigDecimal.TEN, "x", f);
        assertThat(m.getMovimientoId()).isEqualTo("m1");
        assertThat(m.getCuentaId()).isEqualTo("cu");
        assertThat(m.getClienteId()).isEqualTo("cli");
        assertThat(m.getTipo()).isEqualTo("D");
        assertThat(m.getMonto()).isEqualByComparingTo(BigDecimal.ONE);
        assertThat(m.getSaldoPosterior()).isEqualByComparingTo(BigDecimal.TEN);
        assertThat(m.getDescripcion()).isEqualTo("x");
        assertThat(m.getFecha()).isEqualTo(f);
        assertThat(m.getFechaProcesamiento()).isNotNull();
    }

    @Test
    void gettersYSetters() {
        ReporteMovimiento m = new ReporteMovimiento();
        UUID id = UUID.randomUUID();
        LocalDateTime t = LocalDateTime.now();
        m.setId(id);
        m.setMovimientoId("m");
        m.setCuentaId("c");
        m.setClienteId("cl");
        m.setTipo("T");
        m.setMonto(BigDecimal.valueOf(2));
        m.setSaldoPosterior(BigDecimal.valueOf(3));
        m.setDescripcion("d");
        m.setFecha(t);
        m.setFechaProcesamiento(t.plusHours(1));
        assertThat(m.getId()).isEqualTo(id);
    }
}
