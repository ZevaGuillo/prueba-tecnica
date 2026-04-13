package com.zevaguillo.ms_accounts.application.port.out;

/**
 * Output Port - Account Event Publishing (Kafka)
 */
public interface CuentaEventPort {
    void publicarCuentaCreada(Object event);
    void publicarCuentaActualizada(Object event);
    void publicarMovimientoRegistrado(Object event);
}