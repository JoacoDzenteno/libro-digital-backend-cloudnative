package cl.bernardo.ohiggins.ms_rabbit_admin.model;

/**
 * Tipos de exchange que el administrador permite crear.
 * DIRECT: entrega por routing key exacta.
 * TOPIC: entrega por patrón de routing key (ej: libro.calificacion.*).
 * FANOUT: entrega a todas las colas enlazadas, ignora la routing key.
 */
public enum TipoExchange {
    DIRECT,
    TOPIC,
    FANOUT
}
