package com.zevaguillo.application.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zevaguillo.application.port.out.CuentaPersistencePort;
import com.zevaguillo.application.port.out.OutboxEventPersistencePort;
import com.zevaguillo.domain.model.Cuenta;
import com.zevaguillo.domain.model.OutboxEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class ActualizarCuentaUseCaseImplTest {

    @Mock
    private CuentaPersistencePort persistencePort;
    @Mock
    private OutboxEventPersistencePort outboxPort;
    @Mock
    private ObjectMapper objectMapper;

    private ActualizarCuentaUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new ActualizarCuentaUseCaseImpl(persistencePort, outboxPort, objectMapper);
    }

    @Test
    void ejecutarActualizaEstado() throws Exception {
        Cuenta input = new Cuenta();
        input.setCuentaId("cu");
        input.setEstado("INACTIVE");
        Cuenta existente = Cuenta.crear("cu", "n", "A", 0.0, "cli");
        when(persistencePort.existsById("cu")).thenReturn(true);
        when(persistencePort.findById("cu")).thenReturn(Optional.of(existente));
        when(persistencePort.save(any(Cuenta.class))).thenAnswer(inv -> inv.getArgument(0));
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        Cuenta r = useCase.ejecutar(input);

        assertThat(r.getEstado()).isEqualTo("INACTIVE");
        verify(outboxPort).save(any(OutboxEvent.class));
    }

    @Test
    void ejecutarSinCambioEstadoMantiene() throws Exception {
        Cuenta input = new Cuenta();
        input.setCuentaId("cu");
        input.setEstado(null);
        Cuenta existente = Cuenta.crear("cu", "n", "A", 5.0, "cli");
        when(persistencePort.existsById("cu")).thenReturn(true);
        when(persistencePort.findById("cu")).thenReturn(Optional.of(existente));
        when(persistencePort.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        Cuenta r = useCase.ejecutar(input);

        assertThat(r.getEstado()).isEqualTo("ACTIVE");
    }

    @Test
    void ejecutarRechazaSinId() {
        assertThatThrownBy(() -> useCase.ejecutar(new Cuenta()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cuenta ID");
    }

    @Test
    void ejecutarRechazaNoExiste() {
        Cuenta input = new Cuenta();
        input.setCuentaId("cu");
        when(persistencePort.existsById("cu")).thenReturn(false);

        assertThatThrownBy(() -> useCase.ejecutar(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no encontrada");
    }

    @Test
    void errorSerializacion() throws Exception {
        Cuenta input = new Cuenta();
        input.setCuentaId("cu");
        Cuenta existente = Cuenta.crear("cu", "n", "A", 0.0, "cli");
        when(persistencePort.existsById("cu")).thenReturn(true);
        when(persistencePort.findById("cu")).thenReturn(Optional.of(existente));
        when(persistencePort.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("x") { });

        assertThatThrownBy(() -> useCase.ejecutar(input))
                .isInstanceOf(RuntimeException.class);
        verify(outboxPort, never()).save(any());
    }
}
