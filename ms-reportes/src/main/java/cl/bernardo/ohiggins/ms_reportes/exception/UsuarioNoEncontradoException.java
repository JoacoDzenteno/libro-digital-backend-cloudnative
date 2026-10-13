package cl.bernardo.ohiggins.ms_reportes.exception;

/**
 * Error NO RECUPERABLE: el estudiante o el usuario generador no existen en ms-usuarios.
 * Reintentar no sirve, porque los datos no van a cambiar solos.
 * El mensaje se rechaza (NACK sin reencolar) y va directo a la DLQ.
 */
public class UsuarioNoEncontradoException extends RuntimeException {
    public UsuarioNoEncontradoException(String message) {
        super(message);
    }
}