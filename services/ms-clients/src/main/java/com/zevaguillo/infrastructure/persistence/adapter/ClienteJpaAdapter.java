package com.zevaguillo.infrastructure.persistence.adapter;

import com.zevaguillo.application.port.out.ClientePersistencePort;
import com.zevaguillo.domain.model.Cliente;
import com.zevaguillo.infrastructure.persistence.entity.ClienteEntity;
import com.zevaguillo.infrastructure.persistence.entity.ClienteRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ClienteJpaAdapter - JPA implementation of ClientePersistencePort
 * Adapter that transforms between domain and JPA entities.
 */
@Repository
public class ClienteJpaAdapter implements ClientePersistencePort {
    
    private final ClienteRepository repository;
    
    public ClienteJpaAdapter(ClienteRepository repository) {
        this.repository = repository;
    }
    
    @Override
    public Cliente save(Cliente cliente) {
        ClienteEntity entity = toEntity(cliente);
        ClienteEntity saved = repository.save(entity);
        return toDomain(saved);
    }
    
    @Override
    public Optional<Cliente> findById(String clienteId) {
        return repository.findById(clienteId).map(this::toDomain);
    }
    
    @Override
    public List<Cliente> findAll() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }
    
    @Override
    public List<Cliente> findByEstado(String estado) {
        return repository.findByEstado(estado).stream().map(this::toDomain).toList();
    }
    
    @Override
    public void delete(String clienteId) {
        repository.deleteById(clienteId);
    }
    
    @Override
    public boolean existsById(String clienteId) {
        return repository.existsById(clienteId);
    }
    
    // Mapper methods
    private ClienteEntity toEntity(Cliente cliente) {
        ClienteEntity entity = new ClienteEntity();
        entity.setIdentificacion(cliente.getIdentificacion());
        entity.setNombre(cliente.getNombre());
        entity.setGenero(cliente.getGenero());
        entity.setEdad(cliente.getEdad());
        entity.setDireccion(cliente.getDireccion());
        entity.setTelefono(cliente.getTelefono());
        entity.setClienteId(cliente.getClienteId());
        entity.setContrasena(cliente.getContrasena());
        entity.setEstado(cliente.getEstado());
        return entity;
    }
    
    private Cliente toDomain(ClienteEntity entity) {
        Cliente cliente = new Cliente();
        cliente.setIdentificacion(entity.getIdentificacion());
        cliente.setNombre(entity.getNombre());
        cliente.setGenero(entity.getGenero());
        cliente.setEdad(entity.getEdad());
        cliente.setDireccion(entity.getDireccion());
        cliente.setTelefono(entity.getTelefono());
        cliente.setClienteId(entity.getClienteId());
        cliente.setContrasena(entity.getContrasena());
        cliente.setEstado(entity.getEstado());
        return cliente;
    }
}