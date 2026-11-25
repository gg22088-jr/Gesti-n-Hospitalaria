package com.hospital.gestion.chatbot;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LimiteConsultasTest {

    private static final Instant T0 = Instant.parse("2026-11-20T10:00:00Z");

    @Test
    void permiteHastaElMaximoYLuegoBloquea() {
        LimiteConsultas limite = new LimiteConsultas(2, Duration.ofHours(1));

        assertTrue(limite.permitir(1, T0));
        assertTrue(limite.permitir(1, T0.plusSeconds(10)));
        assertFalse(limite.permitir(1, T0.plusSeconds(20)));
    }

    @Test
    void pasadaLaVentanaVuelveAPermitir() {
        LimiteConsultas limite = new LimiteConsultas(1, Duration.ofHours(1));

        assertTrue(limite.permitir(1, T0));
        assertFalse(limite.permitir(1, T0.plusSeconds(30 * 60)));
        assertTrue(limite.permitir(1, T0.plusSeconds(61 * 60)));
    }

    @Test
    void cadaUsuarioTieneSuPropioContador() {
        LimiteConsultas limite = new LimiteConsultas(1, Duration.ofHours(1));

        assertTrue(limite.permitir(1, T0));
        assertFalse(limite.permitir(1, T0));
        assertTrue(limite.permitir(2, T0));
    }
}
