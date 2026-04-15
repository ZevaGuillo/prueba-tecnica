package com.zevaguillo.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Cuenta")
class CuentaTest {

    @Nested
    class Constructores {

        @Test
        void constructorPorDefecto() {
            assertThat(new Cuenta().getCuentaId()).isNull();
        }

        @Test
        void constructorConArgumentos() {
            Cuenta c = new Cuenta("c1", "0001", "AHORRO", 100.0, "ACTIVE", "cli");
            assertThat(c.getCuentaId()).isEqualTo("c1");
            assertThat(c.getNumeroCuenta()).isEqualTo("0001");
            assertThat(c.getTipoCuenta()).isEqualTo("AHORRO");
            assertThat(c.getSaldo()).isEqualTo(100.0);
            assertThat(c.getEstado()).isEqualTo("ACTIVE");
            assertThat(c.getClienteId()).isEqualTo("cli");
        }

        @Test
        void crearFactory() {
            Cuenta c = Cuenta.crear("c1", "0001", "CORRIENTE", 50.0, "cli-1");
            assertThat(c.getEstado()).isEqualTo("ACTIVE");
            assertThat(c.getSaldo()).isEqualTo(50.0);
            assertThat(c.getVersion()).isEqualTo(0);
            assertThat(c.isActiva()).isTrue();
        }
    }

    @Nested
    class ReglasDeDominio {

        @Test
        void isActiva() {
            Cuenta c = new Cuenta();
            c.setEstado("ACTIVE");
            assertThat(c.isActiva()).isTrue();
            c.setEstado("INACTIVE");
            assertThat(c.isActiva()).isFalse();
        }

        @Test
        void activarYDesactivar() {
            Cuenta c = new Cuenta();
            c.setEstado("INACTIVE");
            c.activar();
            assertThat(c.getEstado()).isEqualTo("ACTIVE");
            c.desactivar();
            assertThat(c.getEstado()).isEqualTo("INACTIVE");
        }

        @Test
        void tieneSaldo() {
            Cuenta c = new Cuenta();
            assertThat(c.tieneSaldo()).isFalse();
            c.setSaldo(0.0);
            assertThat(c.tieneSaldo()).isFalse();
            c.setSaldo(0.01);
            assertThat(c.tieneSaldo()).isTrue();
        }
    }
}
