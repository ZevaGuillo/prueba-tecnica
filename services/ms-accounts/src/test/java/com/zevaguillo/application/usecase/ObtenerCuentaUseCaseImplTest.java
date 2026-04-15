package com.zevaguillo.application.usecase;

import com.zevaguillo.application.port.out.CuentaPersistencePort;
import com.zevaguillo.domain.model.Cuenta;
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
class ObtenerCuentaUseCaseImplTest {

    @Mock
    private CuentaPersistencePort persistencePort;

    private ObtenerCuentaUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new ObtenerCuentaUseCaseImpl(persistencePort);
    }

    @Test
    void obtenerPorIdOk() {
        Cuenta c = new Cuenta();
        when(persistencePort.findById("a")).thenReturn(Optional.of(c));

        assertThat(useCase.obtenerPorId("a")).isSameAs(c);
    }

    @Test
    void obtenerPorIdLanza() {
        when(persistencePort.findById("x")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.obtenerPorId("x"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void obtenerPorCliente() {
        when(persistencePort.findByClienteId("cli")).thenReturn(List.of(new Cuenta()));

        assertThat(useCase.obtenerPorCliente("cli")).hasSize(1);
    }

    @Test
    void obtenerTodas() {
        when(persistencePort.findAll()).thenReturn(List.of());

        assertThat(useCase.obtenerTodas()).isEmpty();
    }
}
