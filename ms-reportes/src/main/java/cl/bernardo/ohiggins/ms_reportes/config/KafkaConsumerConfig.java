package cl.bernardo.ohiggins.ms_reportes.config;

import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DelegatingByTypeSerializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${kafka.topicos.calificaciones}")
    private String topicoCalificaciones;

    @Value("${kafka.topicos.calificaciones-dlt}")
    private String topicoCalificacionesDlt;

    @Value("${kafka.topicos.asistencia-dlt}")
    private String topicoAsistenciaDlt;

    // Productor usado solo para enviar los mensajes fallidos a los topicos DLT.
    // Los mensajes mal formados viajan como bytes; los demas como JSON.
    @Bean
    public ProducerFactory<String, Object> dltProducerFactory(KafkaProperties properties) {
        Map<Class<?>, Serializer<?>> serializadores = Map.<Class<?>, Serializer<?>>of(
                byte[].class, new ByteArraySerializer(),
                Object.class, new JsonSerializer<Object>());
        return new DefaultKafkaProducerFactory<>(
                properties.buildProducerProperties(null),
                new StringSerializer(),
                new DelegatingByTypeSerializer(serializadores, true));
    }

    @Bean
    public KafkaTemplate<String, Object> dltKafkaTemplate(ProducerFactory<String, Object> dltProducerFactory) {
        return new KafkaTemplate<>(dltProducerFactory);
    }

    // Reintenta 3 veces (1 segundo entre intentos) y despues envia el mensaje al DLT
    // del topico original, en la misma particion.
    @Bean
    public DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<String, Object> dltKafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                dltKafkaTemplate,
                (record, ex) -> new TopicPartition(
                        record.topic().equals(topicoCalificaciones) ? topicoCalificacionesDlt : topicoAsistenciaDlt,
                        record.partition()));
        return new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3L));
    }
}