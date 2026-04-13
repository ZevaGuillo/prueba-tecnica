package com.banco.msclients.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Cliente Entity Tests")
class ClienteTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        
        @Test
        @DisplayName("Default constructor should create empty Cliente")
        void defaultConstructor_createsEmptyCliente() {
            Cliente cliente = new Cliente();
            
            assertThat(cliente.getClienteId()).isNull();
            assertThat(cliente.getContrasena()).isNull();
            assertThat(cliente.getEstado()).isNull();
        }
        
        @Test
        @DisplayName("All-args constructor should initialize all fields")
        void allArgsConstructor_initializesAllFields() {
            Cliente cliente = new Cliente(
                "CLI001",
                "password123",
                "ACTIVE",
                "1234567890",
                "Juan Perez",
                "M",
                30,
                "Calle 123",
                "3001234567"
            );
            
            assertThat(cliente.getClienteId()).isEqualTo("CLI001");
            assertThat(cliente.getContrasena()).isEqualTo("password123");
            assertThat(cliente.getEstado()).isEqualTo("ACTIVE");
            assertThat(cliente.getIdentificacion()).isEqualTo("1234567890");
            assertThat(cliente.getNombre()).isEqualTo("Juan Perez");
            assertThat(cliente.getGenero()).isEqualTo("M");
            assertThat(cliente.getEdad()).isEqualTo(30);
            assertThat(cliente.getDireccion()).isEqualTo("Calle 123");
            assertThat(cliente.getTelefono()).isEqualTo("3001234567");
        }
    }
    
    @Nested
    @DisplayName("Factory Method Tests")
    class FactoryMethodTests {
        
        @Test
        @DisplayName("crear should create active Cliente with all fields")
        void crear_shouldCreateActiveCliente() {
            Cliente cliente = Cliente.crear(
                "CLI002",
                "0987654321",
                "Maria Garcia",
                "F",
                25,
                "secreto"
            );
            
            assertThat(cliente.getClienteId()).isEqualTo("CLI002");
            assertThat(cliente.getIdentificacion()).isEqualTo("0987654321");
            assertThat(cliente.getNombre()).isEqualTo("Maria Garcia");
            assertThat(cliente.getGenero()).isEqualTo("F");
            assertThat(cliente.getEdad()).isEqualTo(25);
            assertThat(cliente.getContrasena()).isEqualTo("secreto");
            assertThat(cliente.getEstado()).isEqualTo("ACTIVE");
        }
        
        @Test
        @DisplayName("crear should set default state to ACTIVE")
        void crear_shouldSetDefaultStateToActive() {
            Cliente cliente = Cliente.crear(
                "CLI003",
                "111222333",
                "Pedro Martinez",
                "M",
                40,
                "password"
            );
            
            assertThat(cliente.isActivo()).isTrue();
        }
    }
    
    @Nested
    @DisplayName("Domain Methods Tests")
    class DomainMethodsTests {
        
        @Test
        @DisplayName("isActivo should return true when state is ACTIVE")
        void isActivo_returnsTrueWhenActive() {
            Cliente cliente = new Cliente();
            cliente.setEstado("ACTIVE");
            
            assertThat(cliente.isActivo()).isTrue();
        }
        
        @Test
        @DisplayName("isActivo should return false when state is INACTIVE")
        void isActivo_returnsFalseWhenInactive() {
            Cliente cliente = new Cliente();
            cliente.setEstado("INACTIVE");
            
            assertThat(cliente.isActivo()).isFalse();
        }
        
        @Test
        @DisplayName("isActivo should return false when state is null")
        void isActivo_returnsFalseWhenNull() {
            Cliente cliente = new Cliente();
            
            assertThat(cliente.isActivo()).isFalse();
        }
        
        @Test
        @DisplayName("desactivar should change state to INACTIVE")
        void desactivar_shouldChangeStateToInactive() {
            Cliente cliente = new Cliente();
            cliente.setEstado("ACTIVE");
            
            cliente.desactivar();
            
            assertThat(cliente.getEstado()).isEqualTo("INACTIVE");
            assertThat(cliente.isActivo()).isFalse();
        }
        
        @Test
        @DisplayName("activar should change state to ACTIVE")
        void activar_shouldChangeStateToActive() {
            Cliente cliente = new Cliente();
            cliente.setEstado("INACTIVE");
            
            cliente.activar();
            
            assertThat(cliente.getEstado()).isEqualTo("ACTIVE");
            assertThat(cliente.isActivo()).isTrue();
        }
        
        @Test
        @DisplayName("Toggle state: deactivate then activate")
        void toggleState_deactivateThenActivate() {
            Cliente cliente = new Cliente();
            cliente.setEstado("ACTIVE");
            
            assertThat(cliente.isActivo()).isTrue();
            
            cliente.desactivar();
            assertThat(cliente.isActivo()).isFalse();
            
            cliente.activar();
            assertThat(cliente.isActivo()).isTrue();
        }
    }
    
    @Nested
    @DisplayName("Getters and Setters Tests")
    class GettersSettersTests {
        
        @Test
        @DisplayName("All setters should update fields correctly")
        void setters_shouldUpdateAllFields() {
            Cliente cliente = new Cliente();
            
            cliente.setClienteId("CLI004");
            cliente.setContrasena("newpassword");
            cliente.setEstado("INACTIVE");
            
            assertThat(cliente.getClienteId()).isEqualTo("CLI004");
            assertThat(cliente.getContrasena()).isEqualTo("newpassword");
            assertThat(cliente.getEstado()).isEqualTo("INACTIVE");
        }
    }
}