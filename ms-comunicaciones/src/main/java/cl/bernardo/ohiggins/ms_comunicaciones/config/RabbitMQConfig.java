package cl.bernardo.ohiggins.ms_comunicaciones.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${mensajeria.exchange}") private String exchange;
    @Value("${mensajeria.dlx}") private String dlx;

    @Value("${mensajeria.asistencia.routing-key}") private String asistenciaRk;
    @Value("${mensajeria.asistencia.cola}") private String asistenciaCola;
    @Value("${mensajeria.asistencia.dlq}") private String asistenciaDlq;

    @Value("${mensajeria.calificacion-notificacion.routing-key}") private String calificacionRk;
    @Value("${mensajeria.calificacion-notificacion.cola}") private String calificacionCola;
    @Value("${mensajeria.calificacion-notificacion.dlq}") private String calificacionDlq;

    @Value("${mensajeria.hoja-vida.routing-key}") private String hojaVidaRk;
    @Value("${mensajeria.hoja-vida.cola}") private String hojaVidaCola;
    @Value("${mensajeria.hoja-vida.dlq}") private String hojaVidaDlq;

    // ---------- Exchanges ----------

    @Bean
    public TopicExchange libroExchange() {
        return new TopicExchange(exchange);
    }

    // Exchange donde caen los mensajes rechazados (NACK sin reencolar)
    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(dlx);
    }

    // ---------- Asistencia ----------

    @Bean
    public Queue asistenciaQueue() {
        return colaConDlq(asistenciaCola, asistenciaDlq);
    }

    @Bean
    public Queue asistenciaDlq() {
        return QueueBuilder.durable(asistenciaDlq).build();
    }

    @Bean
    public Binding asistenciaBinding() {
        return BindingBuilder.bind(asistenciaQueue()).to(libroExchange()).with(asistenciaRk);
    }

    @Bean
    public Binding asistenciaDlqBinding() {
        return BindingBuilder.bind(asistenciaDlq()).to(dlxExchange()).with(asistenciaDlq);
    }

    // ---------- Calificación (aviso al apoderado) ----------

    @Bean
    public Queue calificacionQueue() {
        return colaConDlq(calificacionCola, calificacionDlq);
    }

    @Bean
    public Queue calificacionDlq() {
        return QueueBuilder.durable(calificacionDlq).build();
    }

    @Bean
    public Binding calificacionBinding() {
        return BindingBuilder.bind(calificacionQueue()).to(libroExchange()).with(calificacionRk);
    }

    @Bean
    public Binding calificacionDlqBinding() {
        return BindingBuilder.bind(calificacionDlq()).to(dlxExchange()).with(calificacionDlq);
    }

    // ---------- Hoja de vida ----------

    @Bean
    public Queue hojaVidaQueue() {
        return colaConDlq(hojaVidaCola, hojaVidaDlq);
    }

    @Bean
    public Queue hojaVidaDlq() {
        return QueueBuilder.durable(hojaVidaDlq).build();
    }

    @Bean
    public Binding hojaVidaBinding() {
        return BindingBuilder.bind(hojaVidaQueue()).to(libroExchange()).with(hojaVidaRk);
    }

    @Bean
    public Binding hojaVidaDlqBinding() {
        return BindingBuilder.bind(hojaVidaDlq()).to(dlxExchange()).with(hojaVidaDlq);
    }

    // ---------- Conversor JSON ----------

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // Cola durable cuyos mensajes rechazados van al DLX con la routing key de su DLQ
    private Queue colaConDlq(String cola, String dlq) {
        return QueueBuilder.durable(cola)
                .withArgument("x-dead-letter-exchange", dlx)
                .withArgument("x-dead-letter-routing-key", dlq)
                .build();
    }
}