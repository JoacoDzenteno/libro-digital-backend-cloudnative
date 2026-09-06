package cl.bernardo.ohiggins.ms_reportes.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "libro-digital-exchange";
    public static final String QUEUE_REPORTES = "cola-reportes";
    public static final String ROUTING_KEY_REPORTE = "libro.reporte";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue colaReportes() {
        return new Queue(QUEUE_REPORTES, true);
    }

    @Bean
    public Binding binding(Queue colaReportes, TopicExchange exchange) {
        return BindingBuilder.bind(colaReportes).to(exchange).with(ROUTING_KEY_REPORTE);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}