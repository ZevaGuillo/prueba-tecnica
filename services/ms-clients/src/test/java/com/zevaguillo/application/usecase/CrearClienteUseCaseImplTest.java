package com.zevaguillo.application.usecase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zevaguillo.application.exception.ClienteYaExisteException;
import com.zevaguillo.application.port.out.ClientePersistencePort;
import com.zevaguillo.application.port.out.OutboxEventPersistencePort;
import com.zevaguillo.domain.model.Cliente;
import com.zevaguillo.domain.model.OutboxEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrearClienteUseCaseImplTest {

    @Mock
    private ClientePersistencePort persistencePort;
    @Mock
    private OutboxEventPersistencePort outboxPort;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ObjectMapper objectMapper;

    private CrearClienteUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new CrearClienteUseCaseImpl(persistencePort, outboxPort, passwordEncoder, objectMapper);
    }

    @Test
    void ejecutarPersisteClienteYOutbox() throws Exception {
        Cliente input = Cliente.crear("c1", "id1", "Nombre", "M", 20, "plain");
        when(persistencePort.existsById("c1")).thenReturn(false);
        when(passwordEncoder.encode("plain")).thenReturn("hash");
        Cliente guardado = new Cliente();
        guardado.setClienteId("c1");
        guardado.setIdentificacion("id1");
        when(persistencePort.save(any(Cliente.class))).thenReturn(guardado);
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        Cliente result = useCase.ejecutar(input);

        assertThat(result).isSameAs(guardado);
        verify(outboxPort).save(any(OutboxEvent.class));
    }

    @Test
    void ejecutarAsignaEstadoActiveSiNull() throws Exception {
        Cliente input = Cliente.crear("c2", "id2", "N", "F", 30, "p");
        input.setEstado(null);
        when(persistencePort.existsById("c2")).thenReturn(false);
        when(passwordEncoder.encode("p")).thenReturn("h");
        when(persistencePort.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        useCase.ejecutar(input);

        assertThat(input.getEstado()).isEqualTo("ACTIVE");
    }

    @Test
    void ejecutarRechazaIdentificacionVacia() {
        Cliente c = new Cliente();
        c.setClienteId("x");
        c.setNombre("n");
        c.setContrasena("p");

        assertThatThrownBy(() -> useCase.ejecutar(c))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Identificacion");
        verify(persistencePort, never()).save(any());
    }

    @Test
    void ejecutarRechazaNombreVacio() {
        Cliente c = new Cliente();
        c.setClienteId("x");
        c.setIdentificacion("i");
        c.setNombre("");
        c.setContrasena("p");

        assertThatThrownBy(() -> useCase.ejecutar(c))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Nombre");
    }

    @Test
    void ejecutarRechazaClienteIdInvalido() {
        Cliente c = new Cliente();
        c.setClienteId("  ");
        c.setIdentificacion("i");
        c.setNombre("n");
        c.setContrasena("p");

        assertThatThrownBy(() -> useCase.ejecutar(c))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cliente ID");
    }

    @Test
    void ejecutarLanzaSiClienteExiste() {
        Cliente c = Cliente.crear("dup", "i", "n", "M", 1, "p");
        when(persistencePort.existsById("dup")).thenReturn(true);

        assertThatThrownBy(() -> useCase.ejecutar(c))
                .isInstanceOf(ClienteYaExisteException.class);
    }

    @Test
    void ejecutarRechazaContrasenaVacia() {
        Cliente c = Cliente.crear("c", "i", "n", "M", 1, " ");
        c.setContrasena("  ");
        when(persistencePort.existsById("c")).thenReturn(false);

        assertThatThrownBy(() -> useCase.ejecutar(c))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Contrasena");
    }

    @Test
    void ejecutarPropagaErrorSerializacion() throws Exception {
        Cliente input = Cliente.crear("c3", "i3", "N", "M", 20, "p");
        when(persistencePort.existsById("c3")).thenReturn(false);
        when(passwordEncoder.encode("p")).thenReturn("h");
        when(persistencePort.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("x") { });

        assertThatThrownBy(() -> useCase.ejecutar(input))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("serializando");
        verify(outboxPort, never()).save(any());
    }

    @Test
    void ejecutarGuardaEventoConAggregateIdCorrecto() throws Exception {
        Cliente input = Cliente.crear("agg", "i", "N", "M", 20, "p");
        when(persistencePort.existsById("agg")).thenReturn(false);
        when(passwordEncoder.encode("p")).thenReturn("h");
        Cliente saved = new Cliente();
        saved.setClienteId("agg");
        when(persistencePort.save(any())).thenReturn(saved);
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        useCase.ejecutar(input);

        ArgumentCaptor<OutboxEvent> cap = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxPort).save(cap.capture());
        assertThat(cap.getValue().getAggregateId()).isEqualTo("agg");
        assertThat(cap.getValue().getEventType()).isEqualTo("ClienteCreado");
    }
}
