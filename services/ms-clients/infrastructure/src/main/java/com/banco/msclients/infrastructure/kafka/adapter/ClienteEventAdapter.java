package com.banco.msclients.infrastructure.kafka.adapter;

import com.banco.msclients.application.port.out.ClienteEventPort;
import com.banco.msclients.domain.model.Cliente;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * ClienteEventAdapter - Mock implementation of ClienteEventPort
 * Logs events instead of publishing to Kafka (for local testing).
 */
@Component
public class ClienteEventAdapter implements ClienteEventPort {
    
    private static final Logger log = LoggerFactory.getLogger(ClienteEventAdapter.class);
    
    @Override
    public void publicarClienteCreado(Cliente cliente) {
        log.info("EVENT: ClienteCreated - clienteId: {}, identificacion: {}, nombre: {}", 
            cliente.getClienteId(), cliente.getIdentificacion(), cliente.getNombre());
    }
}