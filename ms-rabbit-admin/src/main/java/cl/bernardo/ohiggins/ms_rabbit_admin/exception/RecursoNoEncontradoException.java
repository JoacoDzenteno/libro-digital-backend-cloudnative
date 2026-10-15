package cl.bernardo.ohiggins.ms_rabbit_admin.exception;

/** Se lanza cuando la cola o exchange solicitado no existe en RabbitMQ. Se traduce a HTTP 404. */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}