package cl.bernardo.ohiggins.ms_rabbit_admin.dto;

import cl.bernardo.ohiggins.ms_rabbit_admin.model.TipoExchange;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Datos para crear un exchange. Ej: { "nombre": "libro-digital-exchange", "tipo": "TOPIC", "durable": true } */
public record CrearExchangeRequest(

        @NotBlank(message = "El nombre del exchange no puede estar vacío")
        @Size(max = ReglasNombre.LARGO_MAXIMO, message = "El nombre del exchange no puede superar 255 caracteres")
        @Pattern(regexp = ReglasNombre.PATRON, message = "El nombre del exchange " + ReglasNombre.MENSAJE)
        String nombre,

        @NotNull(message = "Debe indicar el tipo de exchange: DIRECT, TOPIC o FANOUT")
        TipoExchange tipo,

        @NotNull(message = "Debe indicar si el exchange es durable (true o false)")
        Boolean durable
) {
}