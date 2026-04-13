package com.zevaguillo.msreportes.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ReporteResponse {
    private ClienteDto cliente;
    private List<CuentaDto> cuentas;
    private List<MovimientoDto> movimientos;
    private PaginationDto pagination;
    private MetadataDto metadata;

    public ReporteResponse() {
    }

    public ReporteResponse(ClienteDto cliente, List<CuentaDto> cuentas, List<MovimientoDto> movimientos, 
                         PaginationDto pagination, MetadataDto metadata) {
        this.cliente = cliente;
        this.cuentas = cuentas;
        this.movimientos = movimientos;
        this.pagination = pagination;
        this.metadata = metadata;
    }

    public ClienteDto getCliente() {
        return cliente;
    }

    public void setCliente(ClienteDto cliente) {
        this.cliente = cliente;
    }

    public List<CuentaDto> getCuentas() {
        return cuentas;
    }

    public void setCuentas(List<CuentaDto> cuentas) {
        this.cuentas = cuentas;
    }

    public List<MovimientoDto> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<MovimientoDto> movimientos) {
        this.movimientos = movimientos;
    }

    public PaginationDto getPagination() {
        return pagination;
    }

    public void setPagination(PaginationDto pagination) {
        this.pagination = pagination;
    }

    public MetadataDto getMetadata() {
        return metadata;
    }

    public void setMetadata(MetadataDto metadata) {
        this.metadata = metadata;
    }

    public static class ClienteDto {
        private String clienteId;
        private String nombre;
        private String identificacion;
        private String email;
        private String telefono;

        public ClienteDto() {
        }

        public ClienteDto(String clienteId, String nombre, String identificacion, String email, String telefono) {
            this.clienteId = clienteId;
            this.nombre = nombre;
            this.identificacion = identificacion;
            this.email = email;
            this.telefono = telefono;
        }

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

        public String getIdentificacion() {
            return identificacion;
        }

        public void setIdentificacion(String identificacion) {
            this.identificacion = identificacion;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getTelefono() {
            return telefono;
        }

        public void setTelefono(String telefono) {
            this.telefono = telefono;
        }
    }

    public static class CuentaDto {
        private String cuentaId;
        private String numeroCuenta;
        private String tipo;
        private BigDecimal saldoActual;
        private String moneda;
        private String estado;

        public CuentaDto() {
        }

        public CuentaDto(String cuentaId, String numeroCuenta, String tipo, BigDecimal saldoActual, String moneda, String estado) {
            this.cuentaId = cuentaId;
            this.numeroCuenta = numeroCuenta;
            this.tipo = tipo;
            this.saldoActual = saldoActual;
            this.moneda = moneda;
            this.estado = estado;
        }

        public String getCuentaId() {
            return cuentaId;
        }

        public void setCuentaId(String cuentaId) {
            this.cuentaId = cuentaId;
        }

        public String getNumeroCuenta() {
            return numeroCuenta;
        }

        public void setNumeroCuenta(String numeroCuenta) {
            this.numeroCuenta = numeroCuenta;
        }

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        public BigDecimal getSaldoActual() {
            return saldoActual;
        }

        public void setSaldoActual(BigDecimal saldoActual) {
            this.saldoActual = saldoActual;
        }

        public String getMoneda() {
            return moneda;
        }

        public void setMoneda(String moneda) {
            this.moneda = moneda;
        }

        public String getEstado() {
            return estado;
        }

        public void setEstado(String estado) {
            this.estado = estado;
        }
    }

    public static class MovimientoDto {
        private String movimientoId;
        private String cuentaId;
        private String tipo;
        private BigDecimal monto;
        private BigDecimal saldoPosterior;
        private String descripcion;
        private LocalDateTime fecha;

        public MovimientoDto() {
        }

        public MovimientoDto(String movimientoId, String cuentaId, String tipo, BigDecimal monto, 
                         BigDecimal saldoPosterior, String descripcion, LocalDateTime fecha) {
            this.movimientoId = movimientoId;
            this.cuentaId = cuentaId;
            this.tipo = tipo;
            this.monto = monto;
            this.saldoPosterior = saldoPosterior;
            this.descripcion = descripcion;
            this.fecha = fecha;
        }

        public String getMovimientoId() {
            return movimientoId;
        }

        public void setMovimientoId(String movimientoId) {
            this.movimientoId = movimientoId;
        }

        public String getCuentaId() {
            return cuentaId;
        }

        public void setCuentaId(String cuentaId) {
            this.cuentaId = cuentaId;
        }

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        public BigDecimal getMonto() {
            return monto;
        }

        public void setMonto(BigDecimal monto) {
            this.monto = monto;
        }

        public BigDecimal getSaldoPosterior() {
            return saldoPosterior;
        }

        public void setSaldoPosterior(BigDecimal saldoPosterior) {
            this.saldoPosterior = saldoPosterior;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }

        public LocalDateTime getFecha() {
            return fecha;
        }

        public void setFecha(LocalDateTime fecha) {
            this.fecha = fecha;
        }
    }

    public static class PaginationDto {
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;

        public PaginationDto() {
        }

        public PaginationDto(int page, int size, long totalElements, int totalPages) {
            this.page = page;
            this.size = size;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
        }

        public int getPage() {
            return page;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public long getTotalElements() {
            return totalElements;
        }

        public void setTotalElements(long totalElements) {
            this.totalElements = totalElements;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }
    }

    public static class MetadataDto {
        private LocalDateTime ultimaActualizacion;
        private int lag;

        public MetadataDto() {
        }

        public MetadataDto(LocalDateTime ultimaActualizacion, int lag) {
            this.ultimaActualizacion = ultimaActualizacion;
            this.lag = lag;
        }

        public LocalDateTime getUltimaActualizacion() {
            return ultimaActualizacion;
        }

        public void setUltimaActualizacion(LocalDateTime ultimaActualizacion) {
            this.ultimaActualizacion = ultimaActualizacion;
        }

        public int getLag() {
            return lag;
        }

        public void setLag(int lag) {
            this.lag = lag;
        }
    }
}