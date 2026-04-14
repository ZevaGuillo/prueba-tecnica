package com.zevaguillo.domain.repository;

import com.zevaguillo.domain.model.Cliente;
import java.util.List;
import java.util.Optional;

public interface ClienteRepository {
    Cliente save(Cliente cliente);
    Optional<Cliente> findById(String clienteId);
    List<Cliente> findAll();
    List<Cliente> findByEstado(String estado);
    void delete(String clienteId);
    boolean existsById(String clienteId);
}