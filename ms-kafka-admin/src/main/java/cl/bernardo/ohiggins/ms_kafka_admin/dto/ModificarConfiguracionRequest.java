package cl.bernardo.ohiggins.ms_kafka_admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Map;

/**
 * Parámetros a cambiar en un tópico; los que no se envían quedan igual. Ej:
 * { "configuracion": { "retention.ms": "604800000" } }
 */
public record ModificarConfiguracionRequest(

        @NotEmpty(message = "Debe enviar al menos un parámetro a modificar")
        Map<@NotBlank String, @NotBlank String> configuracion
) {
}