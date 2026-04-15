package com.zevaguillo.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class OutboxEventTest {

    @Test
    void constructorYGettersSetters() {
        LocalDateTime now = LocalDateTime.of(2026, 1, 1, 12, 0);
        OutboxEvent e = new OutboxEvent("id1", "agg", "T", "EV", "topic", "{}", "PENDING", now);
        assertThat(e.getId()).isEqualTo("id1");
        assertThat(e.getAggregateId()).isEqualTo("agg");
        assertThat(e.getAggregateType()).isEqualTo("T");
        assertThat(e.getEventType()).isEqualTo("EV");
        assertThat(e.getTopic()).isEqualTo("topic");
        assertThat(e.getPayload()).isEqualTo("{}");
        assertThat(e.getStatus()).isEqualTo("PENDING");
        assertThat(e.getCreatedAt()).isEqualTo(now);

        e.setPublishedAt(now.plusHours(1));
        assertThat(e.getPublishedAt()).isEqualTo(now.plusHours(1));
    }

    @Test
    void defaultConstructor() {
        OutboxEvent e = new OutboxEvent();
        e.setId("x");
        assertThat(e.getId()).isEqualTo("x");
    }
}
