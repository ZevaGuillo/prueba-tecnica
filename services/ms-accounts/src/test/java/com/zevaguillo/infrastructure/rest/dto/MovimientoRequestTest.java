package com.zevaguillo.infrastructure.rest.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MovimientoRequestTest {

    @Test
    void gettersYSetters() {
        MovimientoRequest r = new MovimientoRequest();
        r.setMovimientoId("m1");
        r.setCuentaId("c1");
        r.setTipoMovimiento("DEPOSITO");
        r.setValor(25.0);
        r.setTransactionId("tx");

        assertThat(r.getMovimientoId()).isEqualTo("m1");
        assertThat(r.getCuentaId()).isEqualTo("c1");
        assertThat(r.getTipoMovimiento()).isEqualTo("DEPOSITO");
        assertThat(r.getValor()).isEqualTo(25.0);
        assertThat(r.getTransactionId()).isEqualTo("tx");
    }
}
