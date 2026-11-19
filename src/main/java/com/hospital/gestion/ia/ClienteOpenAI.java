package com.hospital.gestion.ia;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Cliente de la API Chat Completions de OpenAI (POST /v1/chat/completions con Authorization: Bearer).
 * La clave se lee de la variable de entorno IA_API_KEY; nunca se envía a la página.
 */
public class ClienteOpenAI implements ProveedorIA {

    public static final String MODELO_POR_DEFECTO = "gpt-4o-mini";
    public static final String URL_POR_DEFECTO = "https://api.openai.com/v1/chat/completions";
    private static final int MAX_TOKENS = 1200;

    private final RestClient http;
    private final String apiKey;
    private final String modelo;
    private final String url;

    public ClienteOpenAI(String apiKey, String modelo, String url, int timeoutSegundos) {
        this.apiKey = apiKey == null ? "" : apiKey.trim();
        this.modelo = modelo;
        this.url = url;
        SimpleClientHttpRequestFactory fabrica = new SimpleClientHttpRequestFactory();
        fabrica.setConnectTimeout(Duration.ofSeconds(10));
        fabrica.setReadTimeout(Duration.ofSeconds(timeoutSegundos));
        this.http = RestClient.builder().requestFactory(fabrica).build();
    }

    @Override
    public boolean disponible() {
        return !apiKey.isBlank();
    }

    @Override
    public String modelo() {
        return modelo;
    }

    @Override
    public String completar(String sistema, String usuario) {
        JsonNode respuesta = http.post()
                .uri(url)
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(construirCuerpo(modelo, sistema, usuario))
                .retrieve()
                .body(JsonNode.class);
        return extraerTexto(respuesta);
    }

    static Map<String, Object> construirCuerpo(String modelo, String sistema, String usuario) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("model", modelo);
        cuerpo.put("max_completion_tokens", MAX_TOKENS);
        cuerpo.put("messages", List.of(
                Map.of("role", "system", "content", sistema),
                Map.of("role", "user", "content", usuario)));
        return cuerpo;
    }

    /** El texto está en choices[0].message.content. */
    static String extraerTexto(JsonNode respuesta) {
        if (respuesta == null || !respuesta.path("choices").isArray() || respuesta.path("choices").isEmpty()) {
            throw new IllegalStateException("Respuesta inesperada del servicio de IA");
        }
        String texto = respuesta.path("choices").get(0).path("message").path("content").asText("").trim();
        if (texto.isEmpty()) throw new IllegalStateException("El servicio de IA no devolvió texto");
        return texto;
    }
}
