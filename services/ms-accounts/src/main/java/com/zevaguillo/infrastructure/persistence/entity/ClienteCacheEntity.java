package com.zevaguillo.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "clientes_cache", schema = "msaccounts_schema")
public class ClienteCacheEntity {

    @Id
    @Column(name = "cliente_id")
    private String clienteId;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "estado", nullable = false)
    private String estado;

    @Column(name = "synced_at", nullable = false)
    private LocalDateTime syncedAt;

    public String getClienteId() {
        return clienteId;
    }

    public void setClienteId(String clienteId) {
        this.clienteId = clienteId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getSyncedAt() {
        return syncedAt;
    }

    public void setSyncedAt(LocalDateTime syncedAt) {
        this.syncedAt = syncedAt;
    }
}
