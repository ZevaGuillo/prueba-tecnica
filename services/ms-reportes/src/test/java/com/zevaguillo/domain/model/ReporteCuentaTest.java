package com.zevaguillo.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ReporteCuentaTest {

    @Test
    void constructorInicializaEstadoYFecha() {
        ReporteCuenta r = new ReporteCuenta("cu", "cli", "001", "A", BigDecimal.TEN, "USD");
        assertThat(r.getCuentaId()).isEqualTo("cu");
        assertThat(r.getClienteId()).isEqualTo("cli");
        assertThat(r.getNumeroCuenta()).isEqualTo("001");
        assertThat(r.getTipo()).isEqualTo("A");
        assertThat(r.getSaldoActual()).isEqualByComparingTo(BigDecimal.TEN);
        assertThat(r.getMoneda()).isEqualTo("USD");
        assertThat(r.getEstado()).isEqualTo("ACTIVE");
        assertThat(r.getFechaCreacion()).isNotNull();
    }

    @Test
    void gettersYSetters() {
        ReporteCuenta r = new ReporteCuenta();
        UUID id = UUID.randomUUID();
        LocalDateTime t = LocalDateTime.now();
        r.setId(id);
        r.setCuentaId("c");
        r.setClienteId("cl");
        r.setNumeroCuenta("n");
        r.setTipo("T");
        r.setSaldoActual(BigDecimal.ONE);
        r.setMoneda("PEN");
        r.setEstado("X");
        r.setFechaCreacion(t);
        assertThat(r.getId()).isEqualTo(id);
    }
}
