package cl.bernardo.ohiggins.ms_matricula.service;

import cl.bernardo.ohiggins.ms_matricula.model.Matricula;
import cl.bernardo.ohiggins.ms_matricula.repository.MatriculaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final UsuarioClientService usuarioClientService;
    private final AcademicoClientService academicoClientService;

    public List<Matricula> obtenerTodas() {
        return matriculaRepository.findAll();
    }

    public Optional<Matricula> obtenerPorId(Long id) {
        return matriculaRepository.findById(id);
    }

    public List<Matricula> obtenerPorEstudiante(Long idEstudiante) {
        return matriculaRepository.findByIdEstudiante(idEstudiante);
    }

    public List<Matricula> obtenerPorCurso(Long idCurso) {
        return matriculaRepository.findByIdCurso(idCurso);
    }

    public List<Matricula> obtenerPorAnio(Integer anioEscolar) {
        return matriculaRepository.findByAnioEscolar(anioEscolar);
    }

    public Optional<Matricula> crear(Matricula matricula) {
        // Verificar via WebClient que el estudiante existe en ms-usuarios
        if (!usuarioClientService.existeUsuario(matricula.getIdEstudiante())) {
            return Optional.empty();
        }

        // Verificar via WebClient que el apoderado existe en ms-usuarios
        if (!usuarioClientService.existeUsuario(matricula.getIdApoderado())) {
            return Optional.empty();
        }

        // Verificar si el estudiante ya tiene matrícula activa en el mismo año
        Optional<Matricula> matriculaExistente = matriculaRepository
                .findByIdEstudianteAndAnioEscolar(
                        matricula.getIdEstudiante(),
                        matricula.getAnioEscolar()
                );

        if (matriculaExistente.isPresent() &&
            matriculaExistente.get().getEstado() == Matricula.EstadoMatricula.ACTIVA) {
            return Optional.empty();
        }

        // Verificar capacidad del curso e incrementar contador en ms-academico
        boolean incrementado = academicoClientService.incrementarAlumnos(matricula.getIdCurso());
        if (!incrementado) {
            return Optional.empty();
        }

        matricula.setFechaMatricula(LocalDate.now());
        matricula.setEstado(Matricula.EstadoMatricula.ACTIVA);
        return Optional.of(matriculaRepository.save(matricula));
    }

    public Optional<Matricula> cambiarEstado(Long id, Matricula.EstadoMatricula estado) {
        return matriculaRepository.findById(id).map(m -> {
            m.setEstado(estado);
            return matriculaRepository.save(m);
        });
    }

    public boolean eliminar(Long id) {
        return matriculaRepository.findById(id).map(m -> {
            matriculaRepository.deleteById(id);
            academicoClientService.decrementarAlumnos(m.getIdCurso());
            return true;
        }).orElse(false);
    }

        public List<Matricula> obtenerPorApoderado(Long idApoderado) {
        return matriculaRepository.findByIdApoderado(idApoderado);
    }
}