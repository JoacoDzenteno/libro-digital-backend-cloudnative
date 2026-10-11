package cl.bernardo.ohiggins.ms_comunicaciones.config;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Queue;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RabbitMQConfigTest {

    private RabbitMQConfig config() {
        RabbitMQConfig c = new RabbitMQConfig();
        ReflectionTestUtils.setField(c, "exchange", "libro-digital-exchange");
        ReflectionTestUtils.setField(c, "dlx", "libro-digital-dlx");
        ReflectionTestUtils.setField(c, "asistenciaRk", "libro.asistencia.registrada");
        ReflectionTestUtils.setField(c, "asistenciaCola", "comunicaciones-asistencia-queue");
        ReflectionTestUtils.setField(c, "asistenciaDlq", "comunicaciones-asistencia-dlq");
        ReflectionTestUtils.setField(c, "calificacionRk", "libro.calificacion.notificacion");
        ReflectionTestUtils.setField(c, "calificacionCola", "comunicaciones-calificacion-queue");
        ReflectionTestUtils.setField(c, "calificacionDlq", "comunicaciones-calificacion-dlq");
        ReflectionTestUtils.setField(c, "hojaVidaRk", "libro.hojavida.registrada");
        ReflectionTestUtils.setField(c, "hojaVidaCola", "comunicaciones-hojavida-queue");
        ReflectionTestUtils.setField(c, "hojaVidaDlq", "comunicaciones-hojavida-dlq");
        return c;
    }

    private void verificar(Queue q, String nombre, String dlq) {
        assertEquals(nombre, q.getName());
        assertTrue(q.isDurable());
        assertEquals("libro-digital-dlx", q.getArguments().get("x-dead-letter-exchange"));
        assertEquals(dlq, q.getArguments().get("x-dead-letter-routing-key"));
    }

    @Test
    void cadaColaApuntaASuDlq() {
        RabbitMQConfig c = config();
        verificar(c.asistenciaQueue(), "comunicaciones-asistencia-queue", "comunicaciones-asistencia-dlq");
        verificar(c.calificacionQueue(), "comunicaciones-calificacion-queue", "comunicaciones-calificacion-dlq");
        verificar(c.hojaVidaQueue(), "comunicaciones-hojavida-queue", "comunicaciones-hojavida-dlq");
    }
}