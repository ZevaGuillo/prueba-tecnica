package com.zevaguillo.infrastructure.rest.dto;

import com.zevaguillo.domain.model.Persona;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

/**
 * PersonaRequest - Request DTO for Persona REST endpoints
 */
public class PersonaRequest {
    
    @NotBlank(groups = Create.class, message = "identificacion es requerido")
    private String identificacion;

    @NotBlank(groups = Create.class, message = "nombre es requerido")
    private String nombre;

    @NotBlank(groups = Create.class, message = "genero es requerido")
    private String genero;

    @NotNull(groups = Create.class, message = "edad es requerido")
    @Min(groups = Create.class, value = 0, message = "edad debe ser mayor o igual a 0")
    @Max(groups = Create.class, value = 150, message = "edad debe ser menor o igual a 150")
    private Integer edad;

    private String direccion;
    private String telefono;

    public interface Create {}
    public interface Update {}
    
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