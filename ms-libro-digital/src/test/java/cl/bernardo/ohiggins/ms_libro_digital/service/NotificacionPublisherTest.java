package cl.bernardo.ohiggins.ms_libro_digital.service;

import cl.bernardo.ohiggins.ms_libro_digital.config.RabbitMQConfig;
import cl.bernardo.ohiggins.ms_libro_digital.dto.NotificacionEvent;
import cl.bernardo.ohiggins.ms_libro_digital.dto.ReporteEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacionPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private NotificacionPublisher notificacionPublisher;

    @Test
    void publicarNotificacion_enviaAlExchangeCorrecto() {
        NotificacionEvent evento = NotificacionEvent.builder()
                .idEstudiante(2L)
                .asunto("Test")
                .contenido("Contenido test")
                .tipoEvento("CALIFICACION")
                .build();

        notificacionPublisher.publicarNotificacion(evento);

        verify(rabbitTemplate).convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY_NOTIFICACION,
                evento
        );
    }

    @Test
    void publicarReporte_enviaAlExchangeCorrecto() {
        ReporteEvent evento = ReporteEvent.builder()
                .idEstudiante(2L)
                .idGeneradoPor(1L)
                .contenido("Contenido test")
                .build();

        notificacionPublisher.publicarReporte(evento);

        verify(rabbitTemplate).convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY_REPORTE,
                evento
        );
    }
}