package cl.bernardo.ohiggins.ms_reportes.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "estadisticas_estudiante")
@Getter
@Setter
@NoArgsConstructor
public class EstadisticaEstudiante {

    @Id
    private Long idEstudiante;

    // Control de concurrencia: si dos listeners actualizan la misma fila a la vez,
    // uno falla y el manejador de errores de Kafka lo reintenta.
    @Version
    private Long version;

    private double sumaNotas;
    private int cantidadNotas;
    private int totalAsistencias;
    private int asistenciasPresentes;

    public double promedioNotas() {
        return cantidadNotas == 0 ? 0.0 : sumaNotas / cantidadNotas;
    }

    public double porcentajeAsistencia() {
        return totalAsistencias == 0 ? 0.0 : (asistenciasPresentes * 100.0) / totalAsistencias;
    }
}