package cl.bernardo.ohiggins.ms_kafka_admin.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

/**
 * Convierte la lista de tópicos del yaml en órdenes de creación (NewTopic).
 * Al arrancar, KafkaAdmin crea los que no existen y no duplica los que ya están.
 */
@Configuration
@EnableConfigurationProperties(TopicosProperties.class)
public class KafkaTopicosConfig {

    @Bean
    public KafkaAdmin.NewTopics topicosDelContrato(TopicosProperties properties) {
        NewTopic[] topicos = properties.topicos().stream()
                .map(this::aNewTopic)
                .toArray(NewTopic[]::new);
        return new KafkaAdmin.NewTopics(topicos);
    }

    private NewTopic aNewTopic(TopicosProperties.Topico t) {
        return TopicBuilder.name(t.nombre())
                .partitions(t.particiones())
                .replicas(t.replicas())
                .config(TopicConfig.RETENTION_MS_CONFIG, String.valueOf(t.retencionMs()))
                .config(TopicConfig.CLEANUP_POLICY_CONFIG, t.cleanupPolicy())
                .config(TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG, String.valueOf(t.minInsyncReplicas()))
                .build();
    }
}