package cl.bernardo.ohiggins.ms_kafka_admin.dto;

/**
 * Reglas para nombres de tópicos, las mismas que exige Kafka.
 * Son constantes porque las anotaciones de validación solo aceptan valores fijos.
 */
public final class ReglasNombreTopico {

    /** Letras, números, punto, guion y guion bajo; no puede empezar con "__" ni ser "." o "..". */
    public static final String PATRON = "^(?!__)(?!\\.{1,2}$)[a-zA-Z0-9._-]+$";

    public static final String MENSAJE =
            "solo puede contener letras, números, punto, guion y guion bajo, "
                    + "no puede empezar con '__' (reservado por Kafka) ni ser '.' o '..'";

    public static final int LARGO_MAXIMO = 249;

    private ReglasNombreTopico() {
        // clase de constantes, no se instancia
    }
}