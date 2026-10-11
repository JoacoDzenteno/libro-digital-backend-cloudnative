package cl.bernardo.ohiggins.ms_reportes.config;

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

    @Value("${mensajeria.calificacion-reporte.routing-key}") private String reporteRk;
    @Value("${mensajeria.calificacion-reporte.cola}") private String reporteCola;
    @Value("${mensajeria.calificacion-reporte.dlq}") private String reporteDlq;

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

    // ---------- Calificación (reporte) ----------

    @Bean
    public Queue reporteQueue() {
        return colaConDlq(reporteCola, reporteDlq);
    }

    @Bean
    public Queue reporteDlq() {
        return QueueBuilder.durable(reporteDlq).build();
    }

    @Bean
    public Binding reporteBinding() {
        return BindingBuilder.bind(reporteQueue()).to(libroExchange()).with(reporteRk);
    }

    @Bean
    public Binding reporteDlqBinding() {
        return BindingBuilder.bind(reporteDlq()).to(dlxExchange()).with(reporteDlq);
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