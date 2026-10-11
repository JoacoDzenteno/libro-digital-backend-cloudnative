package cl.bernardo.ohiggins.ms_comunicaciones.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = MensajeriaYmlTest.Vacio.class)
class MensajeriaYmlTest {

    @Configuration
    static class Vacio {}

    @Value("${mensajeria.exchange}") String exchange;
    @Value("${mensajeria.dlx}") String dlx;
    @Value("${mensajeria.asistencia.cola}") String asistenciaCola;
    @Value("${mensajeria.asistencia.dlq}") String asistenciaDlq;
    @Value("${mensajeria.calificacion-notificacion.routing-key}") String califRk;
    @Value("${mensajeria.hoja-vida.cola}") String hojaVidaCola;

    @Test
    void leeLosNombresDesdeElYml() {
        assertEquals("libro-digital-exchange", exchange);
        assertEquals("libro-digital-dlx", dlx);
        assertEquals("comunicaciones-asistencia-queue", asistenciaCola);
        assertEquals("comunicaciones-asistencia-dlq", asistenciaDlq);
        assertEquals("libro.calificacion.notificacion", califRk);
        assertEquals("comunicaciones-hojavida-queue", hojaVidaCola);
    }
}