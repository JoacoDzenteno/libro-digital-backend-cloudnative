package cl.bernardo.ohiggins.ms_rabbit_admin.exception;

/** Se lanza al intentar crear una cola o exchange que ya existe. Se traduce a HTTP 409. */
public class RecursoDuplicadoException extends RuntimeException {
    public RecursoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}