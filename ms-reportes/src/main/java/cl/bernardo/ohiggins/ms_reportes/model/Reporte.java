package cl.bernardo.ohiggins.ms_reportes.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reporte")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoReporte tipo;

    @Column(name = "id_referencia", nullable = false)
    private Long idReferencia;

    @Column(columnDefinition = "TEXT")
    private String contenido;

    @Column(name = "fecha_generacion", nullable = false)
    private LocalDateTime fechaGeneracion;

    @Column(name = "id_generado_por", nullable = false)
    private Long idGeneradoPor;

    public enum TipoReporte {
        ASISTENCIA, CALIFICACIONES, CONDUCTA, ACADEMICO_GENERAL
    }
}