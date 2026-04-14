package com.zevaguillo.infrastructure.persistence.adapter;

import com.zevaguillo.application.port.out.ClienteCachePersistencePort;
import com.zevaguillo.domain.model.ClienteCache;
import com.zevaguillo.infrastructure.persistence.entity.ClienteCacheEntity;
import com.zevaguillo.infrastructure.persistence.entity.ClienteCacheRepository;
import org.springframework.stereotype.Component;

@Component
public class ClienteCacheJpaAdapter implements ClienteCachePersistencePort {

    private final ClienteCacheRepository repository;

    public ClienteCacheJpaAdapter(ClienteCacheRepository repository) {
        this.repository = repository;
    }

    @Override
    public void upsert(ClienteCache cliente) {
        ClienteCacheEntity entity = new ClienteCacheEntity();
        entity.setClienteId(cliente.getClienteId());
        entity.setNombre(cliente.getNombre());
        entity.setEstado(cliente.getEstado() != null ? cliente.getEstado() : "ACTIVE");
        entity.setSyncedAt(cliente.getSyncedAt());
        repository.save(entity);
    }

    @Override
    public boolean existsById(String clienteId) {
        return repository.existsById(clienteId);
    }
}
