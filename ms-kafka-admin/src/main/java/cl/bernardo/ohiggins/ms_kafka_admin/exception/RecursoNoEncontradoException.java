package cl.bernardo.ohiggins.ms_kafka_admin.exception;

/** Se pide un tópico o consumer group que no existe (404 Not Found). */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}