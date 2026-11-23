package cl.bernardo.ohiggins.ms_kafka_admin.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Map;

/**
 * Datos para crear un tópico. Ej:
 * { "nombre": "topico-prueba", "particiones": 3, "replicas": 3,
 *   "configuracion": { "retention.ms": "86400000", "cleanup.policy": "delete" } }
 * configuracion es opcional: si no se envía, el tópico usa los valores por defecto del broker.
 */
public record CrearTopicoRequest(

        @NotBlank(message = "El nombre del tópico no puede estar vacío")
        @Size(max = ReglasNombreTopico.LARGO_MAXIMO, message = "El nombre del tópico no puede superar 249 caracteres")
        @Pattern(regexp = ReglasNombreTopico.PATRON, message = "El nombre del tópico " + ReglasNombreTopico.MENSAJE)
        String nombre,

        @NotNull(message = "Debe indicar el número de particiones")
        @Min(value = 1, message = "Debe tener al menos 1 partición")
        @Max(value = 100, message = "No puede superar 100 particiones")
        Integer particiones,

        // No se fija un máximo aquí: si se piden más réplicas que brokers, Kafka lo rechaza (400)
        @NotNull(message = "Debe indicar el factor de replicación")
        @Min(value = 1, message = "Debe tener al menos 1 réplica")
        Integer replicas,

        Map<@NotBlank String, @NotBlank String> configuracion
) {
}