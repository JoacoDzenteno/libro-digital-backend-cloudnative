package cl.bernardo.ohiggins.ms_libro_digital.service;

import cl.bernardo.ohiggins.ms_libro_digital.dto.NotificacionEvent;
import cl.bernardo.ohiggins.ms_libro_digital.dto.ReporteEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class NotificacionPublisher {

    private final RabbitTemplate rabbitTemplate;

    // Leemos el exchange y las routing keys desde el application.yml
    @Value("${mensajeria.exchange}")
    private String exchange;

    @Value("${mensajeria.asistencia.routing-key}")
    private String asistenciaRoutingKey;

    @Value("${mensajeria.calificacion-notificacion.routing-key}")
    private String calificacionNotificacionRoutingKey;

    @Value("${mensajeria.calificacion-reporte.routing-key}")
    private String calificacionReporteRoutingKey;

    @Value("${mensajeria.hoja-vida.routing-key}")
    private String hojaVidaRoutingKey;

    public NotificacionPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publicarAsistencia(NotificacionEvent event) {
        enriquecerNotificacion(event);
        rabbitTemplate.convertAndSend(exchange, asistenciaRoutingKey, event);
    }

    public void publicarCalificacion(NotificacionEvent event) {
        enriquecerNotificacion(event);
        rabbitTemplate.convertAndSend(exchange, calificacionNotificacionRoutingKey, event);
    }

    public void publicarHojaVida(NotificacionEvent event) {
        enriquecerNotificacion(event);
        rabbitTemplate.convertAndSend(exchange, hojaVidaRoutingKey, event);
    }

    public void publicarReporteCalificacion(ReporteEvent event) {
        event.setEventoId(UUID.randomUUID());
        event.setFecha(LocalDateTime.now());
        rabbitTemplate.convertAndSend(exchange, calificacionReporteRoutingKey, event);
    }

    // Método auxiliar para no repetir código
    private void enriquecerNotificacion(NotificacionEvent event) {
        event.setEventoId(UUID.randomUUID());
        event.setFecha(LocalDateTime.now());
    }
}