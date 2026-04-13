package com.zevaguillo.msreportes.infrastructure.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zevaguillo.msreportes.application.dto.event.MovimientoRegistradoEvent;
import com.zevaguillo.msreportes.domain.model.ProcessedEvent;
import com.zevaguillo.msreportes.domain.model.ReporteMovimiento;
import com.zevaguillo.msreportes.infrastructure.persistence.mapper.ReporteEntityMapper;
import com.zevaguillo.msreportes.infrastructure.persistence.repository.ProcessedEventRepository;
import com.zevaguillo.msreportes.infrastructure.persistence.repository.ReporteMovimientoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MovimientoEventHandler {

    private static final Logger log = LoggerFactory.getLogger(MovimientoEventHandler.class);

    private final ReporteMovimientoRepository movimientoRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final ObjectMapper objectMapper;

    public MovimientoEventHandler(ReporteMovimientoRepository movimientoRepository,
                               ProcessedEventRepository processedEventRepository,
                               ObjectMapper objectMapper) {
        this.movimientoRepository = movimientoRepository;
        this.processedEventRepository = processedEventRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void handle(String eventJson) {
        try {
            MovimientoRegistradoEvent event = objectMapper.readValue(eventJson, MovimientoRegistradoEvent.class);
            String eventId = event.getEventId();

            if (processedEventRepository.existsById(eventId)) {
                log.info("Evento duplicado ignorado: {}", eventId);
                return;
            }

            MovimientoRegistradoEvent.Payload payload = event.getPayload();
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
            processedEventRepository.save(ReporteEntityMapper.toEntity(new ProcessedEvent(eventId, "MovimientoRegistradoEvent")));

            log.info("Movimiento procesado: {}", payload.getMovimientoId());
        } catch (Exception e) {
            log.error("Error procesando evento MovimientoRegistradoEvent: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
