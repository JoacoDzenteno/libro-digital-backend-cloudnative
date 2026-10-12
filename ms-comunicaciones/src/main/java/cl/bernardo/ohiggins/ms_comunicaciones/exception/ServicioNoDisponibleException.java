package cl.bernardo.ohiggins.ms_comunicaciones.exception;

/**
 * Error RECUPERABLE: un servicio externo (ms-matricula) no responde.
 * Puede volver a funcionar, así que se reintenta unas veces
 * y, si sigue fallando, el mensaje va a la DLQ.
 */
public class ServicioNoDisponibleException extends RuntimeException {
    public ServicioNoDisponibleException(String message, Throwable cause) {
        super(message, cause);
    }
}