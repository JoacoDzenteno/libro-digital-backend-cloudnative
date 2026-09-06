package cl.bernardo.ohiggins.ms_libro_digital.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "calificacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Calificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_estudiante", nullable = false)
    private Long idEstudiante;

    @Column(name = "id_asignatura", nullable = false)
    private Long idAsignatura;

    @Column(name = "id_curso", nullable = false)
    private Long idCurso;

    @Column(nullable = false)
    private Double nota;

    @Column(nullable = false)
    private String periodo;

    @Column(nullable = false)
    private LocalDate fecha;

    private String descripcion;
}