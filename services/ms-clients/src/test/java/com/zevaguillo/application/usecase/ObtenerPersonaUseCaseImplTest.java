package com.zevaguillo.application.usecase;

import com.zevaguillo.application.port.out.PersonaPersistencePort;
import com.zevaguillo.domain.model.Persona;
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
class ObtenerPersonaUseCaseImplTest {

    @Mock
    private PersonaPersistencePort persistencePort;

    private ObtenerPersonaUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new ObtenerPersonaUseCaseImpl(persistencePort);
    }

    @Test
    void porIdDelega() {
        Persona p = new Persona();
        when(persistencePort.findById("id1")).thenReturn(Optional.of(p));

        assertThat(useCase.porId("id1")).containsSame(p);
    }

    @Test
    void porIdRechazaVacio() {
        assertThatThrownBy(() -> useCase.porId(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void todasDelega() {
        when(persistencePort.findAll()).thenReturn(List.of(new Persona()));

        assertThat(useCase.todas()).hasSize(1);
    }
}
