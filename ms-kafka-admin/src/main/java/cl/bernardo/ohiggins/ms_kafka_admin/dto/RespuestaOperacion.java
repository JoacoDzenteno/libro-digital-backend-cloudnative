package cl.bernardo.ohiggins.ms_kafka_admin.dto;

/** Respuesta de las operaciones que no devuelven datos. Ej: { "mensaje": "Tópico 'x' creado correctamente" } */
public record RespuestaOperacion(String mensaje) {
}