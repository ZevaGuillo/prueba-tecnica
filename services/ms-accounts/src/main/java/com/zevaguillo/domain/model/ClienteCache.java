package com.zevaguillo.domain.model;

import java.time.LocalDateTime;

public class ClienteCache {

    private String clienteId;
    private String nombre;
    private String estado;
    private LocalDateTime syncedAt;

    public ClienteCache() {}

    public ClienteCache(String clienteId, String nombre, String estado, LocalDateTime syncedAt) {
        this.clienteId = clienteId;
        this.nombre = nombre;
        this.estado = estado;
        this.syncedAt = syncedAt;
    }

    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getSyncedAt() { return syncedAt; }
    public void setSyncedAt(LocalDateTime syncedAt) { this.syncedAt = syncedAt; }
}
