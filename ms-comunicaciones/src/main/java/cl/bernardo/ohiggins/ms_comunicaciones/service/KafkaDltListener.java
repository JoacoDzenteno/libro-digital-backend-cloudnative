package cl.bernardo.ohiggins.ms_comunicaciones.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * Lee los tópicos DLT y deja registrado en el log cada mensaje fallido, para poder revisarlo.
 * Equivalente al DlqListener de RabbitMQ.
 * Recibe el mensaje como texto (StringDeserializer) para que nunca falle por un cuerpo mal formado.
 */
@Slf4j
@Service
public class KafkaDltListener {

    @KafkaListener(
            topics = {"${kafka.topicos.calificaciones-dlt}", "${kafka.topicos.asistencia-dlt}"},
            groupId = "comunicaciones-alertas-dlt",
            properties = "value.deserializer=org.apache.kafka.common.serialization.StringDeserializer")
    public void registrar(ConsumerRecord<String, String> registro) {
        log.error("[DLT] mensaje fallido. topicoOrigen={} particion={} motivo={} cuerpo={}",
                header(registro, KafkaHeaders.DLT_ORIGINAL_TOPIC),
                registro.partition(),
                header(registro, KafkaHeaders.DLT_EXCEPTION_MESSAGE),
                registro.value());
    }

    // Cuando un mensaje se envía al DLT, Kafka le agrega encabezados con el tópico de origen y el error
    private String header(ConsumerRecord<?, ?> registro, String nombre) {
        Header header = registro.headers().lastHeader(nombre);
        return header == null ? "-" : new String(header.value(), StandardCharsets.UTF_8);
    }
}