package cl.bernardo.ohiggins.ms_reportes.service;

import cl.bernardo.ohiggins.ms_reportes.dto.ReporteEvent;
import cl.bernardo.ohiggins.ms_reportes.model.Reporte;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReporteEventListenerTest {

    @Mock
    private ReporteService reporteService;

    @InjectMocks
    private ReporteEventListener reporteEventListener;

    @Test
    void recibirEventoCalificacion_construyeReporteYLlamaGenerar() {
        ReporteEvent evento = ReporteEvent.builder()
                .idEstudiante(2L)
                .idGeneradoPor(1L)
                .contenido("Calificación 6.5 registrada")
                .build();

        when(reporteService.generar(any(Reporte.class))).thenReturn(Optional.empty());

        reporteEventListener.recibirEventoCalificacion(evento);

        ArgumentCaptor<Reporte> captor = ArgumentCaptor.forClass(Reporte.class);
        verify(reporteService).generar(captor.capture());

        Reporte construido = captor.getValue();
        assertEquals(2L, construido.getIdReferencia());
        assertEquals(1L, construido.getIdGeneradoPor());
        assertEquals(Reporte.TipoReporte.CALIFICACIONES, construido.getTipo());
        assertEquals("Calificación 6.5 registrada", construido.getContenido());
    }
}