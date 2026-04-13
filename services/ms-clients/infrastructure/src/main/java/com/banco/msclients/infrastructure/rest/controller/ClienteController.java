package com.banco.msclients.infrastructure.rest.controller;

import com.banco.msclients.application.port.in.ActualizarClienteUseCase;
import com.banco.msclients.application.port.in.CrearClienteUseCase;
import com.banco.msclients.application.port.in.EliminarClienteUseCase;
import com.banco.msclients.application.port.in.ObtenerClienteUseCase;
import com.banco.msclients.domain.model.Cliente;
import com.banco.msclients.infrastructure.rest.dto.ClienteRequest;
import com.banco.msclients.infrastructure.rest.dto.ClienteResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClienteController - REST controller for Cliente endpoints
 * Exposes HTTP endpoints for Cliente CRUD operations.
 */
@RestController
@RequestMapping("/clientes")
public class ClienteController {
    
    private final CrearClienteUseCase crearUseCase;
    private final ObtenerClienteUseCase obtenerUseCase;
    private final ActualizarClienteUseCase actualizarUseCase;
    private final EliminarClienteUseCase eliminarUseCase;
    
    public ClienteController(CrearClienteUseCase crearUseCase,
                           ObtenerClienteUseCase obtenerUseCase,
                           ActualizarClienteUseCase actualizarUseCase,
                           EliminarClienteUseCase eliminarUseCase) {
        this.crearUseCase = crearUseCase;
        this.obtenerUseCase = obtenerUseCase;
        this.actualizarUseCase = actualizarUseCase;
        this.eliminarUseCase = eliminarUseCase;
    }
    
    /**
     * POST /clientes - Create a new cliente
     */
    @PostMapping
    public ResponseEntity<ClienteResponse> crear(@Validated(ClienteRequest.Create.class) @RequestBody ClienteRequest request) {
        Cliente cliente = request.toDomain();
        Cliente creado = crearUseCase.ejecutar(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(ClienteResponse.from(creado));
    }
    
    /**
     * GET /clientes - List all clientes
     */
    @GetMapping
    public ResponseEntity<List<ClienteResponse>> listar() {
        return ResponseEntity.ok(obtenerUseCase.todos().stream()
            .map(ClienteResponse::from).toList());
    }
    
    /**
     * GET /clientes/{id} - Get a cliente by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> obtener(@PathVariable String id) {
        return obtenerUseCase.porId(id)
            .map(c -> ResponseEntity.ok(ClienteResponse.from(c)))
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * PUT /clientes/{id} - Update an existing cliente
     */
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> actualizar(@PathVariable String id, 
                                                    @Validated(ClienteRequest.Update.class) @RequestBody ClienteRequest request) {
        Cliente actualizado = actualizarUseCase.ejecutar(id, request.toDomain());
        return ResponseEntity.ok(ClienteResponse.from(actualizado));
    }
    
    /**
     * DELETE /clientes/{id} - Delete (logical) a cliente
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        eliminarUseCase.ejecutar(id);
        return ResponseEntity.noContent().build();
    }
}