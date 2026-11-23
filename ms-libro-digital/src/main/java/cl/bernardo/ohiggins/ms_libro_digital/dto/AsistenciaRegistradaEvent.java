package cl.bernardo.ohiggins.ms_libro_digital.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsistenciaRegistradaEvent implements Serializable {

    @NotNull(message = "El eventoId es obligatorio")
    private UUID eventoId;

    @NotNull(message = "La fecha es obligatoria")
    private LocalDateTime fecha;

    @NotNull(message = "El idAsistencia es obligatorio")
    private Long idAsistencia;

    @NotNull(message = "El idEstudiante es obligatorio")
    private Long idEstudiante;

    @NotBlank(message = "El estado es obligatorio")
    @Pattern(regexp = "PRESENTE|AUSENTE|TARDANZA|JUSTIFICADO",
             message = "Estado invalido: PRESENTE, AUSENTE, TARDANZA o JUSTIFICADO")
    private String estado;
}