package com.zevaguillo.ms_accounts.infrastructure.kafka.adapter;

import com.zevaguillo.ms_accounts.application.port.out.CuentaEventPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CuentaEventAdapter implements CuentaEventPort {

    private static final Logger logger = LoggerFactory.getLogger(CuentaEventAdapter.class);
    private static final String CUENTA_CREADA_TOPIC = "cuenta-creada";
    private static final String CUENTA_ACTUALIZADA_TOPIC = "cuenta-actualizada";
    private static final String MOVIMIENTO_REGISTRADO_TOPIC = "movimiento-registrado";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public CuentaEventAdapter(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publicarCuentaCreada(Object event) {
        logger.info("Publicando evento CuentaCreada: {}", event);
        kafkaTemplate.send(CUENTA_CREADA_TOPIC, event);
    }

    @Override
    public void publicarCuentaActualizada(Object event) {
        logger.info("Publicando evento CuentaActualizada: {}", event);
        kafkaTemplate.send(CUENTA_ACTUALIZADA_TOPIC, event);
    }

    @Override
    public void publicarMovimientoRegistrado(Object event) {
        logger.info("Publicando evento MovimientoRegistrado: {}", event);
        kafkaTemplate.send(MOVIMIENTO_REGISTRADO_TOPIC, event);
    }
}