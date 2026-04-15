package com.zevaguillo.infrastructure.handler;

import com.zevaguillo.application.dto.ReporteResponse;
import com.zevaguillo.infrastructure.persistence.entity.ReporteClienteEntity;
import com.zevaguillo.infrastructure.persistence.entity.ReporteCuentaEntity;
import com.zevaguillo.infrastructure.persistence.repository.ReporteClienteRepository;
import com.zevaguillo.infrastructure.persistence.repository.ReporteCuentaRepository;
import com.zevaguillo.infrastructure.persistence.repository.ReporteMovimientoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarReporteUseCaseImplTest {

    @Mock
    private ReporteClienteRepository clienteRepository;

    @Mock
    private ReporteCuentaRepository cuentaRepository;

    @Mock
    private ReporteMovimientoRepository movimientoRepository;

    @InjectMocks
    private ConsultarReporteUseCaseImpl useCase;

    @Test
    void consultarSinClienteDevuelveClienteNull() {
        when(clienteRepository.findByClienteId("x")).thenReturn(List.of());
        when(cuentaRepository.findByClienteId("x")).thenReturn(List.of());
        when(movimientoRepository.findByClienteIdAndFechaBetweenOrderByFechaDesc(
                eq("x"), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of()));

        ReporteResponse r = useCase.consultar(
                "x", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), 0, 20);

        assertThat(r.getCliente()).isNull();
        assertThat(r.getCuentas()).isEmpty();
        assertThat(r.getMovimientos()).isEmpty();
        assertThat(r.getPagination().getPage()).isEqualTo(0);
    }

    @Test
    void consultarConCliente() {
        ReporteClienteEntity cliente = new ReporteClienteEntity();
        cliente.setId(UUID.randomUUID());
        cliente.setClienteId("cli-1");
        cliente.setNombre("N");
        cliente.setIdentificacion("ID");
        cliente.setEmail("e@mail.com");
        cliente.setTelefono("1");

        when(clienteRepository.findByClienteId("cli-1")).thenReturn(List.of(cliente));
        when(cuentaRepository.findByClienteId("cli-1")).thenReturn(List.of());
        when(movimientoRepository.findByClienteIdAndFechaBetweenOrderByFechaDesc(
                eq("cli-1"), any(), any(), eq(PageRequest.of(0, 20))))
                .thenReturn(new PageImpl<>(List.of()));

        ReporteResponse r = useCase.consultar(
                "cli-1", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), 0, 20);

        assertThat(r.getCliente()).isNotNull();
        assertThat(r.getCliente().getClienteId()).isEqualTo("cli-1");
        assertThat(r.getCliente().getNombre()).isEqualTo("N");
    }

    @Test
    void consultarIncluyeCuentas() {
        ReporteCuentaEntity cuenta = new ReporteCuentaEntity();
        cuenta.setId(UUID.randomUUID());
        cuenta.setCuentaId("cu1");
        cuenta.setClienteId("cli");
        cuenta.setNumeroCuenta("001");
        cuenta.setTipo("AHORRO");
        cuenta.setSaldoActual(BigDecimal.TEN);
        cuenta.setMoneda("USD");
        cuenta.setEstado("ACTIVE");
        cuenta.setFechaCreacion(java.time.LocalDateTime.now());

        when(clienteRepository.findByClienteId("cli")).thenReturn(List.of());
        when(cuentaRepository.findByClienteId("cli")).thenReturn(List.of(cuenta));
        when(movimientoRepository.findByClienteIdAndFechaBetweenOrderByFechaDesc(
                eq("cli"), any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of()));

        ReporteResponse r = useCase.consultar(
                "cli", LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31), 0, 20);

        assertThat(r.getCuentas()).hasSize(1);
        assertThat(r.getCuentas().get(0).getNumeroCuenta()).isEqualTo("001");
    }
}
