package cl.bernardo.ohiggins.ms_libro_digital.dto;

import lombok.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReporteEvent implements Serializable {
    private Long idEstudiante;
    private Long idGeneradoPor;
    private String contenido;
}