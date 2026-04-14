package com.zevaguillo.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Persona Entity Tests")
class PersonaTest {

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        
        @Test
        @DisplayName("Default constructor should create empty Persona")
        void defaultConstructor_createsEmptyPersona() {
            Persona persona = new Persona();
            
            assertThat(persona.getIdentificacion()).isNull();
            assertThat(persona.getNombre()).isNull();
            assertThat(persona.getGenero()).isNull();
            assertThat(persona.getEdad()).isNull();
            assertThat(persona.getDireccion()).isNull();
            assertThat(persona.getTelefono()).isNull();
        }
        
        @Test
        @DisplayName("All-args constructor should initialize all fields")
        void allArgsConstructor_initializesAllFields() {
            Persona persona = new Persona(
                "1234567890",
                "Juan Perez",
                "M",
                30,
                "Calle 123",
                "3001234567"
            );
            
            assertThat(persona.getIdentificacion()).isEqualTo("1234567890");
            assertThat(persona.getNombre()).isEqualTo("Juan Perez");
            assertThat(persona.getGenero()).isEqualTo("M");
            assertThat(persona.getEdad()).isEqualTo(30);
            assertThat(persona.getDireccion()).isEqualTo("Calle 123");
            assertThat(persona.getTelefono()).isEqualTo("3001234567");
        }
    }
    
    @Nested
    @DisplayName("Setters Tests")
    class SettersTests {
        
        @Test
        @DisplayName("Setters should update all fields correctly")
        void setters_shouldUpdateAllFields() {
            Persona persona = new Persona();
            
            persona.setIdentificacion("0987654321");
            persona.setNombre("Maria Garcia");
            persona.setGenero("F");
            persona.setEdad(25);
            persona.setDireccion("Avenida 456");
            persona.setTelefono("3109876543");
            
            assertThat(persona.getIdentificacion()).isEqualTo("0987654321");
            assertThat(persona.getNombre()).isEqualTo("Maria Garcia");
            assertThat(persona.getGenero()).isEqualTo("F");
            assertThat(persona.getEdad()).isEqualTo(25);
            assertThat(persona.getDireccion()).isEqualTo("Avenida 456");
            assertThat(persona.getTelefono()).isEqualTo("3109876543");
        }
    }
}