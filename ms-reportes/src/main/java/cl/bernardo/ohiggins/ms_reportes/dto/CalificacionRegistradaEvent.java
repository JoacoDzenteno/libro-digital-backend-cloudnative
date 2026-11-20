package cl.bernardo.ohiggins.ms_reportes.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalificacionRegistradaEvent implements Serializable {

    @NotNull(message = "El eventoId es obligatorio")
    private UUID eventoId;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDateTime fecha;

    @NotNull(message = "El idCalificacion es obligatorio")
    private Long idCalificacion;

    @NotNull(message = "El idEstudiante es obligatorio")
    private Long idEstudiante;

    @NotNull(message = "El idCurso es obligatorio")
    private Long idCurso;

    @NotNull(message = "La nota es obligatoria")
    @DecimalMin(value = "1.0", message = "La nota minima es 1.0")
    @DecimalMax(value = "7.0", message = "La nota maxima es 7.0")
    private Double nota;

    @NotBlank(message = "El periodo es obligatorio")
    private String periodo;
}