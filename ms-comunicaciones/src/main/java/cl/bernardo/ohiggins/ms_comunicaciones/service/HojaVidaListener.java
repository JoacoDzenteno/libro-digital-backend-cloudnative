package cl.bernardo.ohiggins.ms_comunicaciones.service;

import cl.bernardo.ohiggins.ms_comunicaciones.dto.NotificacionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HojaVidaListener {

    private final NotificacionProcesador procesador;

    @RabbitListener(queues = "${mensajeria.hoja-vida.cola}")
    public void recibir(NotificacionEvent evento) {
        procesador.procesar(evento);
    }
}