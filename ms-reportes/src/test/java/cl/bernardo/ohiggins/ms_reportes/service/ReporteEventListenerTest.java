package cl.bernardo.ohiggins.ms_reportes.service;

import cl.bernardo.ohiggins.ms_reportes.dto.ReporteEvent;
import cl.bernardo.ohiggins.ms_reportes.exception.ServicioNoDisponibleException;
import cl.bernardo.ohiggins.ms_reportes.model.Reporte;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReporteEventListenerTest {

    @Mock
    private ReporteService reporteService;

    @Mock
    private Channel channel;

    private ReporteEventListener listener;

    @BeforeEach
    void setUp() {
        ConfirmadorMensajes confirmador = new ConfirmadorMensajes();
        ReflectionTestUtils.setField(confirmador, "esperaMs", 0L); // sin esperas en los tests
        listener = new ReporteEventListener(reporteService, confirmador);
    }

    private ReporteEvent evento() {
        return ReporteEvent.builder()
                .eventoId(UUID.randomUUID())
                .fecha(LocalDateTime.now())
                .idEstudiante(2L)
                .idCurso(10L)
                .tipoReporte("CALIFICACION")
                .contenido("Calificación 6.5 registrada")
                .build();
    }

    @Test
    void recibirEventoCalificacion_construyeReporteYConfirmaConAck() throws IOException {
        when(reporteService.generar(any(Reporte.class))).thenReturn(Optional.of(Reporte.builder().build()));

        listener.recibirEventoCalificacion(evento(), channel, 1L);

        ArgumentCaptor<Reporte> captor = ArgumentCaptor.forClass(Reporte.class);
        verify(reporteService).generar(captor.capture());

        Reporte construido = captor.getValue();
        assertEquals(2L, construido.getIdReferencia());
        assertEquals(1L, construido.getIdGeneradoPor()); // generador por defecto (admin)
        assertEquals(Reporte.TipoReporte.CALIFICACIONES, construido.getTipo());
        assertEquals("Calificación 6.5 registrada", construido.getContenido());
        verify(channel).basicAck(1L, false);
    }

    @Test
    void recibirEventoCalificacion_usaElGeneradorConfigurado() throws IOException {
        ReflectionTestUtils.setField(listener, "idGeneradoPorDefecto", 7L);
        when(reporteService.generar(any(Reporte.class))).thenReturn(Optional.of(Reporte.builder().build()));

        listener.recibirEventoCalificacion(evento(), channel, 1L);

        ArgumentCaptor<Reporte> captor = ArgumentCaptor.forClass(Reporte.class);
        verify(reporteService).generar(captor.capture());
        assertEquals(7L, captor.getValue().getIdGeneradoPor());
    }

    @Test
    void recibirEventoCalificacion_usuarioNoExiste_nackADlqSinReintentos() throws IOException {
        // NO RECUPERABLE: generar() devuelve vacío cuando el usuario no existe
        when(reporteService.generar(any(Reporte.class))).thenReturn(Optional.empty());

        listener.recibirEventoCalificacion(evento(), channel, 2L);

        verify(reporteService, times(1)).generar(any(Reporte.class));
        verify(channel).basicNack(2L, false, false);
        verify(channel, never()).basicAck(anyLong(), anyBoolean());
    }

    @Test
    void recibirEventoCalificacion_servicioCaido_reintentaYLuegoNackADlq() throws IOException {
        // RECUPERABLE: ms-usuarios no responde, se reintenta 3 veces y luego va a la DLQ
        when(reporteService.generar(any(Reporte.class)))
                .thenThrow(new ServicioNoDisponibleException("ms-usuarios caído", new RuntimeException()));

        listener.recibirEventoCalificacion(evento(), channel, 3L);

        verify(reporteService, times(3)).generar(any(Reporte.class));
        verify(channel).basicNack(3L, false, false);
        verify(channel, never()).basicAck(anyLong(), anyBoolean());
    }
}