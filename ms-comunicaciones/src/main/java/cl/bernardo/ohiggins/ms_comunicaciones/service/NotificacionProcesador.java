package cl.bernardo.ohiggins.ms_comunicaciones.service;

import cl.bernardo.ohiggins.ms_comunicaciones.dto.NotificacionEvent;
import cl.bernardo.ohiggins.ms_comunicaciones.exception.ApoderadoNoEncontradoException;
import cl.bernardo.ohiggins.ms_comunicaciones.model.Mensaje;
import cl.bernardo.ohiggins.ms_comunicaciones.repository.MensajeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificacionProcesador {

    private final MatriculaClientService matriculaClientService;
    private final MensajeRepository mensajeRepository;

    /**
     * Guarda el aviso para el apoderado del estudiante.
     *
     * @throws ApoderadoNoEncontradoException (NO RECUPERABLE) si el estudiante no tiene apoderado
     * @throws cl.bernardo.ohiggins.ms_comunicaciones.exception.ServicioNoDisponibleException
     *         (RECUPERABLE) si ms-matricula no responde
     */
    public void procesar(NotificacionEvent evento) {
        Long idApoderado = matriculaClientService.obtenerIdApoderado(evento.getIdEstudiante());

        if (idApoderado == null) {
            throw new ApoderadoNoEncontradoException(
                    "El estudiante " + evento.getIdEstudiante() + " no tiene apoderado");
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