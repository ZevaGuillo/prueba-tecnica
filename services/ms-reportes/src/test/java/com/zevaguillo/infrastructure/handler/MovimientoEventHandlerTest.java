package com.zevaguillo.infrastructure.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zevaguillo.infrastructure.persistence.entity.ReporteCuentaEntity;
import com.zevaguillo.infrastructure.persistence.entity.ReporteMovimientoEntity;
import com.zevaguillo.infrastructure.persistence.repository.ProcessedEventRepository;
import com.zevaguillo.infrastructure.persistence.repository.ReporteCuentaRepository;
import com.zevaguillo.infrastructure.persistence.repository.ReporteMovimientoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovimientoEventHandlerTest {

    @Mock
    private ReporteMovimientoRepository movimientoRepository;
    @Mock
    private ReporteCuentaRepository cuentaRepository;
    @Mock
    private ProcessedEventRepository processedEventRepository;

    private ObjectMapper objectMapper;
    private MovimientoEventHandler handler;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().findAndRegisterModules();
        handler = new MovimientoEventHandler(movimientoRepository, cuentaRepository, processedEventRepository, objectMapper);
    }

    @Test
    void handleConPayload() {
        String json = """
                {"eventId":"em1","payload":{
                "movimientoId":"m1","cuentaId":"cu1","clienteId":"cli","tipo":"DEPOSITO",
                "monto":10,"saldoPosterior":110,"descripcion":"d","fecha":"2026-01-15T10:00:00"
                }}
                """;
        when(processedEventRepository.existsById("em1")).thenReturn(false);

        handler.handle(json);

        verify(movimientoRepository).save(any(ReporteMovimientoEntity.class));
        verify(processedEventRepository).save(any());
    }

    @Test
    void handleRootResuelveClienteDesdeCuenta() {
        ReporteCuentaEntity cuenta = new ReporteCuentaEntity();
        cuenta.setId(UUID.randomUUID());
        cuenta.setCuentaId("cu9");
        cuenta.setClienteId("desde-cuenta");
        when(cuentaRepository.findByCuentaId("cu9")).thenReturn(Optional.of(cuenta));
        when(processedEventRepository.existsById("tx-1")).thenReturn(false);

        String json = """
                {"movimientoId":"m9","cuentaId":"cu9","tipoMovimiento":"RETIRO","valor":5,"saldoResultante":95,"transactionId":"tx-1"}
                """;

        handler.handle(json);

        ArgumentCaptor<ReporteMovimientoEntity> cap = ArgumentCaptor.forClass(ReporteMovimientoEntity.class);
        verify(movimientoRepository).save(cap.capture());
        assertThat(cap.getValue().getClienteId()).isEqualTo("desde-cuenta");
    }

    @Test
    void handleRootUsaMontoYsaldoPosteriorAliases() {
        String json = """
                {"movimientoId":"m2","cuentaId":"cu","clienteId":"c","tipo":"DEPOSITO","monto":1,"saldoPosterior":2}
                """;
        when(processedEventRepository.existsById("mov-m2")).thenReturn(false);

        handler.handle(json);

        ArgumentCaptor<ReporteMovimientoEntity> cap = ArgumentCaptor.forClass(ReporteMovimientoEntity.class);
        verify(movimientoRepository).save(cap.capture());
        assertThat(cap.getValue().getMonto()).isEqualByComparingTo(BigDecimal.ONE);
    }

    @Test
    void handleDuplicado() {
        String json = """
                {"eventId":"dup","payload":{"movimientoId":"m","cuentaId":"c","clienteId":"x","tipo":"D","monto":1,"saldoPosterior":1}}
                """;
        when(processedEventRepository.existsById("dup")).thenReturn(true);

        handler.handle(json);

        verify(movimientoRepository, never()).save(any());
    }

    @Test
    void handleError() {
        assertThatThrownBy(() -> handler.handle("@@@"))
                .isInstanceOf(RuntimeException.class);
    }
}
