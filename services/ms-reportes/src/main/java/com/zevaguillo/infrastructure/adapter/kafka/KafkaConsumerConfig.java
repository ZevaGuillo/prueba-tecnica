package com.zevaguillo.infrastructure.adapter.kafka;

import com.zevaguillo.infrastructure.handler.ClienteEventHandler;
import com.zevaguillo.infrastructure.handler.CuentaEventHandler;
import com.zevaguillo.infrastructure.handler.MovimientoEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumerConfig.class);

    private final ClienteEventHandler clienteEventHandler;
    private final CuentaEventHandler cuentaEventHandler;
    private final MovimientoEventHandler movimientoEventHandler;

    public KafkaConsumerConfig(ClienteEventHandler clienteEventHandler,
                           CuentaEventHandler cuentaEventHandler,
                           MovimientoEventHandler movimientoEventHandler) {
        this.clienteEventHandler = clienteEventHandler;
        this.cuentaEventHandler = cuentaEventHandler;
        this.movimientoEventHandler = movimientoEventHandler;
    }

    @KafkaListener(topics = {"cliente-events"}, groupId = "ms-reportes-group")
    public void listenClienteEvents(String message) {
        if (log.isInfoEnabled()) {
            log.info("Recibido evento de cliente: {}", message);
        }
        try {
            clienteEventHandler.handle(message);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Error procesando evento de cliente: {}", e.getMessage());
            }
        }
    }

    @KafkaListener(topics = {"cuenta-events", "cuenta-creada", "cuenta-actualizada"}, groupId = "ms-reportes-group")
    public void listenCuentaEvents(String message) {
        if (log.isInfoEnabled()) {
            log.info("Recibido evento de cuenta: {}", message);
        }
        try {
            cuentaEventHandler.handle(message);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Error procesando evento de cuenta: {}", e.getMessage());
            }
        }
    }

    @KafkaListener(topics = {"movimiento-events", "movimiento-registrado"}, groupId = "ms-reportes-group")
    public void listenMovimientoEvents(String message) {
        if (log.isInfoEnabled()) {
            log.info("Recibido evento de movimiento: {}", message);
        }
        try {
            movimientoEventHandler.handle(message);
        } catch (Exception e) {
            if (log.isErrorEnabled()) {
                log.error("Error procesando evento de movimiento: {}", e.getMessage());
            }
        }
    }
}