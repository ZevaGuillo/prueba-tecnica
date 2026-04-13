package com.banco.msclients.infrastructure.rest.dto;

import com.banco.msclients.domain.model.Cliente;

/**
 * ClienteResponse - Response DTO for Cliente REST endpoints
 */
public class ClienteResponse {
    
    private String clienteId;
    private String identificacion;
    private String nombre;
    private String genero;
    private Integer edad;
    private String direccion;
    private String telefono;
    private String estado;
    
    // Default constructor
    public ClienteResponse() {
    }
    
    /**
     * Creates a ClienteResponse from a domain Cliente object.
     * @param cliente the domain Cliente
     * @return ClienteResponse
     */
    public static ClienteResponse from(Cliente cliente) {
        ClienteResponse response = new ClienteResponse();
        response.setClienteId(cliente.getClienteId());
        response.setIdentificacion(cliente.getIdentificacion());
        response.setNombre(cliente.getNombre());
        response.setGenero(cliente.getGenero());
        response.setEdad(cliente.getEdad());
        response.setDireccion(cliente.getDireccion());
        response.setTelefono(cliente.getTelefono());
        response.setEstado(cliente.getEstado());
        return response;
    }
    
    // Getters and Setters
    public String getClienteId() {
        return clienteId;
    }
    
    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }
    
    public String getIdentificacion() {
        return identificacion;
    }
    
    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getGenero() {
        return genero;
    }
    
    public void setGenero(String genero) {
        this.genero = genero;
    }
    
    public Integer getEdad() {
        return edad;
    }
    
    public void setEdad(Integer edad) {
        this.edad = edad;
    }
    
    public String getDireccion() {
        return direccion;
    }
    
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    
    public String getTelefono() {
        return telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
}