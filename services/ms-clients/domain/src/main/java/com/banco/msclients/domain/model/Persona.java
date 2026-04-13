package com.banco.msclients.domain.model;

/**
 * Persona - Domain Entity (PURE POJO - No framework annotations)
 * Represents a natural person in the banking system.
 */
public class Persona {
    
    private String identificacion;
    private String nombre;
    private String genero;  // M, F, O
    private Integer edad;
    private String direccion;
    private String telefono;
    
    // Default constructor
    public Persona() {
    }
    
    // All-args constructor
    public Persona(String identificacion, String nombre, String genero, 
                 Integer edad, String direccion, String telefono) {
        this.identificacion = identificacion;
        this.nombre = nombre;
        this.genero = genero;
        this.edad = edad;
        this.direccion = direccion;
        this.telefono = telefono;
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