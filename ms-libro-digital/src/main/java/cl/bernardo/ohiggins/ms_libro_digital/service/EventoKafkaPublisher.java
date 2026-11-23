package cl.bernardo.ohiggins.ms_libro_digital.service;

import cl.bernardo.ohiggins.ms_libro_digital.dto.AsistenciaRegistradaEvent;
import cl.bernardo.ohiggins.ms_libro_digital.dto.CalificacionRegistradaEvent;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class EventoKafkaPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Validator validator;

    // Nombres de los topicos leidos desde el application.yaml
    @Value("${kafka.topicos.calificaciones}")
    private String topicoCalificaciones;

    @Value("${kafka.topicos.asistencia}")
    private String topicoAsistencia;

    public EventoKafkaPublisher(KafkaTemplate<String, Object> kafkaTemplate, Validator validator) {
        this.kafkaTemplate = kafkaTemplate;
        this.validator = validator;
    }

    public void publicarCalificacion(CalificacionRegistradaEvent event) {
        event.setEventoId(UUID.randomUUID());
        event.setFecha(LocalDateTime.now());
        enviar(topicoCalificaciones, event.getIdEstudiante(), event);
    }

    public void publicarAsistencia(AsistenciaRegistradaEvent event) {
        event.setEventoId(UUID.randomUUID());
        event.setFecha(LocalDateTime.now());
        enviar(topicoAsistencia, event.getIdEstudiante(), event);
    }

    // Metodo comun: valida, envia y registra el resultado en el log
    private void enviar(String topico, Long idEstudiante, Object event) {
        Set<ConstraintViolation<Object>> errores = validator.validate(event);
        if (!errores.isEmpty()) {
            log.error("Evento invalido, no se publica en Kafka. Topico: {}. Errores: {}",
                    topico, errores.stream().map(ConstraintViolation::getMessage).toList());
            return;
        }

        try {
            // La key es el idEstudiante: los eventos del mismo alumno quedan en la misma particion
            kafkaTemplate.send(topico, String.valueOf(idEstudiante), event)
                    .whenComplete((resultado, error) -> {
                        if (error != null) {
                            log.error("No se pudo publicar en Kafka. Topico: {}. Causa: {}",
                                    topico, error.getMessage());
                        } else {
                            log.info("Evento publicado en Kafka. Topico: {}, particion: {}, offset: {}",
                                    topico,
                                    resultado.getRecordMetadata().partition(),
                                    resultado.getRecordMetadata().offset());
                        }
                    });
        } catch (Exception e) {
            log.error("Error al publicar en Kafka. Topico: {}. Causa: {}", topico, e.getMessage());
        }
    }
}