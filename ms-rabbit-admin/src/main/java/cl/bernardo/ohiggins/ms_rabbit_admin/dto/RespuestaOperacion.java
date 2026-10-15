package cl.bernardo.ohiggins.ms_rabbit_admin.dto;

/** Respuesta de una operación exitosa. Ej: { "mensaje": "Cola 'x' creada correctamente" } */
public record RespuestaOperacion(String mensaje) {
}
