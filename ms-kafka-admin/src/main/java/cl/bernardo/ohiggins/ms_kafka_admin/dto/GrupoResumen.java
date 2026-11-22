package cl.bernardo.ohiggins.ms_kafka_admin.dto;

/** Una fila del listado de consumer groups. estado: STABLE, EMPTY, REBALANCING... */
public record GrupoResumen(String grupo, String estado, int miembros) {
}