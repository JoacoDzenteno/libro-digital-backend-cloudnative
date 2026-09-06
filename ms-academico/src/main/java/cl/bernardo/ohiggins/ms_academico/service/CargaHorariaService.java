package cl.bernardo.ohiggins.ms_academico.service;

import cl.bernardo.ohiggins.ms_academico.model.CargaHoraria;
import cl.bernardo.ohiggins.ms_academico.repository.CargaHorariaRepository;
import cl.bernardo.ohiggins.ms_academico.repository.CursoRepository;
import cl.bernardo.ohiggins.ms_academico.repository.AsignaturaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CargaHorariaService {

    private final CargaHorariaRepository cargaHorariaRepository;
    private final CursoRepository cursoRepository;
    private final AsignaturaRepository asignaturaRepository;

    public List<CargaHoraria> obtenerTodas() {
        return cargaHorariaRepository.findAll();
    }

    public Optional<CargaHoraria> obtenerPorId(Long id) {
        return cargaHorariaRepository.findById(id);
    }

    public List<CargaHoraria> obtenerPorCurso(Long cursoId) {
        return cargaHorariaRepository.findByCursoId(cursoId);
    }

    public List<CargaHoraria> obtenerPorProfesor(Long idProfesor) {
        return cargaHorariaRepository.findByIdProfesor(idProfesor);
    }

    public Optional<CargaHoraria> crear(CargaHoraria cargaHoraria) {
        if (cargaHoraria.getCurso() == null || cargaHoraria.getCurso().getId() == null) {
            return Optional.empty();
        }
        if (cargaHoraria.getAsignatura() == null || cargaHoraria.getAsignatura().getId() == null) {
            return Optional.empty();
        }

        Long cursoId = cargaHoraria.getCurso().getId();
        Long asignaturaId = cargaHoraria.getAsignatura().getId();

        boolean cursoExiste = cursoRepository.existsById(cursoId);
        boolean asignaturaExiste = asignaturaRepository.existsById(asignaturaId);

        if (!cursoExiste || !asignaturaExiste) {
            return Optional.empty();
        }
        return Optional.of(cargaHorariaRepository.save(cargaHoraria));
    }

    public Optional<CargaHoraria> actualizar(Long id, CargaHoraria cargaActualizada) {
        if (cargaActualizada.getCurso() == null || cargaActualizada.getCurso().getId() == null) {
            return Optional.empty();
        }
        if (cargaActualizada.getAsignatura() == null || cargaActualizada.getAsignatura().getId() == null) {
            return Optional.empty();
        }

        Long cursoId = cargaActualizada.getCurso().getId();
        Long asignaturaId = cargaActualizada.getAsignatura().getId();

        boolean cursoExiste = cursoRepository.existsById(cursoId);
        boolean asignaturaExiste = asignaturaRepository.existsById(asignaturaId);

        if (!cursoExiste || !asignaturaExiste) {
            return Optional.empty();
        }

        return cargaHorariaRepository.findById(id).map(c -> {
            c.setCurso(cargaActualizada.getCurso());
            c.setAsignatura(cargaActualizada.getAsignatura());
            c.setHorasSemanales(cargaActualizada.getHorasSemanales());
            return cargaHorariaRepository.save(c);
        });
    }

    public boolean eliminar(Long id) {
        if (cargaHorariaRepository.existsById(id)) {
            cargaHorariaRepository.deleteById(id);
            return true;
        }
        return false;
    }
}