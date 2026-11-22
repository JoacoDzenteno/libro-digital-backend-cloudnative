package cl.bernardo.ohiggins.ms_kafka_admin.config;

import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

/**
 * Crea el AdminClient, la herramienta de Kafka para administrarlo desde Java.
 * Reutiliza la conexión que Spring ya armó con los datos del application.yml
 * (bootstrap-servers), así no se repite la configuración.
 */
@Configuration
public class KafkaAdminClientConfig {

    // destroyMethod = "close": al apagar el servicio se cierran las conexiones con los brokers
    @Bean(destroyMethod = "close")
    public AdminClient adminClient(KafkaAdmin kafkaAdmin) {
        return AdminClient.create(kafkaAdmin.getConfigurationProperties());
    }
}