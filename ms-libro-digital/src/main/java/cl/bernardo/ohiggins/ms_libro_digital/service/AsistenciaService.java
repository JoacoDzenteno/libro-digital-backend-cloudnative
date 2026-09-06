package cl.bernardo.ohiggins.ms_libro_digital.service;

import cl.bernardo.ohiggins.ms_libro_digital.dto.NotificacionEvent;
import cl.bernardo.ohiggins.ms_libro_digital.model.Asistencia;
import cl.bernardo.ohiggins.ms_libro_digital.repository.AsistenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AsistenciaService {

    private final AsistenciaRepository asistenciaRepository;
    private final AcademicoClientService academicoClientService;
    private final NotificacionPublisher notificacionPublisher;

    public List<Asistencia> obtenerTodas() {
        return asistenciaRepository.findAll();
    }

    public Optional<Asistencia> obtenerPorId(Long id) {
        return asistenciaRepository.findById(id);
    }

    public List<Asistencia> obtenerPorEstudiante(Long idEstudiante) {
        return asistenciaRepository.findByIdEstudiante(idEstudiante);
    }

    public List<Asistencia> obtenerPorCursoYFecha(Long idCurso, LocalDate fecha) {
        return asistenciaRepository.findByIdCursoAndFecha(idCurso, fecha);
    }

    public Optional<Asistencia> registrar(Asistencia asistencia) {
        if (!academicoClientService.existeCurso(asistencia.getIdCurso())) {
            return Optional.empty();
        }
        if (asistencia.getFecha() == null) {
            asistencia.setFecha(LocalDate.now());
        }
        Asistencia guardada = asistenciaRepository.save(asistencia);

        notificacionPublisher.publicarNotificacion(NotificacionEvent.builder()
                .idEstudiante(guardada.getIdEstudiante())
                .asunto("Nueva asistencia registrada")
                .contenido("Se registró asistencia con estado: " + guardada.getEstado())
                .tipoEvento("ASISTENCIA")
                .build());

        return Optional.of(guardada);
    }

    public Optional<Asistencia> actualizar(Long id, Asistencia asistenciaActualizada) {
        return asistenciaRepository.findById(id).map(a -> {
            a.setEstado(asistenciaActualizada.getEstado());
            a.setObservacion(asistenciaActualizada.getObservacion());
            return asistenciaRepository.save(a);
        });
    }

    public boolean eliminar(Long id) {
        if (asistenciaRepository.existsById(id)) {
            asistenciaRepository.deleteById(id);
            return true;
        }
        return false;
    }
}