package cl.bernardo.ohiggins.ms_rabbit_admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Datos de un binding (regla que une un exchange con una cola).
 * Ej: { "exchange": "libro-digital-exchange", "cola": "comunicaciones-asistencia-queue",
 *       "routingKey": "libro.asistencia.registrada" }
 */
public record BindingRequest(

        @NotBlank(message = "El nombre del exchange no puede estar vacío")
        @Pattern(regexp = ReglasNombre.PATRON, message = "El nombre del exchange " + ReglasNombre.MENSAJE)
        String exchange,

        @NotBlank(message = "El nombre de la cola no puede estar vacío")
        @Pattern(regexp = ReglasNombre.PATRON, message = "El nombre de la cola " + ReglasNombre.MENSAJE)
        String cola,

        // Puede ir vacío (los exchanges FANOUT ignoran la routing key), pero no null.
        // Admite * y # porque son los comodines de los exchanges TOPIC.
        @NotNull(message = "Debe indicar la routing key (puede ser vacía para exchanges FANOUT)")
        @Size(max = ReglasNombre.LARGO_MAXIMO, message = "La routing key no puede superar 255 caracteres")
        @Pattern(regexp = "^[a-zA-Z0-9._*#-]*$",
                message = "La routing key solo puede contener letras, números, punto, guion, guion bajo, * y #")
        String routingKey
) {
}