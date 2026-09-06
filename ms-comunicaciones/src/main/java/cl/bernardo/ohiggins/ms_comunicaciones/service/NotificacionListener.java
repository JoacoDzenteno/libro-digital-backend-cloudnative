package cl.bernardo.ohiggins.ms_comunicaciones.service;

import cl.bernardo.ohiggins.ms_comunicaciones.config.RabbitMQConfig;
import cl.bernardo.ohiggins.ms_comunicaciones.dto.NotificacionEvent;
import cl.bernardo.ohiggins.ms_comunicaciones.model.Mensaje;
import cl.bernardo.ohiggins.ms_comunicaciones.repository.MensajeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificacionListener {

    private final MatriculaClientService matriculaClientService;
    private final MensajeRepository mensajeRepository;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void recibirNotificacion(NotificacionEvent evento) {
        Long idApoderado = matriculaClientService.obtenerIdApoderado(evento.getIdEstudiante());

        if (idApoderado == null) {
            return; // No se encontró apoderado, no se puede notificar
        }

        Mensaje mensaje = Mensaje.builder()
                .idRemitente(evento.getIdEstudiante()) // referencia al estudiante asociado al evento
                .idDestinatario(idApoderado)
                .asunto(evento.getAsunto())
                .contenido(evento.getContenido())
                .fechaEnvio(LocalDateTime.now())
                .tipo(Mensaje.TipoMensaje.NOTIFICACION)
                .leido(false)
                .build();

        mensajeRepository.save(mensaje);
    }
}