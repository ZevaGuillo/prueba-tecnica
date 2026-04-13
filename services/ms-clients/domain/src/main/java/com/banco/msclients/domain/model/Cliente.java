package com.banco.msclients.domain.model;

/**
 * Cliente - Domain Entity (PURE POJO - No framework annotations)
 * Extends Persona with banking-specific fields.
 */
public class Cliente extends Persona {
    
    private String clienteId;
    private String contrasena;
    private String estado;  // ACTIVE, INACTIVE
    
    // Default constructor
    public Cliente() {
        super();
    }
    
    // All-args constructor
    public Cliente(String clienteId, String contrasena, String estado,
                 String identificacion, String nombre, String genero,
                 Integer edad, String direccion, String telefono) {
        super(identificacion, nombre, genero, edad, direccion, telefono);
        this.clienteId = clienteId;
        this.contrasena = contrasena;
        this.estado = estado;
    }
    
    // Factory method - create new active cliente
    public static Cliente crear(String clienteId, String identificacion, String nombre, 
                          String genero, Integer edad, String contrasena) {
        Cliente cliente = new Cliente();
        cliente.setClienteId(clienteId);
        cliente.setIdentificacion(identificacion);
        cliente.setNombre(nombre);
        cliente.setGenero(genero);
        cliente.setEdad(edad);
        cliente.setContrasena(contrasena);
        cliente.setEstado("ACTIVE");
        return cliente;
    }
    
    // Domain methods
    public boolean isActivo() {
        return "ACTIVE".equals(this.estado);
    }
    
    public void desactivar() {
        this.estado = "INACTIVE";
    }
    
    public void activar() {
        this.estado = "ACTIVE";
    }
    
    // Getters and Setters
    public String getClienteId() {
        return clienteId;
    }
    
    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }
    
    public String getContrasena() {
        return contrasena;
    }
    
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
}