package cl.bernardo.ohiggins.ms_kafka_admin.exception;

/** Kafka no respondió o falló por un motivo interno (503 Service Unavailable). */
public class KafkaNoDisponibleException extends RuntimeException {

    public KafkaNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}