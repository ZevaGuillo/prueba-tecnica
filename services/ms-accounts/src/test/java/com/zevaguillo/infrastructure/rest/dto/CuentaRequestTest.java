package com.zevaguillo.infrastructure.rest.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CuentaRequestTest {

    @Test
    void gettersYSetters() {
        CuentaRequest r = new CuentaRequest();
        r.setCuentaId("c1");
        r.setNumeroCuenta("0001");
        r.setTipoCuenta("AHORRO");
        r.setSaldo(100.0);
        r.setClienteId("cli");
        r.setEstado("ACTIVE");

        assertThat(r.getCuentaId()).isEqualTo("c1");
        assertThat(r.getNumeroCuenta()).isEqualTo("0001");
        assertThat(r.getTipoCuenta()).isEqualTo("AHORRO");
        assertThat(r.getSaldo()).isEqualTo(100.0);
        assertThat(r.getClienteId()).isEqualTo("cli");
        assertThat(r.getEstado()).isEqualTo("ACTIVE");
    }
}
