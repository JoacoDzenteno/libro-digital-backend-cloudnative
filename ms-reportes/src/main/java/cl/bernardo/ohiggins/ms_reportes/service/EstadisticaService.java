package cl.bernardo.ohiggins.ms_reportes.service;

import cl.bernardo.ohiggins.ms_reportes.model.EstadisticaEstudiante;
import cl.bernardo.ohiggins.ms_reportes.repository.EstadisticaEstudianteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EstadisticaService {

    private final EstadisticaEstudianteRepository repository;

    @Transactional
    public void registrarCalificacion(Long idEstudiante, double nota) {
        EstadisticaEstudiante e = obtenerOCrear(idEstudiante);
        e.setSumaNotas(e.getSumaNotas() + nota);
        e.setCantidadNotas(e.getCantidadNotas() + 1);
        repository.save(e);
    }

    @Transactional
    public void registrarAsistencia(Long idEstudiante, String estado) {
        EstadisticaEstudiante e = obtenerOCrear(idEstudiante);
        e.setTotalAsistencias(e.getTotalAsistencias() + 1);
        // Se considera que asistió si estuvo PRESENTE o llegó con TARDANZA.
        if ("PRESENTE".equalsIgnoreCase(estado) || "TARDANZA".equalsIgnoreCase(estado)) {
            e.setAsistenciasPresentes(e.getAsistenciasPresentes() + 1);
        }
        repository.save(e);
    }

    private EstadisticaEstudiante obtenerOCrear(Long idEstudiante) {
        return repository.findById(idEstudiante).orElseGet(() -> {
            EstadisticaEstudiante nueva = new EstadisticaEstudiante();
            nueva.setIdEstudiante(idEstudiante);
            return nueva;
        });
    }
}