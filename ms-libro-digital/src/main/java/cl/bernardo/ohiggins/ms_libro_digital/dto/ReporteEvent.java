package cl.bernardo.ohiggins.ms_libro_digital.dto;

import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReporteEvent implements Serializable {
    
    private UUID eventoId;
    private LocalDateTime fecha;
    
    private Long idEstudiante;
    private Long idCurso;
    private String tipoReporte;
    private String contenido;
}