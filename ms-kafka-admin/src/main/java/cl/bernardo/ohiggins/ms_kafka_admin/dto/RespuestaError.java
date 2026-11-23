package cl.bernardo.ohiggins.ms_kafka_admin.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Formato común de todas las respuestas de error de la API.
 * Ej: { "fecha": "...", "status": 404, "error": "Recurso no encontrado",
 *       "detalles": ["El tópico 'x' no existe"], "ruta": "/api/kafka/topics/x" }
 */
public record RespuestaError(
        LocalDateTime fecha,
        int status,
        String error,
        List<String> detalles,
        String ruta
) {
}