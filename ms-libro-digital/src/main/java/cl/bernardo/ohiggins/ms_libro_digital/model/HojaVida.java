package cl.bernardo.ohiggins.ms_libro_digital.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "hoja_vida")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HojaVida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_estudiante", nullable = false)
    private Long idEstudiante;

    @Column(name = "id_profesor", nullable = false)
    private Long idProfesor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAnotacion tipo;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private LocalDate fecha;

    public enum TipoAnotacion {
        POSITIVA, NEGATIVA, NEUTRA
    }
}