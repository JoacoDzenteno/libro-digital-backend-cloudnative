package cl.bernardo.ohiggins.ms_comunicaciones.service;

import cl.bernardo.ohiggins.ms_comunicaciones.dto.NotificacionEvent;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class CalificacionListener {

    private final NotificacionProcesador procesador;
    private final ConfirmadorMensajes confirmador;

    @RabbitListener(queues = "${mensajeria.calificacion-notificacion.cola}")
    public void recibir(NotificacionEvent evento, Channel channel,
                        @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        confirmador.procesar("calificacion", evento.getEventoId(), channel, tag,
                () -> procesador.procesar(evento));
    }
}