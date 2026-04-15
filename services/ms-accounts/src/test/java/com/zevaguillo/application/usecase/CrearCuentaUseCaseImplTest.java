package com.zevaguillo.application.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zevaguillo.application.exception.ClienteNoEncontradoException;
import com.zevaguillo.application.exception.CuentaAlreadyExistsException;
import com.zevaguillo.application.port.out.ClienteCachePersistencePort;
import com.zevaguillo.application.port.out.CuentaPersistencePort;
import com.zevaguillo.application.port.out.OutboxEventPersistencePort;
import com.zevaguillo.domain.model.Cuenta;
import com.zevaguillo.domain.model.OutboxEvent;
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
class CrearCuentaUseCaseImplTest {

    @Mock
    private CuentaPersistencePort persistencePort;
    @Mock
    private OutboxEventPersistencePort outboxPort;
    @Mock
    private ClienteCachePersistencePort clienteCachePort;
    @Mock
    private ObjectMapper objectMapper;

    private CrearCuentaUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new CrearCuentaUseCaseImpl(persistencePort, outboxPort, clienteCachePort, objectMapper);
    }

    @Test
    void ejecutarCreaCuentaYEvento() throws Exception {
        Cuenta c = Cuenta.crear("cu1", "001", "AHORRO", 0.0, "cli");
        when(clienteCachePort.existsById("cli")).thenReturn(true);
        when(persistencePort.existsByNumeroCuenta("001")).thenReturn(false);
        when(persistencePort.existsById("cu1")).thenReturn(false);
        when(persistencePort.save(any(Cuenta.class))).thenAnswer(inv -> inv.getArgument(0));
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        Cuenta result = useCase.ejecutar(c);

        assertThat(result.getEstado()).isEqualTo("ACTIVE");
        verify(outboxPort).save(any(OutboxEvent.class));
    }

    @Test
    void ejecutarAsignaSaldoYCeroPorDefecto() throws Exception {
        Cuenta c = new Cuenta();
        c.setCuentaId("cu2");
        c.setNumeroCuenta("002");
        c.setClienteId("cli");
        c.setTipoCuenta("COR");
        c.setSaldo(null);
        c.setEstado(null);
        when(clienteCachePort.existsById("cli")).thenReturn(true);
        when(persistencePort.existsByNumeroCuenta("002")).thenReturn(false);
        when(persistencePort.existsById("cu2")).thenReturn(false);
        when(persistencePort.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        Cuenta result = useCase.ejecutar(c);

        assertThat(result.getSaldo()).isEqualTo(0.0);
        assertThat(result.getEstado()).isEqualTo("ACTIVE");
    }

    @Test
    void ejecutarRechazaSinNumero() {
        Cuenta c = new Cuenta();
        c.setClienteId("cli");
        assertThatThrownBy(() -> useCase.ejecutar(c))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Número");
    }

    @Test
    void ejecutarRechazaSinCliente() {
        Cuenta c = Cuenta.crear("cu", "n", "t", 0.0, "");
        c.setClienteId(" ");
        assertThatThrownBy(() -> useCase.ejecutar(c))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cliente ID");
    }

    @Test
    void ejecutarLanzaClienteNoEncontrado() {
        Cuenta c = Cuenta.crear("cu", "n", "t", 0.0, "cli");
        when(clienteCachePort.existsById("cli")).thenReturn(false);

        assertThatThrownBy(() -> useCase.ejecutar(c))
                .isInstanceOf(ClienteNoEncontradoException.class);
    }

    @Test
    void ejecutarLanzaSiNumeroExiste() {
        Cuenta c = Cuenta.crear("cu", "n", "t", 0.0, "cli");
        when(clienteCachePort.existsById("cli")).thenReturn(true);
        when(persistencePort.existsByNumeroCuenta("n")).thenReturn(true);

        assertThatThrownBy(() -> useCase.ejecutar(c))
                .isInstanceOf(CuentaAlreadyExistsException.class);
    }

    @Test
    void ejecutarLanzaSiIdExiste() {
        Cuenta c = Cuenta.crear("cu", "n", "t", 0.0, "cli");
        when(clienteCachePort.existsById("cli")).thenReturn(true);
        when(persistencePort.existsByNumeroCuenta("n")).thenReturn(false);
        when(persistencePort.existsById("cu")).thenReturn(true);

        assertThatThrownBy(() -> useCase.ejecutar(c))
                .isInstanceOf(CuentaAlreadyExistsException.class);
    }

    @Test
    void ejecutarPropagaErrorSerializacion() throws Exception {
        Cuenta c = Cuenta.crear("cu3", "n3", "t", 0.0, "cli");
        when(clienteCachePort.existsById("cli")).thenReturn(true);
        when(persistencePort.existsByNumeroCuenta("n3")).thenReturn(false);
        when(persistencePort.existsById("cu3")).thenReturn(false);
        when(persistencePort.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("x") { });

        assertThatThrownBy(() -> useCase.ejecutar(c))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("serializando");
        verify(outboxPort, never()).save(any());
    }

    @Test
    void ejecutarEventoUsaCuentaIdComoAggregate() throws Exception {
        Cuenta c = Cuenta.crear("agg", "n", "t", 1.0, "cli");
        when(clienteCachePort.existsById("cli")).thenReturn(true);
        when(persistencePort.existsByNumeroCuenta("n")).thenReturn(false);
        when(persistencePort.existsById("agg")).thenReturn(false);
        Cuenta saved = Cuenta.crear("agg", "n", "t", 1.0, "cli");
        when(persistencePort.save(any())).thenReturn(saved);
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        useCase.ejecutar(c);

        ArgumentCaptor<OutboxEvent> cap = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxPort).save(cap.capture());
        assertThat(cap.getValue().getAggregateId()).isEqualTo("agg");
        assertThat(cap.getValue().getEventType()).isEqualTo("CuentaCreada");
    }
}
