package cl.bernardo.ohiggins.ms_libro_digital.service;

import cl.bernardo.ohiggins.ms_libro_digital.dto.NotificacionEvent;
import cl.bernardo.ohiggins.ms_libro_digital.dto.ReporteEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificacionPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private NotificacionPublisher notificacionPublisher;

    // Configuramos los valores que normalmente vendrían del application.yml
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(notificacionPublisher, "exchange", "libro-digital-exchange");
        ReflectionTestUtils.setField(notificacionPublisher, "asistenciaRoutingKey", "libro.asistencia.registrada");
        ReflectionTestUtils.setField(notificacionPublisher, "calificacionNotificacionRoutingKey", "libro.calificacion.notificacion");
        ReflectionTestUtils.setField(notificacionPublisher, "calificacionReporteRoutingKey", "libro.calificacion.reporte");
        ReflectionTestUtils.setField(notificacionPublisher, "hojaVidaRoutingKey", "libro.hojavida.registrada");
    }

    @Test
    void publicarAsistencia_enviaAlExchangeCorrecto() {
        NotificacionEvent evento = NotificacionEvent.builder()
                .idEstudiante(2L)
                .asunto("Test")
                .contenido("Contenido test")
                .tipoEvento("ASISTENCIA")
                .build();

        notificacionPublisher.publicarAsistencia(evento);

        verify(rabbitTemplate).convertAndSend(
                eq("libro-digital-exchange"),
                eq("libro.asistencia.registrada"),
                any(NotificacionEvent.class)
        );
    }

    @Test
    void publicarCalificacion_enviaAlExchangeCorrecto() {
        NotificacionEvent evento = NotificacionEvent.builder()
                .idEstudiante(2L)
                .asunto("Test")
                .contenido("Contenido test")
                .tipoEvento("CALIFICACION")
                .build();

        notificacionPublisher.publicarCalificacion(evento);

        verify(rabbitTemplate).convertAndSend(
                eq("libro-digital-exchange"),
                eq("libro.calificacion.notificacion"),
                any(NotificacionEvent.class)
        );
    }
    
    @Test
    void publicarReporteCalificacion_enviaAlExchangeCorrecto() {
        ReporteEvent evento = ReporteEvent.builder()
                .idEstudiante(2L)
                .idCurso(1L)
                .tipoReporte("CALIFICACION")
                .contenido("Contenido test")
                .build();

        notificacionPublisher.publicarReporteCalificacion(evento);

        verify(rabbitTemplate).convertAndSend(
                eq("libro-digital-exchange"),
                eq("libro.calificacion.reporte"),
                any(ReporteEvent.class)
        );
    }
}