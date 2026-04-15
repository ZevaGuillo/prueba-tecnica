package com.zevaguillo.application.usecase;

import com.zevaguillo.application.port.out.ClientePersistencePort;
import com.zevaguillo.domain.model.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObtenerClienteUseCaseImplTest {

    @Mock
    private ClientePersistencePort persistencePort;

    private ObtenerClienteUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new ObtenerClienteUseCaseImpl(persistencePort);
    }

    @Test
    void porIdDelega() {
        Cliente c = new Cliente();
        when(persistencePort.findById("a")).thenReturn(Optional.of(c));

        assertThat(useCase.porId("a")).containsSame(c);
    }

    @Test
    void porIdRechazaVacio() {
        assertThatThrownBy(() -> useCase.porId(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void todosDelega() {
        when(persistencePort.findAll()).thenReturn(List.of(new Cliente()));

        assertThat(useCase.todos()).hasSize(1);
    }

    @Test
    void activosDelega() {
        when(persistencePort.findByEstado("ACTIVE")).thenReturn(List.of());

        assertThat(useCase.activos()).isEmpty();
    }
}
