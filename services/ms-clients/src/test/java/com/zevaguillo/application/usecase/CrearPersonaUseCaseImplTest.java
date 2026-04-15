package com.zevaguillo.application.usecase;

import com.zevaguillo.application.port.out.PersonaPersistencePort;
import com.zevaguillo.domain.model.Persona;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CrearPersonaUseCaseImplTest {

    @Mock
    private PersonaPersistencePort persistencePort;

    private CrearPersonaUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new CrearPersonaUseCaseImpl(persistencePort);
    }

    @Test
    void ejecutarGuarda() {
        Persona p = new Persona();
        p.setIdentificacion("i");
        p.setNombre("n");
        Persona saved = new Persona();
        when(persistencePort.save(any())).thenReturn(saved);

        assertThat(useCase.ejecutar(p)).isSameAs(saved);
        verify(persistencePort).save(p);
    }

    @Test
    void ejecutarRechazaSinIdentificacion() {
        Persona p = new Persona();
        p.setNombre("n");

        assertThatThrownBy(() -> useCase.ejecutar(p))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Identificacion");
    }

    @Test
    void ejecutarRechazaSinNombre() {
        Persona p = new Persona();
        p.setIdentificacion("i");

        assertThatThrownBy(() -> useCase.ejecutar(p))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Nombre");
    }
}
