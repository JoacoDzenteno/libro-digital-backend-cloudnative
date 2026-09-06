package cl.bernardo.ohiggins.ms_matricula.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "matricula")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Matricula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_estudiante", nullable = false)
    private Long idEstudiante;

    @Column(name = "id_curso", nullable = false)
    private Long idCurso;

    @Column(name = "fecha_matricula", nullable = false)
    private LocalDate fechaMatricula;

    @Column(name = "anio_escolar", nullable = false)
    private Integer anioEscolar;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoMatricula estado;

    public enum EstadoMatricula {
        ACTIVA, RETIRADA, TRASLADADA
    }

    @Column(name = "id_apoderado", nullable = false)
    private Long idApoderado;
}