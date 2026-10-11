package cl.bernardo.ohiggins.ms_reportes.config;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Queue;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RabbitMQConfigTest {

    @Test
    void laColaApuntaASuDlq() {
        RabbitMQConfig c = new RabbitMQConfig();
        ReflectionTestUtils.setField(c, "exchange", "libro-digital-exchange");
        ReflectionTestUtils.setField(c, "dlx", "libro-digital-dlx");
        ReflectionTestUtils.setField(c, "reporteRk", "libro.calificacion.reporte");
        ReflectionTestUtils.setField(c, "reporteCola", "reportes-calificacion-queue");
        ReflectionTestUtils.setField(c, "reporteDlq", "reportes-calificacion-dlq");

        Queue q = c.reporteQueue();

        assertEquals("reportes-calificacion-queue", q.getName());
        assertTrue(q.isDurable());
        assertEquals("libro-digital-dlx", q.getArguments().get("x-dead-letter-exchange"));
        assertEquals("reportes-calificacion-dlq", q.getArguments().get("x-dead-letter-routing-key"));
    }
}