package com.zevaguillo.application.usecase;

import com.zevaguillo.application.port.out.ClientePersistencePort;
import com.zevaguillo.domain.model.Cliente;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EliminarClienteUseCaseImplTest {

    @Mock
    private ClientePersistencePort persistencePort;

    private EliminarClienteUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new EliminarClienteUseCaseImpl(persistencePort);
    }

    @Test
    void ejecutarDesactivaCliente() {
        Cliente c = new Cliente();
        c.setClienteId("c1");
        c.setEstado("ACTIVE");
        when(persistencePort.findById("c1")).thenReturn(Optional.of(c));
        when(persistencePort.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.ejecutar("c1");

        ArgumentCaptor<Cliente> cap = ArgumentCaptor.forClass(Cliente.class);
        verify(persistencePort).save(cap.capture());
        assertThat(cap.getValue().getEstado()).isEqualTo("INACTIVE");
    }

    @Test
    void ejecutarRechazaIdVacio() {
        assertThatThrownBy(() -> useCase.ejecutar(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void ejecutarRechazaSiNoExiste() {
        when(persistencePort.findById("x")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.ejecutar("x"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no encontrado");
    }
}
