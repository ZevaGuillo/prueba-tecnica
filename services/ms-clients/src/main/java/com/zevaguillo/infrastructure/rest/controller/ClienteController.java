package com.zevaguillo.infrastructure.rest.controller;

import com.zevaguillo.application.port.in.ActualizarClienteUseCase;
import com.zevaguillo.application.port.in.CrearClienteUseCase;
import com.zevaguillo.application.port.in.EliminarClienteUseCase;
import com.zevaguillo.application.port.in.ObtenerClienteUseCase;
import com.zevaguillo.domain.model.Cliente;
import com.zevaguillo.infrastructure.rest.dto.ClienteRequest;
import com.zevaguillo.infrastructure.rest.dto.ClienteResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ClienteController - REST controller for Cliente endpoints
 * Exposes HTTP endpoints for Cliente CRUD operations.
 */
@RestController
@RequestMapping("/api/clientes")
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