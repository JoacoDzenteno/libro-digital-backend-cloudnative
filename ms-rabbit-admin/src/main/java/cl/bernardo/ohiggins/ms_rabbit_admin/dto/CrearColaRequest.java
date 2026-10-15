package cl.bernardo.ohiggins.ms_rabbit_admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Datos para crear una cola. Ej: { "nombre": "comunicaciones-asistencia-queue", "durable": true } */
public record CrearColaRequest(

        @NotBlank(message = "El nombre de la cola no puede estar vacío")
        @Size(max = ReglasNombre.LARGO_MAXIMO, message = "El nombre de la cola no puede superar 255 caracteres")
        @Pattern(regexp = ReglasNombre.PATRON, message = "El nombre de la cola " + ReglasNombre.MENSAJE)
        String nombre,

        @NotNull(message = "Debe indicar si la cola es durable (true o false)")
        Boolean durable
) {
}