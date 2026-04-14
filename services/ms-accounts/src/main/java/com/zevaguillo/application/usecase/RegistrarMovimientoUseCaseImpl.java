package com.zevaguillo.application.usecase;

import com.zevaguillo.application.exception.TransaccionDuplicadaException;
import com.zevaguillo.application.port.in.RegistrarMovimientoUseCase;
import com.zevaguillo.application.port.out.CuentaEventPort;
import com.zevaguillo.application.port.out.CuentaPersistencePort;
import com.zevaguillo.application.port.out.MovimientoPersistencePort;
import com.zevaguillo.domain.model.Cuenta;
import com.zevaguillo.domain.model.Movimiento;
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
                throw new TransaccionDuplicadaException(
                        "Transaccion duplicada: X-Transaction-Id '" + transactionId + "' ya fue procesado. " +
                        "Si quieres registrar otro movimiento, usa un X-Transaction-Id diferente."
                );
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