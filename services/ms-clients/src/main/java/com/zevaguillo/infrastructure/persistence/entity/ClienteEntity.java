package com.zevaguillo.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * ClienteEntity - JPA Entity for Cliente
 * Maps to the 'cliente' table in msclients_schema.
 */
@Entity
@Table(name = "cliente", schema = "msclients_schema")
public class ClienteEntity {
    
    @Id
    @Column(name = "cliente_id", length = 20)
    private String clienteId;
    
    @Column(name = "identificacion", length = 20)
    private String identificacion;
    
    @Column(name = "nombre", length = 100)
    private String nombre;
    
    @Column(name = "genero", length = 1)
    private String genero;
    
    @Column(name = "edad")
    private Integer edad;
    
    @Column(name = "direccion", length = 200)
    private String direccion;
    
    @Column(name = "telefono", length = 20)
    private String telefono;
    
    @Column(name = "contrasena", nullable = false)
    private String contrasena;
    
    @Column(name = "estado", nullable = false, length = 10)
    private String estado;
    
    public ClienteEntity() {
    }
    
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
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
}