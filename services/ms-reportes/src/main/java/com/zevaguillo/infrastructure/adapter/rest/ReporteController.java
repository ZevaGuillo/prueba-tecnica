package com.zevaguillo.infrastructure.adapter.rest;

import com.zevaguillo.application.dto.ReporteResponse;
import com.zevaguillo.application.port.in.ConsultarReporteUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/reportes")
@Tag(name = "Reportes", description = "API para consulta de reportes de estado de cuenta")
public class ReporteController {

    private static final Logger log = LoggerFactory.getLogger(ReporteController.class);

    private final ConsultarReporteUseCase consultarReporteUseCase;

    public ReporteController(ConsultarReporteUseCase consultarReporteUseCase) {
        this.consultarReporteUseCase = consultarReporteUseCase;
    }

    @GetMapping
    @Operation(summary = "Consultar reporte de estado de cuenta", 
               description = "Retorna información del cliente, cuentas y movimientos en el rango de fechas especificado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Reporte generado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Parámetros inválidos"),
        @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    public ResponseEntity<ReporteResponse> consultar(
            @Parameter(description = "Correlation ID para trazabilidad", required = false)
            @RequestHeader(value = "X-Correlation-ID", required = false) String correlationId,
            
            @Parameter(description = "ID del cliente", required = true, example = "cli-001")
            @RequestParam String clienteId,
            
            @Parameter(description = "Fecha de inicio (YYYY-MM-DD)", required = true, example = "2026-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            
            @Parameter(description = "Fecha de fin (YYYY-MM-DD)", required = true, example = "2026-12-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            
            @Parameter(description = "Número de página", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Tamaño de página", example = "20")
            @RequestParam(defaultValue = "20") int size) {

        String corrId = correlationId != null ? correlationId : UUID.randomUUID().toString();

        if (fechaInicio.isAfter(fechaFin)) {
            return ResponseEntity.badRequest().build();
        }

        if (size < 1 || size > 100) {
            return ResponseEntity.badRequest().build();
        }

        log.info("[{}] GET /reportes - clienteId: {}, fechas: {} a {}", corrId, clienteId, fechaInicio, fechaFin);

        ReporteResponse response = consultarReporteUseCase.consultar(clienteId, fechaInicio, fechaFin, page, size);

        if (response.getCliente() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(response);
    }
}