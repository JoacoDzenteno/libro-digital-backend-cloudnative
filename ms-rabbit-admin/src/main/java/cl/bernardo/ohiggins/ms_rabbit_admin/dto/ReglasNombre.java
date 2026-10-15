package cl.bernardo.ohiggins.ms_rabbit_admin.dto;

/**
 * Reglas comunes para nombres de colas y exchanges.
 * Se usan como constantes porque las anotaciones de validación
 * solo aceptan valores fijos en tiempo de compilación.
 */
public final class ReglasNombre {

    /** Letras, números, punto, guion y guion bajo; no puede empezar con "amq." (reservado por RabbitMQ). */
    public static final String PATRON = "^(?!amq\\.)[a-zA-Z0-9._-]+$";

    public static final String MENSAJE =
            "solo puede contener letras, números, punto, guion y guion bajo, y no puede empezar con 'amq.'";

    public static final int LARGO_MAXIMO = 255;

    private ReglasNombre() {
        // clase de constantes, no se instancia
    }
}