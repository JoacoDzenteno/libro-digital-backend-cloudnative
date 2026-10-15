package cl.bernardo.ohiggins.ms_rabbit_admin.dto;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Formato común de todas las respuestas de error de la API.
 * Ej: { "fecha": "...", "status": 400, "error": "Datos inválidos",
 *       "detalles": ["nombre: El nombre de la cola no puede estar vacío"], "ruta": "/api/rabbit/queues" }
 */
public record RespuestaError(
        LocalDateTime fecha,
        int status,
        String error,
        List<String> detalles,
        String ruta
) {
}
