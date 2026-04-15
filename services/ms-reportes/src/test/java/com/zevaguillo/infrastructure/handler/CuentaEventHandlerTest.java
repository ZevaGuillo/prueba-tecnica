package com.zevaguillo.infrastructure.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zevaguillo.infrastructure.persistence.entity.ReporteCuentaEntity;
import com.zevaguillo.infrastructure.persistence.repository.ProcessedEventRepository;
import com.zevaguillo.infrastructure.persistence.repository.ReporteCuentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CuentaEventHandlerTest {

    @Mock
    private ReporteCuentaRepository cuentaRepository;
    @Mock
    private ProcessedEventRepository processedEventRepository;

    private ObjectMapper objectMapper;
    private CuentaEventHandler handler;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().findAndRegisterModules();
        handler = new CuentaEventHandler(cuentaRepository, processedEventRepository, objectMapper);
    }

    @Test
    void handleConPayload() {
        String json = """
                {"eventId":"e-cu","payload":{"cuentaId":"cu1","clienteId":"cli","numeroCuenta":"001","tipo":"AHORRO","saldoInicial":100,"moneda":"USD"}}
                """;
        when(processedEventRepository.existsById("e-cu")).thenReturn(false);

        handler.handle(json);

        verify(cuentaRepository).save(any(ReporteCuentaEntity.class));
        verify(processedEventRepository).save(any());
    }

    @Test
    void handleRootUsaSaldoYtipoCuenta() {
        String json = """
                {"cuentaId":"cu2","clienteId":"cli","numeroCuenta":"002","tipoCuenta":"COR","saldo":50,"moneda":"PEN"}
                """;
        when(processedEventRepository.existsById("cuenta-cu2")).thenReturn(false);

        handler.handle(json);

        ArgumentCaptor<ReporteCuentaEntity> cap = ArgumentCaptor.forClass(ReporteCuentaEntity.class);
        verify(cuentaRepository).save(cap.capture());
        assertThat(cap.getValue().getCuentaId()).isEqualTo("cu2");
        assertThat(cap.getValue().getSaldoActual()).isEqualByComparingTo(BigDecimal.valueOf(50));
    }

    @Test
    void handleDuplicadoNoGuarda() {
        String json = """
                {"eventId":"dup","payload":{"cuentaId":"x","clienteId":"c","numeroCuenta":"n","tipo":"A","moneda":"USD"}}
                """;
        when(processedEventRepository.existsById("dup")).thenReturn(true);

        handler.handle(json);

        verify(cuentaRepository, never()).save(any());
    }

    @Test
    void handleRootUsaSaldoInicialCuandoNoHaySaldo() {
        String json = """
                {"cuentaId":"cu3","clienteId":"c","numeroCuenta":"003","tipo":"A","saldoInicial":25,"moneda":"EUR"}
                """;
        when(processedEventRepository.existsById("cuenta-cu3")).thenReturn(false);

        handler.handle(json);

        ArgumentCaptor<ReporteCuentaEntity> cap = ArgumentCaptor.forClass(ReporteCuentaEntity.class);
        verify(cuentaRepository).save(cap.capture());
        assertThat(cap.getValue().getSaldoActual()).isEqualByComparingTo(BigDecimal.valueOf(25));
    }

    @Test
    void handleInvalido() {
        assertThatThrownBy(() -> handler.handle("{"))
                .isInstanceOf(RuntimeException.class);
    }
}
