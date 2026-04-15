package com.zevaguillo.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class OutboxEventTest {

    @Test
    void constructoresYAccesores() {
        OutboxEvent e = new OutboxEvent();
        e.setId("id-1");
        e.setStatus("PENDING");
        assertThat(e.getId()).isEqualTo("id-1");
        assertThat(e.getStatus()).isEqualTo("PENDING");

        LocalDateTime t = LocalDateTime.of(2026, 1, 1, 0, 0);
        OutboxEvent full = new OutboxEvent("i", "agg", "TYPE", "EVT", "topic", "{}", "NEW", t);
        assertThat(full.getAggregateId()).isEqualTo("agg");
        assertThat(full.getCreatedAt()).isEqualTo(t);
        full.setPublishedAt(t.plusHours(1));
        assertThat(full.getPublishedAt()).isEqualTo(t.plusHours(1));
    }
}
