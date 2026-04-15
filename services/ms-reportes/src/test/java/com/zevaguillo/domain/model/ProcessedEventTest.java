package com.zevaguillo.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProcessedEventTest {

    @Test
    void constructorYAccesores() {
        ProcessedEvent e = new ProcessedEvent("e1", "TYPE_A");
        assertThat(e.getEventId()).isEqualTo("e1");
        assertThat(e.getEventType()).isEqualTo("TYPE_A");
        assertThat(e.getFechaProcesamiento()).isNotNull();
    }
}
