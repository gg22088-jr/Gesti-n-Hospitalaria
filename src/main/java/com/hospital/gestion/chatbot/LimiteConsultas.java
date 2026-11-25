package com.hospital.gestion.chatbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Máximo de consultas a la IA por usuario en una ventana de tiempo (por defecto 20 por hora), para controlar el gasto. */
@Component
public class LimiteConsultas {

    private final int maximo;
    private final Duration ventana;
    private final Map<Integer, Deque<Instant>> registros = new ConcurrentHashMap<>();

    @Autowired
    public LimiteConsultas(@Value("${app.ia.chatbot-max-por-hora:20}") int maximo) {
        this(maximo, Duration.ofHours(1));
    }

    LimiteConsultas(int maximo, Duration ventana) {
        this.maximo = maximo;
        this.ventana = ventana;
    }

    /** Registra una consulta y devuelve true si todavía está dentro del límite. */
    public boolean permitir(Integer idUsuario) {
        return permitir(idUsuario, Instant.now());
    }

    boolean permitir(Integer idUsuario, Instant ahora) {
        Deque<Instant> consultas = registros.computeIfAbsent(idUsuario == null ? -1 : idUsuario, k -> new ArrayDeque<>());
        synchronized (consultas) {
            while (!consultas.isEmpty() && consultas.peekFirst().isBefore(ahora.minus(ventana))) consultas.pollFirst();
            if (consultas.size() >= maximo) return false;
            consultas.addLast(ahora);
            return true;
        }
    }
}
