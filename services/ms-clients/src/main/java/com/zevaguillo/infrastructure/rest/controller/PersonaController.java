package com.zevaguillo.infrastructure.rest.controller;

import com.zevaguillo.application.port.in.CrearPersonaUseCase;
import com.zevaguillo.application.port.in.ObtenerPersonaUseCase;
import com.zevaguillo.domain.model.Persona;
import com.zevaguillo.infrastructure.rest.dto.PersonaRequest;
import com.zevaguillo.infrastructure.rest.dto.PersonaResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * PersonaController - REST controller for Persona endpoints
 * Exposes HTTP endpoints for Persona CRUD operations.
 */
@RestController
@RequestMapping("/api/personas")
@Validated
public class PersonaController {
    
    private final CrearPersonaUseCase crearUseCase;
    private final ObtenerPersonaUseCase obtenerUseCase;
    
    public PersonaController(CrearPersonaUseCase crearUseCase,
                           ObtenerPersonaUseCase obtenerUseCase) {
        this.crearUseCase = crearUseCase;
        this.obtenerUseCase = obtenerUseCase;
    }
    
    /**
     * POST /personas - Create a new persona
     */
    @PostMapping
    public ResponseEntity<PersonaResponse> crear(@Valid @RequestBody @Validated(PersonaRequest.Create.class) PersonaRequest request) {
        Persona persona = request.toDomain();
        Persona creada = crearUseCase.ejecutar(persona);
        return ResponseEntity.status(HttpStatus.CREATED).body(PersonaResponse.from(creada));
    }
    
    /**
     * GET /personas - List all personas
     */
    @GetMapping
    public ResponseEntity<List<PersonaResponse>> listar() {
        return ResponseEntity.ok(obtenerUseCase.todas().stream()
            .map(PersonaResponse::from).toList());
    }
    
    /**
     * GET /personas/{id} - Get a persona by identification
     */
    @GetMapping("/{id}")
    public ResponseEntity<PersonaResponse> obtener(@PathVariable String id) {
        return obtenerUseCase.porId(id)
            .map(p -> ResponseEntity.ok(PersonaResponse.from(p)))
            .orElse(ResponseEntity.notFound().build());
    }
}