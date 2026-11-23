package cl.bernardo.ohiggins.ms_kafka_admin.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Lee el bloque "kafka-admin.topicos" del application.yml.
 * Spring convierte cada elemento de la lista en un objeto Topico.
 * Si falta un dato o viene mal, el servicio no arranca (@Validated).
 */
@Validated
@ConfigurationProperties(prefix = "kafka-admin")
public record TopicosProperties(@NotEmpty List<@Valid Topico> topicos) {

    public record Topico(
            @NotBlank String nombre,
            @Min(1) int particiones,
            @Min(1) int replicas,
            @Min(1) long retencionMs,
            @NotBlank String cleanupPolicy,
            @Min(1) int minInsyncReplicas
    ) {
    }
}