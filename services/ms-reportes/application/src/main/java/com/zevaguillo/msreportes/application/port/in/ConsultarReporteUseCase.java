package com.zevaguillo.msreportes.application.port.in;

import com.zevaguillo.msreportes.application.dto.ReporteResponse;

import java.time.LocalDate;

public interface ConsultarReporteUseCase {
    ReporteResponse consultar(String clienteId, LocalDate fechaInicio, LocalDate fechaFin, int page, int size);
}