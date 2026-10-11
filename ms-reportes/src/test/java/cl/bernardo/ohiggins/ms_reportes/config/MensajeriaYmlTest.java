package cl.bernardo.ohiggins.ms_reportes.config;

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
    @Value("${mensajeria.calificacion-reporte.routing-key}") String routingKey;
    @Value("${mensajeria.calificacion-reporte.cola}") String cola;
    @Value("${mensajeria.calificacion-reporte.dlq}") String dlq;

    @Test
    void leeLosNombresDesdeElYml() {
        assertEquals("libro-digital-exchange", exchange);
        assertEquals("libro-digital-dlx", dlx);
        assertEquals("libro.calificacion.reporte", routingKey);
        assertEquals("reportes-calificacion-queue", cola);
        assertEquals("reportes-calificacion-dlq", dlq);
    }
}