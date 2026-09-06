package cl.bernardo.ohiggins.ms_reportes.dto;

import cl.bernardo.ohiggins.ms_reportes.model.Reporte;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReporteDTO {
    private Long id;
    private String titulo;
    private Reporte.TipoReporte tipo;
    private Long idReferencia;
    private String contenido;
    private LocalDateTime fechaGeneracion;
    private Long idGeneradoPor;
}