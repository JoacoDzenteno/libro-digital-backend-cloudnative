package cl.bernardo.ohiggins.ms_comunicaciones.service;

import cl.bernardo.ohiggins.ms_comunicaciones.dto.AsistenciaRegistradaEvent;
import cl.bernardo.ohiggins.ms_comunicaciones.dto.CalificacionRegistradaEvent;
import cl.bernardo.ohiggins.ms_comunicaciones.dto.NotificacionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaEventosListener {

    private static final double NOTA_MINIMA = 4.0;
    private static final String ESTADO_AUSENTE = "AUSENTE";

    private final NotificacionProcesador procesador;

    // El group-id (comunicaciones-alertas) viene de spring.kafka.consumer.group-id
    @KafkaListener(topics = "${kafka.topicos.calificaciones}")
    public void recibirCalificacion(CalificacionRegistradaEvent evento) {
        log.info("Calificacion recibida: estudiante={}, nota={}", evento.getIdEstudiante(), evento.getNota());

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

    @KafkaListener(topics = "${kafka.topicos.asistencia}")
    public void recibirAsistencia(AsistenciaRegistradaEvent evento) {
        log.info("Asistencia recibida: estudiante={}, estado={}", evento.getIdEstudiante(), evento.getEstado());

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
}