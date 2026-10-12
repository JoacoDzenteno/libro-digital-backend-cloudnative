package cl.bernardo.ohiggins.ms_comunicaciones.service;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Lee las colas de mensajes muertos (DLQ) y los deja registrados en los logs
 * para poder revisarlos. Recibe el mensaje crudo (sin convertirlo a objeto)
 * para que nunca falle por un cuerpo mal formado. Luego lo confirma con basicAck
 * para que no quede acumulado en la DLQ.
 */
@Slf4j
@Service
public class DlqListener {

    @RabbitListener(queues = "${mensajeria.asistencia.dlq}")
    public void asistencia(Message mensaje, Channel channel) throws IOException {
        registrar("asistencia", mensaje, channel);
    }

    @RabbitListener(queues = "${mensajeria.calificacion-notificacion.dlq}")
    public void calificacion(Message mensaje, Channel channel) throws IOException {
        registrar("calificacion", mensaje, channel);
    }

    @RabbitListener(queues = "${mensajeria.hoja-vida.dlq}")
    public void hojaVida(Message mensaje, Channel channel) throws IOException {
        registrar("hoja-vida", mensaje, channel);
    }

    private void registrar(String origen, Message mensaje, Channel channel) throws IOException {
        MessageProperties props = mensaje.getMessageProperties();
        String cuerpo = new String(mensaje.getBody(), StandardCharsets.UTF_8);

        // El cuerpo es el JSON del evento: ahí viene el eventoId y la fecha.
        log.error("[DLQ-{}] mensaje fallido. motivo={} colaOrigen={} cuerpo={}",
                origen,
                props.getHeader("x-first-death-reason"),
                props.getHeader("x-first-death-queue"),
                cuerpo);

        channel.basicAck(props.getDeliveryTag(), false);
    }
}