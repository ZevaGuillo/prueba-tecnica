package com.zevaguillo.infrastructure.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.zevaguillo.application.dto.event.CuentaCreadaEvent;
import com.zevaguillo.domain.model.ProcessedEvent;
import com.zevaguillo.domain.model.ReporteCuenta;
import com.zevaguillo.infrastructure.persistence.mapper.ReporteEntityMapper;
import com.zevaguillo.infrastructure.persistence.repository.ProcessedEventRepository;
import com.zevaguillo.infrastructure.persistence.repository.ReporteCuentaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CuentaEventHandler {

    private static final Logger log = LoggerFactory.getLogger(CuentaEventHandler.class);

    private final ReporteCuentaRepository cuentaRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper;

    public CuentaEventHandler(ReporteCuentaRepository cuentaRepository,
                           ProcessedEventRepository processedEventRepository,
                           ObjectMapper objectMapper) {
        this.cuentaRepository = cuentaRepository;
        this.processedEventRepository = processedEventRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void handle(String eventJson) {
        try {
            CuentaCreadaEvent event = objectMapper.readValue(eventJson, CuentaCreadaEvent.class);
            String eventId = event.getEventId();
            CuentaCreadaEvent.Payload payload = event.getPayload();

            if (payload == null) {
                // Compatibilidad con productores que envian la cuenta en el root del JSON.
                JsonNode root = objectMapper.readTree(eventJson);
                payload = new CuentaCreadaEvent.Payload();
                payload.setCuentaId(root.path("cuentaId").asText(null));
                payload.setClienteId(root.path("clienteId").asText(null));
                payload.setNumeroCuenta(root.path("numeroCuenta").asText(null));
                payload.setTipo(root.path("tipoCuenta").asText(root.path("tipo").asText(null)));
                if (root.hasNonNull("saldo")) {
                    payload.setSaldoInicial(root.get("saldo").decimalValue());
                } else if (root.hasNonNull("saldoInicial")) {
                    payload.setSaldoInicial(root.get("saldoInicial").decimalValue());
                }
                payload.setMoneda(root.path("moneda").asText("USD"));
                if (eventId == null || eventId.isBlank()) {
                    eventId = "cuenta-" + payload.getCuentaId();
                }
            }

            if (eventId != null && processedEventRepository.existsById(eventId)) {
                log.info("Evento duplicado ignorado: {}", eventId);
                return;
            }

            ReporteCuenta cuenta = new ReporteCuenta(
                payload.getCuentaId(),
                payload.getClienteId(),
                payload.getNumeroCuenta(),
                payload.getTipo(),
                payload.getSaldoInicial() != null ? payload.getSaldoInicial() : java.math.BigDecimal.ZERO,
                payload.getMoneda()
            );

            cuentaRepository.save(ReporteEntityMapper.toEntity(cuenta));
            if (eventId != null && !eventId.isBlank()) {
                processedEventRepository.save(ReporteEntityMapper.toEntity(new ProcessedEvent(eventId, "CuentaCreadaEvent")));
            }

            log.info("Cuenta procesada: {}", payload.getCuentaId());
        } catch (Exception e) {
            log.error("Error procesando evento CuentaCreadaEvent: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
