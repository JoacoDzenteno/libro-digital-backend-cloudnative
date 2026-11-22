package cl.bernardo.ohiggins.ms_kafka_admin.dto;

import java.util.List;

/**
 * Detalle de un consumer group: qué consumidor lee cada partición
 * y cuántos mensajes le faltan por leer (lag).
 */
public record GrupoDetalle(
        String grupo,
        String estado,
        List<MiembroInfo> miembros,
        List<OffsetInfo> offsets,
        long lagTotal
) {

    /** particionesAsignadas: ej. "libro.asistencia.eventos-3" (tópico-partición). */
    public record MiembroInfo(String clientId, String host, List<String> particionesAsignadas) {
    }

    /** offsetConsumido: hasta dónde leyó el grupo. offsetFinal: último mensaje existente. */
    public record OffsetInfo(String topico, int particion, long offsetConsumido, long offsetFinal, long lag) {
    }
}