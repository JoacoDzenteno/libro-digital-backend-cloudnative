package cl.bernardo.ohiggins.ms_kafka_admin.service;

import cl.bernardo.ohiggins.ms_kafka_admin.dto.GrupoDetalle;
import cl.bernardo.ohiggins.ms_kafka_admin.dto.GrupoResumen;
import cl.bernardo.ohiggins.ms_kafka_admin.dto.TopicoDetalle;
import cl.bernardo.ohiggins.ms_kafka_admin.dto.TopicoResumen;
import cl.bernardo.ohiggins.ms_kafka_admin.exception.KafkaNoDisponibleException;
import cl.bernardo.ohiggins.ms_kafka_admin.exception.OperacionInvalidaException;
import cl.bernardo.ohiggins.ms_kafka_admin.exception.RecursoDuplicadoException;
import cl.bernardo.ohiggins.ms_kafka_admin.exception.RecursoNoEncontradoException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AlterConfigOp;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.clients.admin.ConsumerGroupDescription;
import org.apache.kafka.clients.admin.ConsumerGroupListing;
import org.apache.kafka.clients.admin.ListOffsetsResult;
import org.apache.kafka.clients.admin.NewPartitions;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.OffsetSpec;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.errors.GroupIdNotFoundException;
import org.apache.kafka.common.errors.GroupNotEmptyException;
import org.apache.kafka.common.errors.InvalidConfigurationException;
import org.apache.kafka.common.errors.InvalidPartitionsException;
import org.apache.kafka.common.errors.InvalidReplicationFactorException;
import org.apache.kafka.common.errors.InvalidRequestException;
import org.apache.kafka.common.errors.InvalidTopicException;
import org.apache.kafka.common.errors.TopicExistsException;
import org.apache.kafka.common.errors.UnknownTopicOrPartitionException;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Encapsula toda la administración de Kafka: tópicos y consumer groups.
 * Los controllers solo llaman a estos métodos y no conocen detalles de Kafka.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaAdminService {

    /** Máximo que se espera una respuesta de Kafka antes de darla por fallida. */
    private static final long TIMEOUT_SEGUNDOS = 10;

    private final AdminClient adminClient;

    // ---------- Tópicos ----------

    public List<TopicoResumen> listarTopicos() {
        // listTopics() no incluye los tópicos internos de Kafka (como __consumer_offsets)
        Set<String> nombres = esperar(adminClient.listTopics().names(), "listar tópicos");
        Map<String, TopicDescription> descripciones =
                esperar(adminClient.describeTopics(nombres).allTopicNames(), "describir tópicos");

        return descripciones.values().stream()
                .map(d -> new TopicoResumen(d.name(), d.partitions().size(), contarReplicas(d)))
                .sorted(Comparator.comparing(TopicoResumen::nombre))
                .toList();
    }

    public TopicoDetalle obtenerTopico(String nombre) {
        TopicDescription descripcion = describir(nombre);

        List<TopicoDetalle.ParticionInfo> particiones = descripcion.partitions().stream()
                .map(p -> new TopicoDetalle.ParticionInfo(
                        p.partition(),
                        p.leader() == null ? -1 : p.leader().id(),   // -1 = sin líder (broker caído)
                        ids(p.replicas()),
                        ids(p.isr())))
                .toList();

        return new TopicoDetalle(
                descripcion.name(),
                particiones.size(),
                contarReplicas(descripcion),
                leerConfiguracion(nombre),
                particiones);
    }

    public void crearTopico(String nombre, int particiones, short replicas, Map<String, String> configuracion) {
        if (existeTopico(nombre)) {
            throw new RecursoDuplicadoException("El tópico '" + nombre + "' ya existe");
        }
        NewTopic topico = new NewTopic(nombre, particiones, replicas).configs(configuracion);
        esperar(adminClient.createTopics(List.of(topico)).all(), "crear el tópico " + nombre);
        log.info("Tópico creado: {} (particiones={}, replicas={}, config={})",
                nombre, particiones, replicas, configuracion);
    }

    /** Cambia solo los parámetros enviados (ej. retention.ms); el resto queda igual. */
    public void modificarConfiguracion(String nombre, Map<String, String> cambios) {
        verificarQueExiste(nombre);
        ConfigResource recurso = new ConfigResource(ConfigResource.Type.TOPIC, nombre);
        List<AlterConfigOp> operaciones = cambios.entrySet().stream()
                .map(e -> new AlterConfigOp(new ConfigEntry(e.getKey(), e.getValue()), AlterConfigOp.OpType.SET))
                .toList();

        esperar(adminClient.incrementalAlterConfigs(Map.of(recurso, operaciones)).all(),
                "modificar la configuración de " + nombre);
        log.info("Configuración modificada en {}: {}", nombre, cambios);
    }

    /**
     * Kafka solo permite AUMENTAR particiones, nunca reducirlas:
     * al quitar una partición se perderían los mensajes guardados en ella.
     */
    public void aumentarParticiones(String nombre, int nuevoTotal) {
        int actual = describir(nombre).partitions().size();
        if (nuevoTotal <= actual) {
            throw new OperacionInvalidaException("El tópico '" + nombre + "' tiene " + actual
                    + " particiones; solo se puede aumentar a un número mayor");
        }
        esperar(adminClient.createPartitions(Map.of(nombre, NewPartitions.increaseTo(nuevoTotal))).all(),
                "aumentar particiones de " + nombre);
        log.info("Particiones de {} aumentadas de {} a {}", nombre, actual, nuevoTotal);
    }

    public void eliminarTopico(String nombre) {
        verificarQueExiste(nombre);
        esperar(adminClient.deleteTopics(List.of(nombre)).all(), "eliminar el tópico " + nombre);
        log.info("Tópico eliminado: {}", nombre);
    }

    // ---------- Consumer groups ----------

    public List<GrupoResumen> listarGrupos() {
        List<String> ids = esperar(adminClient.listConsumerGroups().all(), "listar consumer groups").stream()
                .map(ConsumerGroupListing::groupId)
                .toList();
        if (ids.isEmpty()) {
            return List.of();
        }
        Map<String, ConsumerGroupDescription> descripciones =
                esperar(adminClient.describeConsumerGroups(ids).all(), "describir consumer groups");

        return descripciones.values().stream()
                .map(g -> new GrupoResumen(g.groupId(), g.state().toString(), g.members().size()))
                .sorted(Comparator.comparing(GrupoResumen::grupo))
                .toList();
    }

    public GrupoDetalle obtenerGrupo(String grupo) {
        verificarQueExisteGrupo(grupo);
        ConsumerGroupDescription descripcion =
                esperar(adminClient.describeConsumerGroups(List.of(grupo)).all(), "describir " + grupo)
                        .get(grupo);

        // Qué particiones tiene asignadas cada consumidor del grupo
        List<GrupoDetalle.MiembroInfo> miembros = descripcion.members().stream()
                .map(m -> new GrupoDetalle.MiembroInfo(
                        m.clientId(),
                        m.host(),
                        m.assignment().topicPartitions().stream()
                                .map(TopicPartition::toString)
                                .sorted()
                                .toList()))
                .toList();

        List<GrupoDetalle.OffsetInfo> offsets = calcularOffsets(grupo);
        long lagTotal = offsets.stream().mapToLong(GrupoDetalle.OffsetInfo::lag).sum();

        return new GrupoDetalle(grupo, descripcion.state().toString(), miembros, offsets, lagTotal);
    }

    /** Kafka solo deja eliminar un grupo sin consumidores activos (estado EMPTY). */
    public void eliminarGrupo(String grupo) {
        verificarQueExisteGrupo(grupo);
        esperar(adminClient.deleteConsumerGroups(List.of(grupo)).all(), "eliminar el consumer group " + grupo);
        log.info("Consumer group eliminado: {}", grupo);
    }

    /**
     * Por cada partición que lee el grupo: hasta dónde leyó, cuál es el último mensaje
     * y la diferencia entre ambos (lag = mensajes pendientes).
     */
    private List<GrupoDetalle.OffsetInfo> calcularOffsets(String grupo) {
        Map<TopicPartition, OffsetAndMetadata> consumidos = esperar(
                adminClient.listConsumerGroupOffsets(grupo).partitionsToOffsetAndMetadata(),
                "leer los offsets de " + grupo);

        // Se pide a Kafka el último offset (latest) de cada partición que el grupo ha leído
        Map<TopicPartition, OffsetSpec> pedido = new HashMap<>();
        consumidos.forEach((particion, offset) -> {
            if (offset != null) {
                pedido.put(particion, OffsetSpec.latest());
            }
        });
        if (pedido.isEmpty()) {
            return List.of();
        }
        Map<TopicPartition, ListOffsetsResult.ListOffsetsResultInfo> finales =
                esperar(adminClient.listOffsets(pedido).all(), "leer el final de las particiones");

        return pedido.keySet().stream()
                .map(tp -> {
                    long consumido = consumidos.get(tp).offset();
                    long fin = finales.get(tp).offset();
                    return new GrupoDetalle.OffsetInfo(tp.topic(), tp.partition(), consumido, fin,
                            Math.max(0, fin - consumido));
                })
                .sorted(Comparator.comparing(GrupoDetalle.OffsetInfo::topico)
                        .thenComparingInt(GrupoDetalle.OffsetInfo::particion))
                .toList();
    }

    // ---------- Apoyo ----------

    private boolean existeTopico(String nombre) {
        return esperar(adminClient.listTopics().names(), "listar tópicos").contains(nombre);
    }

    private void verificarQueExiste(String nombre) {
        if (!existeTopico(nombre)) {
            throw new RecursoNoEncontradoException("El tópico '" + nombre + "' no existe");
        }
    }

    private void verificarQueExisteGrupo(String grupo) {
        boolean existe = esperar(adminClient.listConsumerGroups().all(), "listar consumer groups").stream()
                .anyMatch(g -> g.groupId().equals(grupo));
        if (!existe) {
            throw new RecursoNoEncontradoException("El consumer group '" + grupo + "' no existe");
        }
    }

    private TopicDescription describir(String nombre) {
        verificarQueExiste(nombre);
        return esperar(adminClient.describeTopics(List.of(nombre)).allTopicNames(), "describir " + nombre)
                .get(nombre);
    }

    /** Solo los parámetros definidos para este tópico, no los valores por defecto del broker. */
    private Map<String, String> leerConfiguracion(String nombre) {
        ConfigResource recurso = new ConfigResource(ConfigResource.Type.TOPIC, nombre);
        Config config = esperar(adminClient.describeConfigs(List.of(recurso)).all(), "leer configuración de " + nombre)
                .get(recurso);

        Map<String, String> propios = new TreeMap<>();
        config.entries().stream()
                .filter(e -> e.source() == ConfigEntry.ConfigSource.DYNAMIC_TOPIC_CONFIG)
                .forEach(e -> propios.put(e.name(), e.value()));
        return propios;
    }

    private int contarReplicas(TopicDescription descripcion) {
        return descripcion.partitions().get(0).replicas().size();
    }

    private List<Integer> ids(List<Node> nodos) {
        return nodos.stream().map(Node::id).toList();
    }

    /**
     * Las operaciones de AdminClient son asíncronas: devuelven un KafkaFuture (una "promesa"
     * de respuesta). Aquí se espera esa respuesta con un tiempo máximo y los errores de Kafka
     * se traducen a nuestras excepciones, que después se convierten en códigos HTTP.
     */
    private <T> T esperar(KafkaFuture<T> futuro, String operacion) {
        try {
            return futuro.get(TIMEOUT_SEGUNDOS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KafkaNoDisponibleException("Se interrumpió la operación: " + operacion);
        } catch (TimeoutException e) {
            throw new KafkaNoDisponibleException("Kafka no respondió a tiempo al " + operacion);
        } catch (ExecutionException e) {
            throw traducir(e.getCause(), operacion);
        }
    }

    private RuntimeException traducir(Throwable causa, String operacion) {
        if (causa instanceof TopicExistsException) {
            return new RecursoDuplicadoException(causa.getMessage());
        }
        if (causa instanceof UnknownTopicOrPartitionException || causa instanceof GroupIdNotFoundException) {
            return new RecursoNoEncontradoException(causa.getMessage());
        }
        // Un grupo con consumidores activos no se puede borrar: primero hay que detenerlos
        if (causa instanceof GroupNotEmptyException) {
            return new OperacionInvalidaException(
                    "El consumer group tiene consumidores activos; deténgalos antes de eliminarlo");
        }
        // Datos que Kafka rechaza: más réplicas que brokers, parámetro inexistente, nombre inválido...
        if (causa instanceof InvalidReplicationFactorException
                || causa instanceof InvalidPartitionsException
                || causa instanceof InvalidConfigurationException
                || causa instanceof InvalidRequestException
                || causa instanceof InvalidTopicException) {
            return new OperacionInvalidaException(causa.getMessage());
        }
        log.error("Error de Kafka al {}", operacion, causa);
        return new KafkaNoDisponibleException("Error de Kafka al " + operacion + ": " + causa.getMessage());
    }
}