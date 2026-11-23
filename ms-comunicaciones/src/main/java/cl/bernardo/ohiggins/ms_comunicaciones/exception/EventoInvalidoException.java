package cl.bernardo.ohiggins.ms_comunicaciones.exception;

/**
 * El evento llegó con datos faltantes o fuera de rango.
 * NO RECUPERABLE: reintentar no cambia el contenido del mensaje, así que va directo al DLT.
 */
public class EventoInvalidoException extends RuntimeException {

    public EventoInvalidoException(String mensaje) {
        super(mensaje);
    }
}