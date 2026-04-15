package com.zevaguillo.infrastructure.rest.controller;

import com.zevaguillo.application.port.in.RegistrarMovimientoUseCase;
import com.zevaguillo.domain.model.Movimiento;
import com.zevaguillo.infrastructure.rest.dto.MovimientoRequest;
import com.zevaguillo.infrastructure.rest.dto.MovimientoResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movimientos")
public class MovimientoController {

    private final RegistrarMovimientoUseCase registrarMovimientoUseCase;

    public MovimientoController(RegistrarMovimientoUseCase registrarMovimientoUseCase) {
        this.registrarMovimientoUseCase = registrarMovimientoUseCase;
    }

    @PostMapping
    public ResponseEntity<MovimientoResponse> registrar(
            @Validated(MovimientoRequest.Create.class) @RequestBody MovimientoRequest request,
            @RequestHeader(value = "X-Transaction-Id", required = false) String transactionId) {
        
        Movimiento movimiento = new Movimiento();
        movimiento.setMovimientoId(request.getMovimientoId());
        movimiento.setCuentaId(request.getCuentaId());
        movimiento.setTipoMovimiento(request.getTipoMovimiento());
        movimiento.setValor(request.getValor());

        Movimiento registrado = registrarMovimientoUseCase.ejecutar(movimiento, transactionId);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(registrado));
    }

    private MovimientoResponse toResponse(Movimiento movimiento) {
        MovimientoResponse response = new MovimientoResponse();
        response.setMovimientoId(movimiento.getMovimientoId());
        response.setCuentaId(movimiento.getCuentaId());
        response.setTipoMovimiento(movimiento.getTipoMovimiento());
        response.setValor(movimiento.getValor());
        response.setSaldoResultante(movimiento.getSaldoResultante());
        response.setFecha(movimiento.getFecha());
        response.setTransactionId(movimiento.getTransactionId());
        return response;
    }
}