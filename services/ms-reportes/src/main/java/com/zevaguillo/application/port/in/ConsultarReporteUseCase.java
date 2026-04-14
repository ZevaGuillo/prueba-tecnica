package com.zevaguillo.application.port.in;

import com.zevaguillo.application.dto.ReporteResponse;

import java.time.LocalDate;

public interface ConsultarReporteUseCase {
    ReporteResponse consultar(String clienteId, LocalDate fechaInicio, LocalDate fechaFin, int page, int size);
}