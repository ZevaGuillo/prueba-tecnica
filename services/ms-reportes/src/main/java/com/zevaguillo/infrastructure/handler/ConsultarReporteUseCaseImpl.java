package com.zevaguillo.infrastructure.handler;

import com.zevaguillo.application.dto.ReporteResponse;
import com.zevaguillo.application.port.in.ConsultarReporteUseCase;
import com.zevaguillo.infrastructure.persistence.entity.ReporteClienteEntity;
import com.zevaguillo.infrastructure.persistence.entity.ReporteCuentaEntity;
import com.zevaguillo.infrastructure.persistence.entity.ReporteMovimientoEntity;
import com.zevaguillo.infrastructure.persistence.repository.ReporteClienteRepository;
import com.zevaguillo.infrastructure.persistence.repository.ReporteCuentaRepository;
import com.zevaguillo.infrastructure.persistence.repository.ReporteMovimientoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ConsultarReporteUseCaseImpl implements ConsultarReporteUseCase {

    private static final Logger log = LoggerFactory.getLogger(ConsultarReporteUseCaseImpl.class);

    private final ReporteClienteRepository clienteRepository;
    private final ReporteCuentaRepository cuentaRepository;
    private final ReporteMovimientoRepository movimientoRepository;

    public ConsultarReporteUseCaseImpl(ReporteClienteRepository clienteRepository,
                                   ReporteCuentaRepository cuentaRepository,
                                   ReporteMovimientoRepository movimientoRepository) {
        this.clienteRepository = clienteRepository;
        this.cuentaRepository = cuentaRepository;
        this.movimientoRepository = movimientoRepository;
    }

    @Override
    public ReporteResponse consultar(String clienteId, LocalDate fechaInicio, LocalDate fechaFin, int page, int size) {
        log.info("Consultando reporte para clienteId={} desde {} hasta {}", clienteId, fechaInicio, fechaFin);

        Optional<ReporteClienteEntity> clienteOpt = clienteRepository.findByClienteId(clienteId).stream().findFirst();

        ReporteResponse.ClienteDto clienteDto = null;
        if (clienteOpt.isPresent()) {
            ReporteClienteEntity cliente = clienteOpt.get();
            clienteDto = new ReporteResponse.ClienteDto(
                cliente.getClienteId(),
                cliente.getNombre(),
                cliente.getIdentificacion(),
                cliente.getEmail(),
                cliente.getTelefono()
            );
        }

        List<ReporteCuentaEntity> cuentas = cuentaRepository.findByClienteId(clienteId);
        List<ReporteResponse.CuentaDto> cuentaDtos = cuentas.stream()
            .map(c -> new ReporteResponse.CuentaDto(
                c.getCuentaId(),
                c.getNumeroCuenta(),
                c.getTipo(),
                c.getSaldoActual(),
                c.getMoneda(),
                c.getEstado()
            ))
            .toList();

        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(23, 59, 59);

        Page<ReporteMovimientoEntity> movimientosPage = movimientoRepository
            .findByClienteIdAndFechaBetweenOrderByFechaDesc(clienteId, inicio, fin, PageRequest.of(page, size));

        List<ReporteResponse.MovimientoDto> movimientoDtos = movimientosPage.getContent().stream()
            .map(m -> new ReporteResponse.MovimientoDto(
                m.getMovimientoId(),
                m.getCuentaId(),
                m.getTipo(),
                m.getMonto(),
                m.getSaldoPosterior(),
                m.getDescripcion(),
                m.getFecha()
            ))
            .toList();

        ReporteResponse.PaginationDto pagination = new ReporteResponse.PaginationDto(
            page,
            size,
            movimientosPage.getTotalElements(),
            movimientosPage.getTotalPages()
        );
        ReporteResponse.MetadataDto metadata = new ReporteResponse.MetadataDto(LocalDateTime.now(), 0);

        return new ReporteResponse(clienteDto, cuentaDtos, movimientoDtos, pagination, metadata);
    }
}
