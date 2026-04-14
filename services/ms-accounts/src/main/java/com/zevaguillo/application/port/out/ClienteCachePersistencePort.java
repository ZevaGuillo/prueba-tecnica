package com.zevaguillo.application.port.out;

import com.zevaguillo.domain.model.ClienteCache;

public interface ClienteCachePersistencePort {
    void upsert(ClienteCache cliente);
    boolean existsById(String clienteId);
}
