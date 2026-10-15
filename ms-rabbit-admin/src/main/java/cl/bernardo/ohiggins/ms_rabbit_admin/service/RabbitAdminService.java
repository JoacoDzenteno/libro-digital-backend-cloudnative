package cl.bernardo.ohiggins.ms_rabbit_admin.service;

import cl.bernardo.ohiggins.ms_rabbit_admin.exception.RecursoDuplicadoException;
import cl.bernardo.ohiggins.ms_rabbit_admin.exception.RecursoNoEncontradoException;
import cl.bernardo.ohiggins.ms_rabbit_admin.model.TipoExchange;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpConnectException; 
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * Encapsula toda la administración de RabbitMQ (colas, exchanges y bindings).
 * Los controllers solo llaman a estos métodos y no conocen detalles de RabbitMQ.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitAdminService {

    private final AmqpAdmin amqpAdmin;
    private final RabbitTemplate rabbitTemplate;

    // ---------- Colas ----------

    public void crearCola(String nombre, boolean durable) {
        if (existeCola(nombre)) {
            throw new RecursoDuplicadoException("La cola '" + nombre + "' ya existe");
        }
        amqpAdmin.declareQueue(new Queue(nombre, durable));
        log.info("Cola creada: {} (durable={})", nombre, durable);
    }

    public void eliminarCola(String nombre) {
        if (!existeCola(nombre)) {
            throw new RecursoNoEncontradoException("La cola '" + nombre + "' no existe");
        }
        amqpAdmin.deleteQueue(nombre);
        log.info("Cola eliminada: {}", nombre);
    }

    // ---------- Exchanges ----------

    public void crearExchange(String nombre, TipoExchange tipo, boolean durable) {
        if (existeExchange(nombre)) {
            throw new RecursoDuplicadoException("El exchange '" + nombre + "' ya existe");
        }
        Exchange exchange = switch (tipo) {
            case DIRECT -> new DirectExchange(nombre, durable, false);
            case TOPIC -> new TopicExchange(nombre, durable, false);
            case FANOUT -> new FanoutExchange(nombre, durable, false);
        };
        amqpAdmin.declareExchange(exchange);
        log.info("Exchange creado: {} (tipo={}, durable={})", nombre, tipo, durable);
    }

    public void eliminarExchange(String nombre) {
        if (!existeExchange(nombre)) {
            throw new RecursoNoEncontradoException("El exchange '" + nombre + "' no existe");
        }
        amqpAdmin.deleteExchange(nombre);
        log.info("Exchange eliminado: {}", nombre);
    }

    // ---------- Bindings ----------

    public void crearBinding(String cola, String exchange, String routingKey) {
        validarQueExistan(cola, exchange);
        amqpAdmin.declareBinding(construirBinding(cola, exchange, routingKey));
        log.info("Binding creado: {} -> {} (routingKey={})", exchange, cola, routingKey);
    }

    public void eliminarBinding(String cola, String exchange, String routingKey) {
        validarQueExistan(cola, exchange);
        amqpAdmin.removeBinding(construirBinding(cola, exchange, routingKey));
        log.info("Binding eliminado: {} -> {} (routingKey={})", exchange, cola, routingKey);
    }

    // ---------- Apoyo interno ----------

    private boolean existeCola(String nombre) {
        // getQueueInfo devuelve null cuando la cola no existe
        return amqpAdmin.getQueueInfo(nombre) != null;
    }

    private boolean existeExchange(String nombre) {
        // Declaración pasiva: pregunta si existe sin crearlo.
        // Si no existe, RabbitMQ responde con error y lo traducimos a false.
        try {
            rabbitTemplate.execute(channel -> channel.exchangeDeclarePassive(nombre));
            return true;
        } catch (AmqpConnectException e) {
            // RabbitMQ no está disponible: no es "no existe", se propaga para responder 503
            throw e;
        } catch (AmqpException e) {
            return false;
        }
    }

    private void validarQueExistan(String cola, String exchange) {
        if (!existeCola(cola)) {
            throw new RecursoNoEncontradoException("La cola '" + cola + "' no existe");
        }
        if (!existeExchange(exchange)) {
            throw new RecursoNoEncontradoException("El exchange '" + exchange + "' no existe");
        }
    }

    private Binding construirBinding(String cola, String exchange, String routingKey) {
        return new Binding(cola, Binding.DestinationType.QUEUE, exchange, routingKey, null);
    }
}