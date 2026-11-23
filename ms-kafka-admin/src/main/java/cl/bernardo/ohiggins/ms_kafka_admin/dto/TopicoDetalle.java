package cl.bernardo.ohiggins.ms_kafka_admin.dto;

import java.util.List;
import java.util.Map;

/**
 * Detalle de un tópico: sus parámetros propios y cómo quedó repartida cada partición.
 * configuracion solo trae lo que se definió para este tópico (retention.ms, cleanup.policy...),
 * no los cientos de valores por defecto del broker.
 */
public record TopicoDetalle(
        String nombre,
        int particiones,
        int replicas,
        Map<String, String> configuracion,
        List<ParticionInfo> detalleParticiones
) {

    /** lider: broker que manda en la partición. isr: copias que están al día. */
    public record ParticionInfo(int particion, int lider, List<Integer> replicas, List<Integer> isr) {
    }
}