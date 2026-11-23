package cl.bernardo.ohiggins.ms_comunicaciones.service;

import cl.bernardo.ohiggins.ms_comunicaciones.dto.AsistenciaRegistradaEvent;
import cl.bernardo.ohiggins.ms_comunicaciones.dto.CalificacionRegistradaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Solo recibe los eventos de Kafka y los delega a AlertaRiesgoService.
 * Los errores no se atrapan aquí: los maneja el DefaultErrorHandler de KafkaConsumerConfig
 * (reintentos y envío al DLT).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaEventosListener {

    private final AlertaRiesgoService alertaRiesgoService;

    // El group-id (comunicaciones-alertas) viene de spring.kafka.consumer.group-id
    @KafkaListener(topics = "${kafka.topicos.calificaciones}")
    public void recibirCalificacion(CalificacionRegistradaEvent evento) {
        log.info("Calificacion recibida: eventoId={}, estudiante={}",
                evento == null ? null : evento.getEventoId(),
                evento == null ? null : evento.getIdEstudiante());
        alertaRiesgoService.evaluarCalificacion(evento);
    }

    @KafkaListener(topics = "${kafka.topicos.asistencia}")
    public void recibirAsistencia(AsistenciaRegistradaEvent evento) {
        log.info("Asistencia recibida: eventoId={}, estudiante={}",
                evento == null ? null : evento.getEventoId(),
                evento == null ? null : evento.getIdEstudiante());
        alertaRiesgoService.evaluarAsistencia(evento);
    }
}