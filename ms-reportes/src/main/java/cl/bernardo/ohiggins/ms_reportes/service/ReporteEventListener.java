package cl.bernardo.ohiggins.ms_reportes.service;

import cl.bernardo.ohiggins.ms_reportes.config.RabbitMQConfig;
import cl.bernardo.ohiggins.ms_reportes.dto.ReporteEvent;
import cl.bernardo.ohiggins.ms_reportes.model.Reporte;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReporteEventListener {

    private final ReporteService reporteService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_REPORTES)
    public void recibirEventoCalificacion(ReporteEvent evento) {
        Reporte reporte = Reporte.builder()
                .titulo("Reporte automático de calificación")
                .tipo(Reporte.TipoReporte.CALIFICACIONES)
                .idReferencia(evento.getIdEstudiante())
                .contenido(evento.getContenido())
                .idGeneradoPor(evento.getIdGeneradoPor())
                .build();

        reporteService.generar(reporte);
    }
}