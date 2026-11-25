package com.hospital.gestion.chatbot;

import com.hospital.gestion.ia.ProveedorIA;
import com.hospital.gestion.security.ContextoActual;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Asistente de SIGPMAT. Abierto a cualquier usuario con sesión; las respuestas respetan su rol y su alcance. */
@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    public record Mensaje(@NotBlank @Size(max = 300, message = "El mensaje no puede pasar de 300 caracteres") String mensaje) { }

    private final ChatbotService servicio;
    private final ContextoActual contexto;
    private final ProveedorIA proveedor;

    public ChatbotController(ChatbotService servicio, ContextoActual contexto, ProveedorIA proveedor) {
        this.servicio = servicio;
        this.contexto = contexto;
        this.proveedor = proveedor;
    }

    /** Indica si el chat tiene IA disponible (no devuelve la clave). */
    @GetMapping("/estado")
    public java.util.Map<String, Object> estado() {
        return java.util.Map.of("ia", proveedor.disponible());
    }

    @PostMapping("/preguntar")
    public ChatbotService.Respuesta preguntar(@Valid @RequestBody Mensaje m) {
        return servicio.responder(contexto.obtener(), m.mensaje());
    }
}
