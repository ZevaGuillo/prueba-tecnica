package com.zevaguillo.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Movimiento")
class MovimientoTest {

    @Test
    void constructoresYFactory() {
        Movimiento m1 = new Movimiento();
        assertThat(m1.getFecha()).isNotNull();

        Movimiento m2 = new Movimiento("m1", "c1", "DEPOSITO", 10.0, 100.0);
        assertThat(m2.getMovimientoId()).isEqualTo("m1");
        assertThat(m2.getCuentaId()).isEqualTo("c1");
        assertThat(m2.getTipoMovimiento()).isEqualTo("DEPOSITO");

        Movimiento m3 = Movimiento.crear("m2", "c1", "RETIRO", 5.0, 95.0);
        assertThat(m3.getTipoMovimiento()).isEqualTo("RETIRO");
        assertThat(m3.getFecha()).isNotNull();
    }

    @Test
    void tipoDepositoORetiro() {
        Movimiento d = new Movimiento();
        d.setTipoMovimiento("DEPOSITO");
        assertThat(d.isDeposito()).isTrue();
        assertThat(d.isRetiro()).isFalse();

        Movimiento r = new Movimiento();
        r.setTipoMovimiento("RETIRO");
        assertThat(r.isRetiro()).isTrue();
        assertThat(r.isDeposito()).isFalse();
    }

    @Test
    void gettersYSetters() {
        Movimiento m = new Movimiento();
        m.setTransactionId("tx-1");
        m.setFecha(LocalDateTime.of(2026, 1, 1, 12, 0));
        assertThat(m.getTransactionId()).isEqualTo("tx-1");
        assertThat(m.getFecha().getYear()).isEqualTo(2026);
    }
}
