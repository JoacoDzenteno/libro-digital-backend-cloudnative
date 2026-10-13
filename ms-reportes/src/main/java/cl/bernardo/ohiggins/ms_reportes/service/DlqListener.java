package cl.bernardo.ohiggins.ms_reportes.service;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Lee la cola de mensajes muertos (DLQ) de reportes y los deja registrados en los logs
 * para poder revisarlos. Recibe el mensaje crudo (sin convertirlo a objeto)
 * para que nunca falle por un cuerpo mal formado. Luego lo confirma con basicAck
 * para que no quede acumulado en la DLQ.
 */
@Slf4j
@Service
public class DlqListener {

    @RabbitListener(queues = "${mensajeria.calificacion-reporte.dlq}")
    public void reporte(Message mensaje, Channel channel) throws IOException {
        MessageProperties props = mensaje.getMessageProperties();
        String cuerpo = new String(mensaje.getBody(), StandardCharsets.UTF_8);

        // El cuerpo es el JSON del evento: ahí viene el eventoId y la fecha.
        log.error("[DLQ-reporte] mensaje fallido. motivo={} colaOrigen={} cuerpo={}",
                props.getHeader("x-first-death-reason"),
                props.getHeader("x-first-death-queue"),
                cuerpo);

        channel.basicAck(props.getDeliveryTag(), false);
    }
}