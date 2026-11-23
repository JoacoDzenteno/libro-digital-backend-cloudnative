package cl.bernardo.ohiggins.ms_kafka_admin.exception;

/** Kafka rechaza la operación por los datos enviados, por ejemplo más réplicas que brokers (400 Bad Request). */
public class OperacionInvalidaException extends RuntimeException {

    public OperacionInvalidaException(String mensaje) {
        super(mensaje);
    }
}