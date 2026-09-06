package cl.bernardo.ohiggins.ms_matricula.dto;

import cl.bernardo.ohiggins.ms_matricula.model.Matricula;
import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatriculaDTO {
    private Long id;
    private Long idEstudiante;
    private Long idCurso;
    private LocalDate fechaMatricula;
    private Integer anioEscolar;
    private Matricula.EstadoMatricula estado;
    private Long idApoderado;
}