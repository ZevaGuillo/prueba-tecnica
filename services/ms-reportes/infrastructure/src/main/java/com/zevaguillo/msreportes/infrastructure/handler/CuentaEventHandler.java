package com.zevaguillo.msreportes.infrastructure.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zevaguillo.msreportes.application.dto.event.CuentaCreadaEvent;
import com.zevaguillo.msreportes.domain.model.ProcessedEvent;
import com.zevaguillo.msreportes.domain.model.ReporteCuenta;
import com.zevaguillo.msreportes.infrastructure.persistence.mapper.ReporteEntityMapper;
import com.zevaguillo.msreportes.infrastructure.persistence.repository.ProcessedEventRepository;
import com.zevaguillo.msreportes.infrastructure.persistence.repository.ReporteCuentaRepository;
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

            if (processedEventRepository.existsById(eventId)) {
                log.info("Evento duplicado ignorado: {}", eventId);
                return;
            }

            CuentaCreadaEvent.Payload payload = event.getPayload();
            ReporteCuenta cuenta = new ReporteCuenta(
                payload.getCuentaId(),
                payload.getClienteId(),
                payload.getNumeroCuenta(),
                payload.getTipo(),
                payload.getSaldoInicial() != null ? payload.getSaldoInicial() : java.math.BigDecimal.ZERO,
                payload.getMoneda()
            );

            cuentaRepository.save(ReporteEntityMapper.toEntity(cuenta));
            processedEventRepository.save(ReporteEntityMapper.toEntity(new ProcessedEvent(eventId, "CuentaCreadaEvent")));

            log.info("Cuenta procesada: {}", payload.getCuentaId());
        } catch (Exception e) {
            log.error("Error procesando evento CuentaCreadaEvent: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
