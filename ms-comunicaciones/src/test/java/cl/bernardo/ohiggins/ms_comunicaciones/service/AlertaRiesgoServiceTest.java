package cl.bernardo.ohiggins.ms_comunicaciones.service;

import cl.bernardo.ohiggins.ms_comunicaciones.dto.AsistenciaRegistradaEvent;
import cl.bernardo.ohiggins.ms_comunicaciones.dto.CalificacionRegistradaEvent;
import cl.bernardo.ohiggins.ms_comunicaciones.dto.NotificacionEvent;
import cl.bernardo.ohiggins.ms_comunicaciones.exception.EventoInvalidoException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Reglas de las alertas de riesgo, sin Kafka: se llama al service directamente.
 * El NotificacionProcesador es simulado (mock) para comprobar si se generó o no una alerta.
 */
@ExtendWith(MockitoExtension.class)
class AlertaRiesgoServiceTest {

    // Validador real, el mismo que usa Spring: revisa las anotaciones de los DTO
    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    @Mock
    private NotificacionProcesador procesador;

    private AlertaRiesgoService service;

    @BeforeEach
    void setUp() {
        service = new AlertaRiesgoService(procesador, VALIDATOR);
    }

    // ---------- Calificaciones ----------

    @Test
    void notaBajoCuatroGeneraAlerta() {
        service.evaluarCalificacion(calificacion(3.5));

        ArgumentCaptor<NotificacionEvent> captor = ArgumentCaptor.forClass(NotificacionEvent.class);
        verify(procesador).procesar(captor.capture());
        assertThat(captor.getValue().getTipoEvento()).isEqualTo("CALIFICACION_BAJA");
        assertThat(captor.getValue().getIdEstudiante()).isEqualTo(2L);
    }

    @Test
    void notaAprobatoriaNoGeneraAlerta() {
        service.evaluarCalificacion(calificacion(5.5));

        verify(procesador, never()).procesar(any());
    }

    @Test
    void notaCuatroEsAprobatoriaYNoGeneraAlerta() {
        service.evaluarCalificacion(calificacion(4.0));

        verify(procesador, never()).procesar(any());
    }

    @Test
    void calificacionSinNotaEsInvalida() {
        assertThatThrownBy(() -> service.evaluarCalificacion(calificacion(null)))
                .isInstanceOf(EventoInvalidoException.class)
                .hasMessageContaining("nota");

        verify(procesador, never()).procesar(any());
    }

    @Test
    void notaFueraDeRangoEsInvalida() {
        assertThatThrownBy(() -> service.evaluarCalificacion(calificacion(8.0)))
                .isInstanceOf(EventoInvalidoException.class)
                .hasMessageContaining("nota");

        verify(procesador, never()).procesar(any());
    }

    // ---------- Asistencia ----------

    @Test
    void ausenciaGeneraAlerta() {
        service.evaluarAsistencia(asistencia("AUSENTE"));

        ArgumentCaptor<NotificacionEvent> captor = ArgumentCaptor.forClass(NotificacionEvent.class);
        verify(procesador).procesar(captor.capture());
        assertThat(captor.getValue().getTipoEvento()).isEqualTo("INASISTENCIA");
    }

    @Test
    void presenteNoGeneraAlerta() {
        service.evaluarAsistencia(asistencia("PRESENTE"));

        verify(procesador, never()).procesar(any());
    }

    @Test
    void estadoDesconocidoEsInvalido() {
        assertThatThrownBy(() -> service.evaluarAsistencia(asistencia("DORMIDO")))
                .isInstanceOf(EventoInvalidoException.class)
                .hasMessageContaining("estado");

        verify(procesador, never()).procesar(any());
    }

    @Test
    void eventoVacioEsInvalido() {
        assertThatThrownBy(() -> service.evaluarAsistencia(null))
                .isInstanceOf(EventoInvalidoException.class);

        verify(procesador, never()).procesar(any());
    }

    // ---------- Datos de prueba ----------

    private CalificacionRegistradaEvent calificacion(Double nota) {
        return CalificacionRegistradaEvent.builder()
                .eventoId(UUID.randomUUID())
                .fecha(LocalDateTime.of(2026, 11, 23, 10, 0))
                .idCalificacion(1L)
                .idEstudiante(2L)
                .idCurso(10L)
                .nota(nota)
                .periodo("2026-2")
                .build();
    }

    private AsistenciaRegistradaEvent asistencia(String estado) {
        return AsistenciaRegistradaEvent.builder()
                .eventoId(UUID.randomUUID())
                .fecha(LocalDateTime.of(2026, 11, 23, 8, 0))
                .idAsistencia(1L)
                .idEstudiante(2L)
                .estado(estado)
                .build();
    }
}