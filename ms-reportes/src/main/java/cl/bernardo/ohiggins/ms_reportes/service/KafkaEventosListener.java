package cl.bernardo.ohiggins.ms_reportes.service;

import cl.bernardo.ohiggins.ms_reportes.dto.AsistenciaRegistradaEvent;
import cl.bernardo.ohiggins.ms_reportes.dto.CalificacionRegistradaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaEventosListener {

    private final EstadisticaService estadisticaService;

    // El group-id (reportes-analitica) viene de spring.kafka.consumer.group-id
    @KafkaListener(topics = "${kafka.topicos.calificaciones}")
    public void recibirCalificacion(CalificacionRegistradaEvent evento) {
        log.info("Calificacion recibida: estudiante={}, nota={}", evento.getIdEstudiante(), evento.getNota());
        estadisticaService.registrarCalificacion(evento.getIdEstudiante(), evento.getNota());
    }

    @KafkaListener(topics = "${kafka.topicos.asistencia}")
    public void recibirAsistencia(AsistenciaRegistradaEvent evento) {
        log.info("Asistencia recibida: estudiante={}, estado={}", evento.getIdEstudiante(), evento.getEstado());
        estadisticaService.registrarAsistencia(evento.getIdEstudiante(), evento.getEstado());
    }
}