package com.zevaguillo.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ClienteCacheTest {

    @Test
    void constructoresYAccesores() {
        ClienteCache c = new ClienteCache();
        assertThat(c.getClienteId()).isNull();

        LocalDateTime now = LocalDateTime.of(2026, 6, 1, 10, 0);
        ClienteCache full = new ClienteCache("cli", "Nombre", "ACTIVE", now);
        assertThat(full.getClienteId()).isEqualTo("cli");
        assertThat(full.getNombre()).isEqualTo("Nombre");
        assertThat(full.getEstado()).isEqualTo("ACTIVE");
        assertThat(full.getSyncedAt()).isEqualTo(now);

        full.setEstado("INACTIVE");
        assertThat(full.getEstado()).isEqualTo("INACTIVE");
    }
}
