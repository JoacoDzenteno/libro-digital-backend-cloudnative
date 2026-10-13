package cl.bernardo.ohiggins.ms_reportes.service;

import cl.bernardo.ohiggins.ms_reportes.dto.ReporteEvent;
import cl.bernardo.ohiggins.ms_reportes.exception.UsuarioNoEncontradoException;
import cl.bernardo.ohiggins.ms_reportes.model.Reporte;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ReporteEventListener {

    private final ReporteService reporteService;
    private final ConfirmadorMensajes confirmador;

    // El evento de calificación ya no trae quién generó el reporte (las calificaciones
    // no registran al profesor), así que se asume el usuario por defecto (1 = admin).
    @Value("${reportes.generador-por-defecto:1}")
    private Long idGeneradoPorDefecto = 1L;

    @RabbitListener(queues = "${mensajeria.calificacion-reporte.cola}")
    public void recibirEventoCalificacion(ReporteEvent evento, Channel channel,
                                          @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        confirmador.procesar("reporte-calificacion", evento.getEventoId(), channel, tag, () -> {
            Reporte reporte = Reporte.builder()
                    .titulo("Reporte automático de calificación")
                    .tipo(Reporte.TipoReporte.CALIFICACIONES)
                    .idReferencia(evento.getIdEstudiante())
                    .contenido(evento.getContenido())
                    .idGeneradoPor(idGeneradoPorDefecto)
                    .build();

            // NO RECUPERABLE: generar() devuelve vacío cuando el usuario generador
            // o el estudiante no existen. Reintentar no cambia el resultado.
            // RECUPERABLE: si ms-usuarios no responde, generar() deja pasar
            // ServicioNoDisponibleException y el confirmador reintenta.
            if (reporteService.generar(reporte).isEmpty()) {
                throw new UsuarioNoEncontradoException(
                        "No se pudo generar el reporte: el estudiante " + evento.getIdEstudiante()
                                + " o el usuario " + idGeneradoPorDefecto + " no existe");
            }
        });
    }
}