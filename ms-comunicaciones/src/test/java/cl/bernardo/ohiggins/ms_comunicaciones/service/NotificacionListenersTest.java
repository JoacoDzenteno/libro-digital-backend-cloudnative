package cl.bernardo.ohiggins.ms_comunicaciones.service;

import cl.bernardo.ohiggins.ms_comunicaciones.dto.NotificacionEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificacionListenersTest {

    @Mock
    private NotificacionProcesador procesador;

    private NotificacionEvent evento() {
        return NotificacionEvent.builder()
                .idEstudiante(2L)
                .asunto("Asunto")
                .contenido("Contenido")
                .build();
    }

    @Test
    void asistenciaListener_delegaAlProcesador() {
        NotificacionEvent e = evento();
        new AsistenciaListener(procesador).recibir(e);
        verify(procesador).procesar(e);
    }

    @Test
    void calificacionListener_delegaAlProcesador() {
        NotificacionEvent e = evento();
        new CalificacionListener(procesador).recibir(e);
        verify(procesador).procesar(e);
    }

    @Test
    void hojaVidaListener_delegaAlProcesador() {
        NotificacionEvent e = evento();
        new HojaVidaListener(procesador).recibir(e);
        verify(procesador).procesar(e);
    }
}