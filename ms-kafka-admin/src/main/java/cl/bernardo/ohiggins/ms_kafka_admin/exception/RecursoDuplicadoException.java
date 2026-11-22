package cl.bernardo.ohiggins.ms_kafka_admin.exception;

/** Se quiere crear algo que ya existe en Kafka (409 Conflict). */
public class RecursoDuplicadoException extends RuntimeException {

    public RecursoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}