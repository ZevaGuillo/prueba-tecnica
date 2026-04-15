package com.zevaguillo.application.usecase;

import com.zevaguillo.application.port.out.ClientePersistencePort;
import com.zevaguillo.domain.model.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActualizarClienteUseCaseImplTest {

    @Mock
    private ClientePersistencePort persistencePort;
    @Mock
    private PasswordEncoder passwordEncoder;

    private ActualizarClienteUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new ActualizarClienteUseCaseImpl(persistencePort, passwordEncoder);
    }

    @Test
    void ejecutarActualizaYReencodePassword() {
        Cliente existente = new Cliente();
        existente.setClienteId("c1");
        existente.setEstado("ACTIVE");
        existente.setContrasena("hashOld");
        when(persistencePort.findById("c1")).thenReturn(Optional.of(existente));
        when(passwordEncoder.encode("new")).thenReturn("hashNew");

        Cliente input = new Cliente();
        input.setNombre("Otro");
        input.setContrasena("new");

        Cliente out = new Cliente();
        out.setClienteId("c1");
        when(persistencePort.save(any(Cliente.class))).thenReturn(out);

        Cliente result = useCase.ejecutar("c1", input);

        assertThat(result).isSameAs(out);
        verify(passwordEncoder).encode("new");
    }

    @Test
    void ejecutarPreservaHashSiPasswordBlank() {
        Cliente existente = new Cliente();
        existente.setClienteId("c1");
        existente.setContrasena("hashOld");
        existente.setEstado("ACTIVE");
        when(persistencePort.findById("c1")).thenReturn(Optional.of(existente));
        when(persistencePort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Cliente input = new Cliente();
        input.setNombre("X");
        input.setContrasena("   ");

        Cliente result = useCase.ejecutar("c1", input);

        assertThat(result.getContrasena()).isEqualTo("hashOld");
    }

    @Test
    void ejecutarPreservaEstadoSiNullEnInput() {
        Cliente existente = new Cliente();
        existente.setClienteId("c1");
        existente.setEstado("INACTIVE");
        existente.setContrasena("h");
        when(persistencePort.findById("c1")).thenReturn(Optional.of(existente));
        when(persistencePort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Cliente input = new Cliente();
        input.setEstado(null);

        Cliente result = useCase.ejecutar("c1", input);

        assertThat(result.getEstado()).isEqualTo("INACTIVE");
    }

    @Test
    void ejecutarRechazaClienteIdVacio() {
        assertThatThrownBy(() -> useCase.ejecutar("", new Cliente()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cliente ID");
    }

    @Test
    void ejecutarRechazaSiNoExiste() {
        when(persistencePort.findById("x")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.ejecutar("x", new Cliente()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no encontrado");
    }
}
