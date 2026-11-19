package com.hospital.gestion.ia;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClienteOpenAITest {

    private final ObjectMapper json = new ObjectMapper();

    @Test
    void elCuerpoTieneElFormatoDeChatCompletions() {
        Map<String, Object> cuerpo = ClienteOpenAI.construirCuerpo("un-modelo", "instrucciones", "datos");

        assertEquals("un-modelo", cuerpo.get("model"));
        assertTrue(((Number) cuerpo.get("max_completion_tokens")).intValue() > 0);
        List<?> mensajes = (List<?>) cuerpo.get("messages");
        assertEquals(2, mensajes.size());
        Map<?, ?> sistema = (Map<?, ?>) mensajes.get(0);
        Map<?, ?> usuario = (Map<?, ?>) mensajes.get(1);
        assertEquals("system", sistema.get("role"));
        assertEquals("instrucciones", sistema.get("content"));
        assertEquals("user", usuario.get("role"));
        assertEquals("datos", usuario.get("content"));
    }

    @Test
    void elTextoSaleDeChoicesMessageContent() throws Exception {
        String respuesta = "{\"choices\":[{\"index\":0,\"message\":{\"role\":\"assistant\",\"content\":\"  Hola mundo  \"}}]}";
        assertEquals("Hola mundo", ClienteOpenAI.extraerTexto(json.readTree(respuesta)));
    }

    @Test
    void unaRespuestaSinTextoSeRechaza() throws Exception {
        assertThrows(IllegalStateException.class, () -> ClienteOpenAI.extraerTexto(json.readTree("{\"error\":{\"message\":\"x\"}}")));
        assertThrows(IllegalStateException.class, () -> ClienteOpenAI.extraerTexto(json.readTree("{\"choices\":[]}")));
        assertThrows(IllegalStateException.class, () -> ClienteOpenAI.extraerTexto(json.readTree("{\"choices\":[{\"message\":{\"content\":null}}]}")));
        assertThrows(IllegalStateException.class, () -> ClienteOpenAI.extraerTexto(null));
    }

    @Test
    void sinClaveNoEstaDisponible() {
        assertFalse(new ClienteOpenAI("", "m", "http://localhost", 5).disponible());
        assertTrue(new ClienteOpenAI("sk-prueba", "m", "http://localhost", 5).disponible());
    }
}
