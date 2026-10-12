package cl.bernardo.ohiggins.ms_comunicaciones.exception;

/**
 * Error NO RECUPERABLE: el estudiante no tiene apoderado asociado.
 * Reintentar no sirve, porque los datos no van a cambiar solos.
 * El mensaje se rechaza (NACK sin reencolar) y va directo a la DLQ.
 */
public class ApoderadoNoEncontradoException extends RuntimeException {
    public ApoderadoNoEncontradoException(String message) {
        super(message);
    }
}