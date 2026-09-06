package cl.bernardo.ohiggins.ms_academico.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "curso")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String nivel;

    @Column(nullable = false)
    private String letra;

    @Column(name = "anio_escolar", nullable = false)
    private Integer anioEscolar;

    @Builder.Default
    @Column(name = "cantidad_alumnos", nullable = false)
    private Integer cantidadAlumnos = 0;

    @Column(name = "capacidad_maxima", nullable = false)
    private Integer capacidadMaxima;
}