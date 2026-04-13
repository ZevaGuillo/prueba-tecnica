package com.zevaguillo.msreportes.application.port.in;

public interface EventProcessor {
    void processClienteCreado(String eventJson);
    void processCuentaCreada(String eventJson);
    void processMovimientoRegistrado(String eventJson);
}