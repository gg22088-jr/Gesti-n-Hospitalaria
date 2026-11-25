package com.hospital.gestion.ia;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Elige el proveedor de IA según la configuración (o según la clave). Sin clave, el análisis usa reglas básicas. */
@Configuration
public class IaConfig {

    private static final Logger log = LoggerFactory.getLogger(IaConfig.class);

    @Bean
    public ProveedorIA proveedorIA(@Value("${app.ia.proveedor:}") String proveedor,
                                   @Value("${app.ia.api-key:}") String apiKey,
                                   @Value("${app.ia.modelo:}") String modelo,
                                   @Value("${app.ia.url:}") String url,
                                   @Value("${app.ia.timeout-segundos:30}") int timeoutSegundos) {
        ProveedorIA elegido;
        if ("anthropic".equals(elegirProveedor(proveedor, apiKey))) {
            elegido = new ClienteAnthropic(apiKey, valor(modelo, ClienteAnthropic.MODELO_POR_DEFECTO),
                    valor(url, ClienteAnthropic.URL_POR_DEFECTO), timeoutSegundos);
        } else {
            elegido = new ClienteOpenAI(apiKey, valor(modelo, ClienteOpenAI.MODELO_POR_DEFECTO),
                    valor(url, ClienteOpenAI.URL_POR_DEFECTO), timeoutSegundos);
        }
        if (elegido.disponible()) {
            log.info("IA activa: proveedor={}, modelo={}", elegido.proveedor(), elegido.modelo());
        } else {
            log.info("IA no configurada (falta la variable de entorno IA_API_KEY): el análisis de reportes usará el modo básico");
        }
        return elegido;
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
