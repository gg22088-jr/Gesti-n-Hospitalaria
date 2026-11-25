package com.hospital.gestion.ia;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class IaEstadoControllerTest {

    @Test
    void conClaveLaIaApareceActivaSinMostrarLaClave() {
        Map<String, Object> estado = new IaEstadoController(new ClienteOpenAI("sk-secreta", "un-modelo", "http://x", 5)).estado();

        assertEquals(true, estado.get("activa"));
        assertEquals("openai", estado.get("proveedor"));
        assertEquals("un-modelo", estado.get("modelo"));
        assertFalse(estado.toString().contains("sk-secreta"), "la clave nunca debe salir");
    }

    @Test
    void sinClaveLaIaApareceNoConfigurada() {
        Map<String, Object> estado = new IaEstadoController(new ClienteAnthropic("", "m", "http://x", 5)).estado();

        assertEquals(false, estado.get("activa"));
        assertEquals("anthropic", estado.get("proveedor"));
    }
}
