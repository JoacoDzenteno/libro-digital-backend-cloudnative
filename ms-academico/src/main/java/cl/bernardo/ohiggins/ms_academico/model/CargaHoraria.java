package cl.bernardo.ohiggins.ms_academico.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "carga_horaria")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CargaHoraria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    @ManyToOne
    @JoinColumn(name = "asignatura_id", nullable = false)
    private Asignatura asignatura;

    @Column(name = "id_profesor", nullable = false)
    private Long idProfesor;

    @Column(name = "horas_semanales")
    private Integer horasSemanales;
}
