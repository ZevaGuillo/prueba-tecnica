package com.zevaguillo.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class ReporteCliente {
    private UUID id;
    private String clienteId;
    private String nombre;
    private String identificacion;
    private String email;
    private String telefono;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public ReporteCliente() {}

    public ReporteCliente(String clienteId, String nombre, String identificacion, String email, String telefono) {
        this.id = UUID.randomUUID();
        this.clienteId = clienteId;
        this.nombre = nombre;
        this.identificacion = identificacion;
        this.email = email;
        this.telefono = telefono;
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getIdentificacion() { return identificacion; }
    public void setIdentificacion(String identificacion) { this.identificacion = identificacion; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
}