package cl.bernardo.ohiggins.ms_comunicaciones.service;

import cl.bernardo.ohiggins.ms_comunicaciones.dto.NotificacionEvent;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificacionListenersTest {

    @Mock
    private NotificacionProcesador procesador;

    @Mock
    private Channel channel;

    private final ConfirmadorMensajes confirmador = new ConfirmadorMensajes();

    private NotificacionEvent evento() {
        return NotificacionEvent.builder()
                .idEstudiante(2L)
                .asunto("Asunto")
                .contenido("Contenido")
                .build();
    }

    @Test
    void asistenciaListener_procesaYConfirmaConAck() throws IOException {
        NotificacionEvent e = evento();
        new AsistenciaListener(procesador, confirmador).recibir(e, channel, 1L);
        verify(procesador).procesar(e);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void calificacionListener_procesaYConfirmaConAck() throws IOException {
        NotificacionEvent e = evento();
        new CalificacionListener(procesador, confirmador).recibir(e, channel, 2L);
        verify(procesador).procesar(e);
        verify(channel).basicAck(2L, false);
    }

    @Test
    void hojaVidaListener_procesaYConfirmaConAck() throws IOException {
        NotificacionEvent e = evento();
        new HojaVidaListener(procesador, confirmador).recibir(e, channel, 3L);
        verify(procesador).procesar(e);
        verify(channel).basicAck(3L, false);
    }
}