package com.zevaguillo.ms_accounts.application.usecase;

import com.zevaguillo.ms_accounts.application.port.in.RegistrarMovimientoUseCase;
import com.zevaguillo.ms_accounts.application.port.out.CuentaEventPort;
import com.zevaguillo.ms_accounts.application.port.out.CuentaPersistencePort;
import com.zevaguillo.ms_accounts.application.port.out.MovimientoPersistencePort;
import com.zevaguillo.ms_accounts.domain.model.Cuenta;
import com.zevaguillo.ms_accounts.domain.model.Movimiento;
import org.springframework.stereotype.Service;

@Service
public class RegistrarMovimientoUseCaseImpl implements RegistrarMovimientoUseCase {

    private final CuentaPersistencePort cuentaPersistencePort;
    private final MovimientoPersistencePort movimientoPersistencePort;
    private final CuentaEventPort eventPort;

    public RegistrarMovimientoUseCaseImpl(
            CuentaPersistencePort cuentaPersistencePort,
            MovimientoPersistencePort movimientoPersistencePort,
            CuentaEventPort eventPort) {
        this.cuentaPersistencePort = cuentaPersistencePort;
        this.movimientoPersistencePort = movimientoPersistencePort;
        this.eventPort = eventPort;
    }

    @Override
    public Movimiento ejecutar(Movimiento movimiento, String transactionId) {
        if (transactionId != null && !transactionId.isEmpty()) {
            if (movimientoPersistencePort.existsByTransactionId(transactionId)) {
                throw new IllegalStateException("Transacción duplicada: " + transactionId);
            }
        }

        if (movimiento.getCuentaId() == null) {
            throw new IllegalArgumentException("Cuenta ID es requerido");
        }
        if (movimiento.getTipoMovimiento() == null) {
            throw new IllegalArgumentException("Tipo de movimiento es requerido");
        }
        if (movimiento.getValor() == null || movimiento.getValor() <= 0) {
            throw new IllegalArgumentException("Valor debe ser mayor a 0");
        }

        Cuenta cuenta = cuentaPersistencePort.findById(movimiento.getCuentaId())
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada: " + movimiento.getCuentaId()));

        if (!cuenta.isActiva()) {
            throw new IllegalStateException("Cuenta no está activa");
        }

        Double nuevoSaldo;
        if ("RETIRO".equals(movimiento.getTipoMovimiento())) {
            if (cuenta.getSaldo() + movimiento.getValor() < 0) {
                throw new IllegalStateException("Saldo no disponible");
            }
            nuevoSaldo = cuenta.getSaldo() - movimiento.getValor();
        } else {
            nuevoSaldo = cuenta.getSaldo() + movimiento.getValor();
        }

        cuenta.setSaldo(nuevoSaldo);
        cuenta.setVersion(cuenta.getVersion() + 1);
        cuentaPersistencePort.save(cuenta);

        movimiento.setSaldoResultante(nuevoSaldo);
        if (transactionId != null && !transactionId.isEmpty()) {
            movimiento.setTransactionId(transactionId);
        }

        Movimiento guardado = movimientoPersistencePort.save(movimiento);
        eventPort.publicarMovimientoRegistrado(guardado);

        return guardado;
    }
}