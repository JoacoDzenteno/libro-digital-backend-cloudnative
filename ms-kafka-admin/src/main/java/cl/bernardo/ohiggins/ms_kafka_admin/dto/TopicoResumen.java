package cl.bernardo.ohiggins.ms_kafka_admin.dto;

/** Una fila del listado de tópicos. */
public record TopicoResumen(String nombre, int particiones, int replicas) {
}