package com.banco.msclients.infrastructure.rest.dto;

import com.banco.msclients.domain.model.Persona;

/**
 * PersonaRequest - Request DTO for Persona REST endpoints
 */
public class PersonaRequest {
    
    private String identificacion;
    private String nombre;
    private String genero;
    private Integer edad;
    private String direccion;
    private String telefono;
    
    // Default constructor
    public PersonaRequest() {
    }
    
    /**
     * Converts this DTO to a domain Persona object.
     * @return Persona domain object
     */
    public Persona toDomain() {
        Persona persona = new Persona();
        persona.setIdentificacion(this.identificacion);
        persona.setNombre(this.nombre);
        persona.setGenero(this.genero);
        persona.setEdad(this.edad);
        persona.setDireccion(this.direccion);
        persona.setTelefono(this.telefono);
        return persona;
    }
    
    // Getters and Setters
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
}