package com.zevaguillo.infrastructure.rest;

import com.zevaguillo.domain.model.Cliente;
import com.zevaguillo.infrastructure.rest.dto.ClienteRequest;
import com.zevaguillo.infrastructure.rest.dto.ClienteResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Cliente DTO Tests")
class ClienteDtoTest {

    @Test
    @DisplayName("ClienteRequest.toDomain should map all fields")
    void clienteRequest_toDomain_mapsAllFields() {
        ClienteRequest request = new ClienteRequest();
        request.setClienteId("CLI001");
        request.setIdentificacion("1234567890");
        request.setNombre("Juan Perez");
        request.setGenero("M");
        request.setEdad(30);
        request.setDireccion("Calle 123");
        request.setTelefono("3001234567");
        request.setContrasena("password123");
        
        Cliente dominio = request.toDomain();
        
        assertThat(dominio.getClienteId()).isEqualTo("CLI001");
        assertThat(dominio.getIdentificacion()).isEqualTo("1234567890");
        assertThat(dominio.getNombre()).isEqualTo("Juan Perez");
        assertThat(dominio.getGenero()).isEqualTo("M");
        assertThat(dominio.getEdad()).isEqualTo(30);
        assertThat(dominio.getDireccion()).isEqualTo("Calle 123");
        assertThat(dominio.getTelefono()).isEqualTo("3001234567");
        assertThat(dominio.getContrasena()).isEqualTo("password123");
        assertThat(dominio.getEstado()).isEqualTo("ACTIVE");
    }
    
    @Test
    @DisplayName("ClienteResponse.from should map all fields")
    void clienteResponse_from_mapsAllFields() {
        Cliente dominio = Cliente.crear("CLI001", "1234567890", "Juan Perez", "M", 30, "pass");
        dominio.setDireccion("Calle 123");
        dominio.setTelefono("3001234567");
        
        ClienteResponse response = ClienteResponse.from(dominio);
        
        assertThat(response.getClienteId()).isEqualTo("CLI001");
        assertThat(response.getIdentificacion()).isEqualTo("1234567890");
        assertThat(response.getNombre()).isEqualTo("Juan Perez");
        assertThat(response.getGenero()).isEqualTo("M");
        assertThat(response.getEdad()).isEqualTo(30);
        assertThat(response.getDireccion()).isEqualTo("Calle 123");
        assertThat(response.getTelefono()).isEqualTo("3001234567");
        assertThat(response.getEstado()).isEqualTo("ACTIVE");
    }
    
    @Test
    @DisplayName("ClienteRequest.toDomain should default state to ACTIVE")
    void clienteRequest_toDomain_defaultsStateToActive() {
        ClienteRequest request = new ClienteRequest();
        request.setClienteId("CLI002");
        request.setNombre("Maria Garcia");
        
        Cliente dominio = request.toDomain();
        
        assertThat(dominio.getEstado()).isEqualTo("ACTIVE");
    }
    
    @Test
    @DisplayName("ClienteRequest.toDomain should handle null fields")
    void clienteRequest_toDomain_handlesNullFields() {
        ClienteRequest request = new ClienteRequest();
        
        Cliente dominio = request.toDomain();
        
        assertThat(dominio.getClienteId()).isNull();
        assertThat(dominio.getNombre()).isNull();
        assertThat(dominio.getEstado()).isEqualTo("ACTIVE");
    }
    
    @Test
    @DisplayName("ClienteResponse.from should handle inactive state")
    void clienteResponse_from_handlesInactiveState() {
        Cliente dominio = Cliente.crear("CLI003", "0987654321", "Pedro Martinez", "M", 40, "pass");
        dominio.desactivar();
        
        ClienteResponse response = ClienteResponse.from(dominio);
        
        assertThat(response.getEstado()).isEqualTo("INACTIVE");
    }
}