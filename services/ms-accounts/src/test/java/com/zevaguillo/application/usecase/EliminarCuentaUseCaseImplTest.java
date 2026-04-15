package com.zevaguillo.application.usecase;

import com.zevaguillo.application.exception.CuentaConSaldoActivoException;
import com.zevaguillo.application.port.out.CuentaPersistencePort;
import com.zevaguillo.domain.model.Cuenta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EliminarCuentaUseCaseImplTest {

    @Mock
    private CuentaPersistencePort persistencePort;

    private EliminarCuentaUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new EliminarCuentaUseCaseImpl(persistencePort);
    }

    @Test
    void ejecutarInactivaSinSaldo() {
        Cuenta c = Cuenta.crear("cu", "n", "A", 0.0, "cli");
        when(persistencePort.existsById("cu")).thenReturn(true);
        when(persistencePort.findById("cu")).thenReturn(Optional.of(c));
        when(persistencePort.save(any(Cuenta.class))).thenAnswer(inv -> inv.getArgument(0));

        useCase.ejecutar("cu");

        verify(persistencePort).save(any());
        assertThat(c.getEstado()).isEqualTo("INACTIVE");
    }

    @Test
    void ejecutarRechazaConSaldo() {
        Cuenta c = Cuenta.crear("cu", "n", "A", 50.0, "cli");
        when(persistencePort.existsById("cu")).thenReturn(true);
        when(persistencePort.findById("cu")).thenReturn(Optional.of(c));

        assertThatThrownBy(() -> useCase.ejecutar("cu"))
                .isInstanceOf(CuentaConSaldoActivoException.class);
    }

    @Test
    void ejecutarRechazaNoExiste() {
        when(persistencePort.existsById("x")).thenReturn(false);

        assertThatThrownBy(() -> useCase.ejecutar("x"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no encontrada");
    }
}
