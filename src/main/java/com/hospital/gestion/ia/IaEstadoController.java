package com.hospital.gestion.ia;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/** Indica si la IA está configurada, para mostrarlo en Reportes. Nunca devuelve la clave. */
@RestController
@RequestMapping("/api/reportes")
public class IaEstadoController {

    private final ProveedorIA proveedor;

    public IaEstadoController(ProveedorIA proveedor) {
        this.proveedor = proveedor;
    }

    @GetMapping("/ia-estado")
    public Map<String, Object> estado() {
        Map<String, Object> r = new LinkedHashMap<>();
        r.put("activa", proveedor.disponible());
        r.put("proveedor", proveedor.proveedor());
        r.put("modelo", proveedor.modelo());
        return r;
    }
}
