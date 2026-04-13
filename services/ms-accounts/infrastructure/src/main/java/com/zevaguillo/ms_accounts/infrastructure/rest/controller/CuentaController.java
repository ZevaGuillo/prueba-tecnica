package com.zevaguillo.ms_accounts.infrastructure.rest.controller;

import com.zevaguillo.ms_accounts.application.port.in.*;
import com.zevaguillo.ms_accounts.domain.model.Cuenta;
import com.zevaguillo.ms_accounts.infrastructure.rest.dto.CuentaRequest;
import com.zevaguillo.ms_accounts.infrastructure.rest.dto.CuentaResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaController {

    private final CrearCuentaUseCase crearCuentaUseCase;
    private final ObtenerCuentaUseCase obtenerCuentaUseCase;
    private final ActualizarCuentaUseCase actualizarCuentaUseCase;
    private final EliminarCuentaUseCase eliminarCuentaUseCase;

    public CuentaController(
            CrearCuentaUseCase crearCuentaUseCase,
            ObtenerCuentaUseCase obtenerCuentaUseCase,
            ActualizarCuentaUseCase actualizarCuentaUseCase,
            EliminarCuentaUseCase eliminarCuentaUseCase) {
        this.crearCuentaUseCase = crearCuentaUseCase;
        this.obtenerCuentaUseCase = obtenerCuentaUseCase;
        this.actualizarCuentaUseCase = actualizarCuentaUseCase;
        this.eliminarCuentaUseCase = eliminarCuentaUseCase;
    }

    @PostMapping
    public ResponseEntity<CuentaResponse> crear(@Validated(CuentaRequest.Create.class) @RequestBody CuentaRequest request) {
        Cuenta cuenta = new Cuenta();
        cuenta.setCuentaId(request.getCuentaId());
        cuenta.setNumeroCuenta(request.getNumeroCuenta());
        cuenta.setTipoCuenta(request.getTipoCuenta());
        cuenta.setSaldo(request.getSaldo());
        cuenta.setClienteId(request.getClienteId());

        Cuenta creado = crearCuentaUseCase.ejecutar(cuenta);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(creado));
    }

    @GetMapping("/{cuentaId}")
    public ResponseEntity<CuentaResponse> obtenerPorId(@PathVariable String cuentaId) {
        Cuenta cuenta = obtenerCuentaUseCase.obtenerPorId(cuentaId);
        return ResponseEntity.ok(toResponse(cuenta));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<CuentaResponse>> obtenerPorCliente(@PathVariable String clienteId) {
        List<Cuenta> cuentas = obtenerCuentaUseCase.obtenerPorCliente(clienteId);
        return ResponseEntity.ok(cuentas.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @GetMapping
    public ResponseEntity<List<CuentaResponse>> obtenerTodas() {
        List<Cuenta> cuentas = obtenerCuentaUseCase.obtenerTodas();
        return ResponseEntity.ok(cuentas.stream().map(this::toResponse).collect(Collectors.toList()));
    }

    @PatchMapping("/{cuentaId}")
    public ResponseEntity<CuentaResponse> actualizar(
            @PathVariable String cuentaId,
            @Validated(CuentaRequest.Update.class) @RequestBody CuentaRequest request) {
        Cuenta cuenta = new Cuenta();
        cuenta.setCuentaId(cuentaId);
        if (request.getEstado() != null) {
            cuenta.setEstado(request.getEstado());
        }

        Cuenta actualizado = actualizarCuentaUseCase.ejecutar(cuenta);
        return ResponseEntity.ok(toResponse(actualizado));
    }

    @DeleteMapping("/{cuentaId}")
    public ResponseEntity<Void> eliminar(@PathVariable String cuentaId) {
        eliminarCuentaUseCase.ejecutar(cuentaId);
        return ResponseEntity.noContent().build();
    }

    private CuentaResponse toResponse(Cuenta cuenta) {
        CuentaResponse response = new CuentaResponse();
        response.setCuentaId(cuenta.getCuentaId());
        response.setNumeroCuenta(cuenta.getNumeroCuenta());
        response.setTipoCuenta(cuenta.getTipoCuenta());
        response.setSaldo(cuenta.getSaldo());
        response.setEstado(cuenta.getEstado());
        response.setClienteId(cuenta.getClienteId());
        return response;
    }
}