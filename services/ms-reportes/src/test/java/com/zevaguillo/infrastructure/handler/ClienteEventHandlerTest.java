package com.zevaguillo.infrastructure.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zevaguillo.infrastructure.persistence.entity.ReporteClienteEntity;
import com.zevaguillo.infrastructure.persistence.repository.ProcessedEventRepository;
import com.zevaguillo.infrastructure.persistence.repository.ReporteClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteEventHandlerTest {

    @Mock
    private ReporteClienteRepository clienteRepository;
    @Mock
    private ProcessedEventRepository processedEventRepository;

    private ObjectMapper objectMapper;
    private ClienteEventHandler handler;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().findAndRegisterModules();
        handler = new ClienteEventHandler(clienteRepository, processedEventRepository, objectMapper);
    }

    @Test
    void handleConPayloadGuardaClienteYProcessed() {
        String json = """
                {"eventId":"evt-1","payload":{"clienteId":"c1","nombre":"N","identificacion":"i","email":"e","telefono":"t"}}
                """;
        when(processedEventRepository.existsById("evt-1")).thenReturn(false);

        handler.handle(json);

        verify(clienteRepository).save(any(ReporteClienteEntity.class));
        verify(processedEventRepository).save(any());
    }

    @Test
    void handleIgnoraDuplicado() {
        String json = """
                {"eventId":"dup","payload":{"clienteId":"c1","nombre":"N","identificacion":"i","email":null,"telefono":null}}
                """;
        when(processedEventRepository.existsById("dup")).thenReturn(true);

        handler.handle(json);

        verify(clienteRepository, never()).save(any());
    }

    @Test
    void handleJsonRootSinPayloadCompat() {
        String json = """
                {"clienteId":"c2","nombre":"Root","identificacion":"id2","email":"m@x.com","telefono":"1"}
                """;
        when(processedEventRepository.existsById("cliente-c2")).thenReturn(false);

        handler.handle(json);

        ArgumentCaptor<ReporteClienteEntity> cap = ArgumentCaptor.forClass(ReporteClienteEntity.class);
        verify(clienteRepository).save(cap.capture());
        assertThat(cap.getValue().getClienteId()).isEqualTo("c2");
        verify(processedEventRepository).save(any());
    }

    @Test
    void handleJsonInvalidoPropaga() {
        assertThatThrownBy(() -> handler.handle("not-json"))
                .isInstanceOf(RuntimeException.class);
    }
}
