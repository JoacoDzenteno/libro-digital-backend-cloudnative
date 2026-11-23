package cl.bernardo.ohiggins.ms_kafka_admin.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Nuevo total de particiones (no cuántas agregar). Ej: { "particiones": 6 }
 * Que sea mayor al actual lo valida el service, porque necesita consultar a Kafka.
 */
public record AumentarParticionesRequest(

        @NotNull(message = "Debe indicar el nuevo total de particiones")
        @Min(value = 2, message = "El nuevo total debe ser al menos 2")
        @Max(value = 100, message = "No puede superar 100 particiones")
        Integer particiones
) {
}