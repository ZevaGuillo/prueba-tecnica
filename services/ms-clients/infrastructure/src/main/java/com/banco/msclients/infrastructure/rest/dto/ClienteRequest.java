package com.banco.msclients.infrastructure.rest.dto;

import com.banco.msclients.domain.model.Cliente;

/**
 * ClienteRequest - Request DTO for Cliente REST endpoints
 */
public class ClienteRequest {
    
    private String clienteId;
    private String identificacion;
    private String nombre;
    private String genero;
    private Integer edad;
    private String direccion;
    private String telefono;
    private String contrasena;
    
    // Default constructor
    public ClienteRequest() {
    }
    
    /**
     * Converts this DTO to a domain Cliente object.
     * @return Cliente domain object
     */
    public Cliente toDomain() {
        Cliente cliente = new Cliente();
        cliente.setClienteId(this.clienteId);
        cliente.setIdentificacion(this.identificacion);
        cliente.setNombre(this.nombre);
        cliente.setGenero(this.genero);
        cliente.setEdad(this.edad);
        cliente.setDireccion(this.direccion);
        cliente.setTelefono(this.telefono);
        cliente.setContrasena(this.contrasena);
        cliente.setEstado("ACTIVE");
        return cliente;
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
    
    public String getContrasena() {
        return contrasena;
    }
    
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}