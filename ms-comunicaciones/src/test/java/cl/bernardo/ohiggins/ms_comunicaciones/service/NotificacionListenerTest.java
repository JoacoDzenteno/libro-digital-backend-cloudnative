package cl.bernardo.ohiggins.ms_comunicaciones.service;

import cl.bernardo.ohiggins.ms_comunicaciones.dto.NotificacionEvent;
import cl.bernardo.ohiggins.ms_comunicaciones.model.Mensaje;
import cl.bernardo.ohiggins.ms_comunicaciones.repository.MensajeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacionListenerTest {

    @Mock
    private MatriculaClientService matriculaClientService;

    @Mock
    private MensajeRepository mensajeRepository;

    @InjectMocks
    private NotificacionListener notificacionListener;

    @Test
    void recibirNotificacion_sinApoderado_noGuardaMensaje() {
        NotificacionEvent evento = NotificacionEvent.builder()
                .idEstudiante(2L)
                .asunto("Nueva calificación")
                .contenido("Se registró una nota")
                .tipoEvento("CALIFICACION")
                .build();

        when(matriculaClientService.obtenerIdApoderado(2L)).thenReturn(null);

        notificacionListener.recibirNotificacion(evento);

        verify(mensajeRepository, never()).save(any());
    }

    @Test
    void recibirNotificacion_conApoderado_guardaMensajeCorrecto() {
        NotificacionEvent evento = NotificacionEvent.builder()
                .idEstudiante(2L)
                .asunto("Nueva calificación")
                .contenido("Se registró una nota")
                .tipoEvento("CALIFICACION")
                .build();

        when(matriculaClientService.obtenerIdApoderado(2L)).thenReturn(3L);

        notificacionListener.recibirNotificacion(evento);

        ArgumentCaptor<Mensaje> captor = ArgumentCaptor.forClass(Mensaje.class);
        verify(mensajeRepository).save(captor.capture());

        Mensaje guardado = captor.getValue();
        assertEquals(2L, guardado.getIdRemitente());
        assertEquals(3L, guardado.getIdDestinatario());
        assertEquals("Nueva calificación", guardado.getAsunto());
        assertEquals(Mensaje.TipoMensaje.NOTIFICACION, guardado.getTipo());
        assertFalse(guardado.getLeido());
    }
}