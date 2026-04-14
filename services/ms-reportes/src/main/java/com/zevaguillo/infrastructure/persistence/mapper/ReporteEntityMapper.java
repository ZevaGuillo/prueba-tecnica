package com.zevaguillo.infrastructure.persistence.mapper;

import com.zevaguillo.domain.model.ProcessedEvent;
import com.zevaguillo.domain.model.ReporteCliente;
import com.zevaguillo.domain.model.ReporteCuenta;
import com.zevaguillo.domain.model.ReporteMovimiento;
import com.zevaguillo.infrastructure.persistence.entity.ProcessedEventEntity;
import com.zevaguillo.infrastructure.persistence.entity.ReporteClienteEntity;
import com.zevaguillo.infrastructure.persistence.entity.ReporteCuentaEntity;
import com.zevaguillo.infrastructure.persistence.entity.ReporteMovimientoEntity;

public final class ReporteEntityMapper {

    private ReporteEntityMapper() {
    }

    public static ReporteClienteEntity toEntity(ReporteCliente d) {
        ReporteClienteEntity e = new ReporteClienteEntity();
        e.setId(d.getId());
        e.setClienteId(d.getClienteId());
        e.setNombre(d.getNombre());
        e.setIdentificacion(d.getIdentificacion());
        e.setEmail(d.getEmail());
        e.setTelefono(d.getTelefono());
        e.setFechaCreacion(d.getFechaCreacion());
        e.setFechaActualizacion(d.getFechaActualizacion());
        return e;
    }

    public static ReporteCuentaEntity toEntity(ReporteCuenta d) {
        ReporteCuentaEntity e = new ReporteCuentaEntity();
        e.setId(d.getId());
        e.setCuentaId(d.getCuentaId());
        e.setClienteId(d.getClienteId());
        e.setNumeroCuenta(d.getNumeroCuenta());
        e.setTipo(d.getTipo());
        e.setSaldoActual(d.getSaldoActual());
        e.setMoneda(d.getMoneda());
        e.setEstado(d.getEstado());
        e.setFechaCreacion(d.getFechaCreacion());
        return e;
    }

    public static ReporteMovimientoEntity toEntity(ReporteMovimiento d) {
        ReporteMovimientoEntity e = new ReporteMovimientoEntity();
        e.setId(d.getId());
        e.setMovimientoId(d.getMovimientoId());
        e.setCuentaId(d.getCuentaId());
        e.setClienteId(d.getClienteId());
        e.setTipo(d.getTipo());
        e.setMonto(d.getMonto());
        e.setSaldoPosterior(d.getSaldoPosterior());
        e.setDescripcion(d.getDescripcion());
        e.setFecha(d.getFecha());
        e.setFechaProcesamiento(d.getFechaProcesamiento());
        return e;
    }

    public static ProcessedEventEntity toEntity(ProcessedEvent d) {
        ProcessedEventEntity e = new ProcessedEventEntity();
        e.setEventId(d.getEventId());
        e.setEventType(d.getEventType());
        e.setFechaProcesamiento(d.getFechaProcesamiento());
        return e;
    }
}
