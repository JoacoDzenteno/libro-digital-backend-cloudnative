package cl.bernardo.ohiggins.ms_comunicaciones.service;

import cl.bernardo.ohiggins.ms_comunicaciones.dto.AsistenciaRegistradaEvent;
import cl.bernardo.ohiggins.ms_comunicaciones.dto.CalificacionRegistradaEvent;
import cl.bernardo.ohiggins.ms_comunicaciones.dto.NotificacionEvent;
import cl.bernardo.ohiggins.ms_comunicaciones.exception.EventoInvalidoException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Reglas de negocio de las alertas de riesgo. No conoce Kafka:
 * recibe el evento ya convertido a objeto y decide si corresponde avisar al apoderado.
 */
@Service
@RequiredArgsConstructor
public class AlertaRiesgoService {

    private static final double NOTA_MINIMA = 4.0;
    private static final String ESTADO_AUSENTE = "AUSENTE";

    private final NotificacionProcesador procesador;
    private final Validator validator;

    public void evaluarCalificacion(CalificacionRegistradaEvent evento) {
        validar(evento);
        if (evento.getNota() >= NOTA_MINIMA) {
            return; // nota aprobatoria: no hay alerta
        }
        procesador.procesar(NotificacionEvent.builder()
                .eventoId(evento.getEventoId())
                .fecha(evento.getFecha())
                .idEstudiante(evento.getIdEstudiante())
                .asunto("Alerta de calificación")
                .contenido("Se registró una nota " + evento.getNota() + " en el período "
                        + evento.getPeriodo() + ".")
                .tipoEvento("CALIFICACION_BAJA")
                .build());
    }

    public void evaluarAsistencia(AsistenciaRegistradaEvent evento) {
        validar(evento);
        if (!ESTADO_AUSENTE.equalsIgnoreCase(evento.getEstado())) {
            return; // solo se alerta por ausencias
        }
        procesador.procesar(NotificacionEvent.builder()
                .eventoId(evento.getEventoId())
                .fecha(evento.getFecha())
                .idEstudiante(evento.getIdEstudiante())
                .asunto("Alerta de inasistencia")
                .contenido("El estudiante estuvo ausente el " + evento.getFecha().toLocalDate() + ".")
                .tipoEvento("INASISTENCIA")
                .build());
    }

    /**
     * Revisa el evento con las mismas reglas que John puso en el DTO (@NotNull, @DecimalMin...).
     * Si algo falla, lanza EventoInvalidoException, que el error handler manda directo al DLT.
     */
    private <T> void validar(T evento) {
        if (evento == null) {
            throw new EventoInvalidoException("Evento vacío");
        }
        Set<ConstraintViolation<T>> errores = validator.validate(evento);
        if (!errores.isEmpty()) {
            String detalle = errores.stream()
                    .map(e -> e.getPropertyPath() + ": " + e.getMessage())
                    .sorted()
                    .collect(Collectors.joining(", "));
            throw new EventoInvalidoException("Evento inválido: " + detalle);
        }
    }
}