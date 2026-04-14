package com.zevaguillo.infrastructure.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.zevaguillo.application.dto.event.MovimientoRegistradoEvent;
import com.zevaguillo.domain.model.ProcessedEvent;
import com.zevaguillo.domain.model.ReporteMovimiento;
import com.zevaguillo.infrastructure.persistence.mapper.ReporteEntityMapper;
import com.zevaguillo.infrastructure.persistence.entity.ReporteCuentaEntity;
import com.zevaguillo.infrastructure.persistence.repository.ProcessedEventRepository;
import com.zevaguillo.infrastructure.persistence.repository.ReporteCuentaRepository;
import com.zevaguillo.infrastructure.persistence.repository.ReporteMovimientoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Component
public class MovimientoEventHandler {

    private static final Logger log = LoggerFactory.getLogger(MovimientoEventHandler.class);

    private final ReporteMovimientoRepository movimientoRepository;
    private final ReporteCuentaRepository cuentaRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper;

    public MovimientoEventHandler(ReporteMovimientoRepository movimientoRepository,
                               ReporteCuentaRepository cuentaRepository,
                               ProcessedEventRepository processedEventRepository,
                               ObjectMapper objectMapper) {
        this.movimientoRepository = movimientoRepository;
        this.cuentaRepository = cuentaRepository;
        this.processedEventRepository = processedEventRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void handle(String eventJson) {
        try {
            MovimientoRegistradoEvent event = objectMapper.readValue(eventJson, MovimientoRegistradoEvent.class);
            String eventId = event.getEventId();
            MovimientoRegistradoEvent.Payload payload = event.getPayload();

            if (payload == null) {
                // Compatibilidad con productores que envian el movimiento en el root del JSON.
                JsonNode root = objectMapper.readTree(eventJson);
                payload = new MovimientoRegistradoEvent.Payload();
                payload.setMovimientoId(root.path("movimientoId").asText(null));
                payload.setCuentaId(root.path("cuentaId").asText(null));
                String clienteId = root.path("clienteId").asText(null);
                if ((clienteId == null || clienteId.isBlank()) && payload.getCuentaId() != null) {
                    clienteId = cuentaRepository.findByCuentaId(payload.getCuentaId())
                            .map(ReporteCuentaEntity::getClienteId)
                            .orElse(null);
                }
                payload.setClienteId(clienteId);
                payload.setTipo(root.path("tipoMovimiento").asText(root.path("tipo").asText(null)));
                if (root.hasNonNull("valor")) {
                    payload.setMonto(root.get("valor").decimalValue());
                } else if (root.hasNonNull("monto")) {
                    payload.setMonto(root.get("monto").decimalValue());
                }
                if (root.hasNonNull("saldoResultante")) {
                    payload.setSaldoPosterior(root.get("saldoResultante").decimalValue());
                } else if (root.hasNonNull("saldoPosterior")) {
                    payload.setSaldoPosterior(root.get("saldoPosterior").decimalValue());
                }
                payload.setDescripcion(root.path("descripcion").asText(null));
                payload.setFecha(LocalDateTime.now());
                if (eventId == null || eventId.isBlank()) {
                    String tx = root.path("transactionId").asText(null);
                    eventId = (tx != null && !tx.isBlank()) ? tx : "mov-" + payload.getMovimientoId();
                }
            }

            if (eventId != null && processedEventRepository.existsById(eventId)) {
                log.info("Evento duplicado ignorado: {}", eventId);
                return;
            }

            ReporteMovimiento movimiento = new ReporteMovimiento(
                payload.getMovimientoId(),
                payload.getCuentaId(),
                payload.getClienteId(),
                payload.getTipo(),
                payload.getMonto(),
                payload.getSaldoPosterior(),
                payload.getDescripcion(),
                payload.getFecha()
            );

            movimientoRepository.save(ReporteEntityMapper.toEntity(movimiento));
            if (eventId != null && !eventId.isBlank()) {
                processedEventRepository.save(ReporteEntityMapper.toEntity(new ProcessedEvent(eventId, "MovimientoRegistradoEvent")));
            }

            log.info("Movimiento procesado: {}", payload.getMovimientoId());
        } catch (Exception e) {
            log.error("Error procesando evento MovimientoRegistradoEvent: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
