package com.hospital.gestion.ia;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Elige el proveedor de IA según la configuración (o según la clave). Sin clave, el análisis usa reglas básicas. */
@Configuration
public class IaConfig {

    @Bean
    public ProveedorIA proveedorIA(@Value("${app.ia.proveedor:}") String proveedor,
                                   @Value("${app.ia.api-key:}") String apiKey,
                                   @Value("${app.ia.modelo:}") String modelo,
                                   @Value("${app.ia.url:}") String url,
                                   @Value("${app.ia.timeout-segundos:30}") int timeoutSegundos) {
        if ("anthropic".equals(elegirProveedor(proveedor, apiKey))) {
            return new ClienteAnthropic(apiKey, valor(modelo, ClienteAnthropic.MODELO_POR_DEFECTO),
                    valor(url, ClienteAnthropic.URL_POR_DEFECTO), timeoutSegundos);
        }
        return new ClienteOpenAI(apiKey, valor(modelo, ClienteOpenAI.MODELO_POR_DEFECTO),
                valor(url, ClienteOpenAI.URL_POR_DEFECTO), timeoutSegundos);
    }

    /** Lo indicado en la configuración; si no, por el formato de la clave (sk-ant-... es de Anthropic) y, por defecto, OpenAI. */
    static String elegirProveedor(String proveedor, String apiKey) {
        if (proveedor != null && !proveedor.isBlank()) return proveedor.trim().toLowerCase();
        return apiKey != null && apiKey.trim().startsWith("sk-ant-") ? "anthropic" : "openai";
    }

    private static String valor(String valor, String porDefecto) {
        return valor == null || valor.isBlank() ? porDefecto : valor.trim();
    }
}
