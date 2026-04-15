package com.zevaguillo.application.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zevaguillo.application.exception.TransaccionDuplicadaException;
import com.zevaguillo.application.port.out.CuentaPersistencePort;
import com.zevaguillo.application.port.out.MovimientoPersistencePort;
import com.zevaguillo.application.port.out.OutboxEventPersistencePort;
import com.zevaguillo.domain.model.Cuenta;
import com.zevaguillo.domain.model.Movimiento;
import com.zevaguillo.domain.model.OutboxEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrarMovimientoUseCaseImplTest {

    @Mock
    private CuentaPersistencePort cuentaPersistencePort;
    @Mock
    private MovimientoPersistencePort movimientoPersistencePort;
    @Mock
    private OutboxEventPersistencePort outboxPort;
    @Mock
    private ObjectMapper objectMapper;

    private RegistrarMovimientoUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new RegistrarMovimientoUseCaseImpl(
                cuentaPersistencePort, movimientoPersistencePort, outboxPort, objectMapper);
    }

    @Test
    void depositoAumentaSaldo() throws Exception {
        Movimiento m = Movimiento.crear("m1", "cu1", "DEPOSITO", 10.0, null);
        Cuenta cuenta = Cuenta.crear("cu1", "001", "A", 100.0, "cli");
        when(movimientoPersistencePort.existsByTransactionId("tx")).thenReturn(false);
        when(cuentaPersistencePort.findById("cu1")).thenReturn(Optional.of(cuenta));
        when(cuentaPersistencePort.save(any(Cuenta.class))).thenAnswer(inv -> inv.getArgument(0));
        when(movimientoPersistencePort.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        Movimiento r = useCase.ejecutar(m, "tx");

        assertThat(r.getSaldoResultante()).isEqualTo(110.0);
        assertThat(r.getTransactionId()).isEqualTo("tx");
        verify(outboxPort).save(any(OutboxEvent.class));
    }

    @Test
    void retiroDisminuyeSaldo() throws Exception {
        Movimiento m = Movimiento.crear("m2", "cu1", "RETIRO", 30.0, null);
        Cuenta cuenta = Cuenta.crear("cu1", "001", "A", 100.0, "cli");
        when(cuentaPersistencePort.findById("cu1")).thenReturn(Optional.of(cuenta));
        when(cuentaPersistencePort.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(movimientoPersistencePort.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        Movimiento r = useCase.ejecutar(m, null);

        assertThat(r.getSaldoResultante()).isEqualTo(70.0);
    }

    @Test
    void transaccionDuplicada() {
        when(movimientoPersistencePort.existsByTransactionId("dup")).thenReturn(true);
        Movimiento m = Movimiento.crear("m", "cu", "DEPOSITO", 1.0, null);

        assertThatThrownBy(() -> useCase.ejecutar(m, "dup"))
                .isInstanceOf(TransaccionDuplicadaException.class);
    }

    @Test
    void rechazaSinCuentaId() {
        Movimiento m = new Movimiento();
        m.setTipoMovimiento("DEPOSITO");
        m.setValor(1.0);

        assertThatThrownBy(() -> useCase.ejecutar(m, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cuenta ID");
    }

    @Test
    void rechazaSinTipo() {
        Movimiento m = new Movimiento();
        m.setCuentaId("cu");
        m.setValor(1.0);

        assertThatThrownBy(() -> useCase.ejecutar(m, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tipo");
    }

    @Test
    void rechazaValorInvalido() {
        Movimiento m = Movimiento.crear("m", "cu", "DEPOSITO", 0.0, null);

        assertThatThrownBy(() -> useCase.ejecutar(m, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Valor");
    }

    @Test
    void cuentaNoEncontrada() {
        Movimiento m = Movimiento.crear("m", "cu", "DEPOSITO", 10.0, null);
        when(cuentaPersistencePort.findById("cu")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.ejecutar(m, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no encontrada");
    }

    @Test
    void cuentaInactiva() {
        Movimiento m = Movimiento.crear("m", "cu", "DEPOSITO", 10.0, null);
        Cuenta c = Cuenta.crear("cu", "n", "A", 10.0, "cli");
        c.setEstado("INACTIVE");
        when(cuentaPersistencePort.findById("cu")).thenReturn(Optional.of(c));

        assertThatThrownBy(() -> useCase.ejecutar(m, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("activa");
    }

    @Test
    void errorSerializacionNoGuardaOutbox() throws Exception {
        Movimiento m = Movimiento.crear("m", "cu", "DEPOSITO", 5.0, null);
        Cuenta cuenta = Cuenta.crear("cu", "n", "A", 100.0, "cli");
        when(cuentaPersistencePort.findById("cu")).thenReturn(Optional.of(cuenta));
        when(cuentaPersistencePort.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(movimientoPersistencePort.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("x") { });

        assertThatThrownBy(() -> useCase.ejecutar(m, null))
                .isInstanceOf(RuntimeException.class);
        verify(outboxPort, never()).save(any());
    }

    @Test
    void eventoOutboxCorrecto() throws Exception {
        Movimiento m = Movimiento.crear("mid", "cu", "DEPOSITO", 1.0, null);
        Cuenta cuenta = Cuenta.crear("cu", "n", "A", 10.0, "cli");
        when(cuentaPersistencePort.findById("cu")).thenReturn(Optional.of(cuenta));
        when(cuentaPersistencePort.save(any())).thenAnswer(inv -> inv.getArgument(0));
        Movimiento guardado = Movimiento.crear("mid", "cu", "DEPOSITO", 1.0, 11.0);
        when(movimientoPersistencePort.save(any())).thenReturn(guardado);
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        useCase.ejecutar(m, null);

        ArgumentCaptor<OutboxEvent> cap = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxPort).save(cap.capture());
        assertThat(cap.getValue().getEventType()).isEqualTo("MovimientoRegistrado");
        assertThat(cap.getValue().getAggregateId()).isEqualTo("mid");
    }
}
