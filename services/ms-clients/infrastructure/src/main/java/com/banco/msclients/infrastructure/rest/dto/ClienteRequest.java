package com.banco.msclients.infrastructure.rest.dto;

import com.banco.msclients.domain.model.Cliente;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;

/**
 * ClienteRequest - Request DTO for Cliente REST endpoints
 */
public class ClienteRequest {
    
    @NotNull(groups = Create.class, message = "clienteId es requerido")
    @Null(groups = Update.class, message = "clienteId no debe proporcionarse en actualización")
    private String clienteId;

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

    @NotBlank(groups = Create.class, message = "contrasena es requerido")
    @Size(groups = Create.class, min = 6, message = "contrasena debe tener al menos 6 caracteres")
    private String contrasena;

    public interface Create {}
    public interface Update {}
    
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