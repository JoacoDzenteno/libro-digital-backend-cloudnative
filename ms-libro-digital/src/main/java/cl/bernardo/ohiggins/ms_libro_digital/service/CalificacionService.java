package cl.bernardo.ohiggins.ms_libro_digital.service;

import cl.bernardo.ohiggins.ms_libro_digital.dto.NotificacionEvent;
import cl.bernardo.ohiggins.ms_libro_digital.dto.ReporteEvent;
import cl.bernardo.ohiggins.ms_libro_digital.model.Calificacion;
import cl.bernardo.ohiggins.ms_libro_digital.repository.CalificacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CalificacionService {

    private final CalificacionRepository calificacionRepository;
    private final NotificacionPublisher notificacionPublisher;

    public List<Calificacion> obtenerTodas() {
        return calificacionRepository.findAll();
    }

    public Optional<Calificacion> obtenerPorId(Long id) {
        return calificacionRepository.findById(id);
    }

    public List<Calificacion> obtenerPorEstudiante(Long idEstudiante) {
        return calificacionRepository.findByIdEstudiante(idEstudiante);
    }

    public List<Calificacion> obtenerPorEstudianteYAsignatura(Long idEstudiante, Long idAsignatura) {
        return calificacionRepository.findByIdEstudianteAndIdAsignatura(idEstudiante, idAsignatura);
    }

    public List<Calificacion> obtenerPorCursoYAsignatura(Long idCurso, Long idAsignatura) {
        return calificacionRepository.findByIdCursoAndIdAsignatura(idCurso, idAsignatura);
    }

    public Calificacion registrar(Calificacion calificacion) {
        if (calificacion.getFecha() == null) {
            calificacion.setFecha(LocalDate.now());
        }
        Calificacion guardada = calificacionRepository.save(calificacion);

        notificacionPublisher.publicarNotificacion(NotificacionEvent.builder()
                .idEstudiante(guardada.getIdEstudiante())
                .asunto("Nueva calificación registrada")
                .contenido("Se registró una nota de " + guardada.getNota() + " en el período " + guardada.getPeriodo())
                .tipoEvento("CALIFICACION")
                .build());
        
        notificacionPublisher.publicarReporte(ReporteEvent.builder()
                .idEstudiante(guardada.getIdEstudiante())
                .idGeneradoPor(1L) // admin@colegio.cl — Calificacion no registra qué profesor la creó, se usa el admin como generador del sistema
                .contenido("Calificación " + guardada.getNota() + " registrada en el período " + guardada.getPeriodo())
                .build());

        return guardada;
    }

    public Optional<Calificacion> actualizar(Long id, Calificacion calificacionActualizada) {
        return calificacionRepository.findById(id).map(c -> {
            c.setNota(calificacionActualizada.getNota());
            c.setDescripcion(calificacionActualizada.getDescripcion());
            c.setPeriodo(calificacionActualizada.getPeriodo());
            return calificacionRepository.save(c);
        });
    }

    public boolean eliminar(Long id) {
        if (calificacionRepository.existsById(id)) {
            calificacionRepository.deleteById(id);
            return true;
        }
        return false;
    }
}