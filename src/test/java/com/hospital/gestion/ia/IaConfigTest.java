package com.hospital.gestion.ia;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IaConfigTest {

    @Test
    void sinIndicarProveedorSeDetectaPorLaClave() {
        assertEquals("anthropic", IaConfig.elegirProveedor("", "sk-ant-api03-abc"));
        assertEquals("openai", IaConfig.elegirProveedor("", "sk-proj-abc"));
        assertEquals("openai", IaConfig.elegirProveedor(null, "sk-abc"));
        assertEquals("openai", IaConfig.elegirProveedor("", ""));
    }

    @Test
    void elProveedorIndicadoManda() {
        assertEquals("anthropic", IaConfig.elegirProveedor("Anthropic", "sk-proj-abc"));
        assertEquals("openai", IaConfig.elegirProveedor(" OpenAI ", "sk-ant-abc"));
    }

    @Test
    void sinClaveElBeanQuedaNoDisponible() {
        ProveedorIA p = new IaConfig().proveedorIA("", "", "", "", 5);
        assertTrue(p instanceof ClienteOpenAI);
        assertEquals(false, p.disponible());
        assertEquals(ClienteOpenAI.MODELO_POR_DEFECTO, p.modelo());
    }

    @Test
    void conClaveDeAnthropicSeUsaAnthropic() {
        ProveedorIA p = new IaConfig().proveedorIA("", "sk-ant-abc", "", "", 5);
        assertTrue(p instanceof ClienteAnthropic);
        assertEquals(ClienteAnthropic.MODELO_POR_DEFECTO, p.modelo());
    }
}
