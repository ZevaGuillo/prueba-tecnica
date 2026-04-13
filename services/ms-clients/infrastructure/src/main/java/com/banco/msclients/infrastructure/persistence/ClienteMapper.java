package com.banco.msclients.infrastructure.persistence;

import com.banco.msclients.domain.model.Cliente;
import com.banco.msclients.infrastructure.persistence.entity.ClienteEntity;

/**
 * Mapper utility for converting between domain and entity objects.
 */
public final class ClienteMapper {
    
    private ClienteMapper() {
    }
    
    public static ClienteEntity toEntity(Cliente cliente) {
        if (cliente == null) {
            return null;
        }
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
    
    public static Cliente toDomain(ClienteEntity entity) {
        if (entity == null) {
            return null;
        }
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