package cl.bernardo.ohiggins.ms_academico.service;

import cl.bernardo.ohiggins.ms_academico.model.Curso;
import cl.bernardo.ohiggins.ms_academico.repository.CursoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CursoService {

    private final CursoRepository cursoRepository;

    public List<Curso> obtenerTodos() {
        return cursoRepository.findAll();
    }

    public Optional<Curso> obtenerPorId(Long id) {
        return cursoRepository.findById(id);
    }

    public List<Curso> obtenerPorNivel(String nivel) {
        return cursoRepository.findByNivel(nivel);
    }

    public List<Curso> obtenerPorAnio(Integer anio) {
        return cursoRepository.findByAnioEscolar(anio);
    }

    public Curso crear(Curso curso) {
        return cursoRepository.save(curso);
    }

    public Optional<Curso> actualizar(Long id, Curso cursoActualizado) {
        return cursoRepository.findById(id).map(c -> {
            c.setNombre(cursoActualizado.getNombre());
            c.setNivel(cursoActualizado.getNivel());
            c.setLetra(cursoActualizado.getLetra());
            c.setAnioEscolar(cursoActualizado.getAnioEscolar());
            return cursoRepository.save(c);
        });
    }

    public Optional<Curso> incrementarAlumnos(Long id) {
        return cursoRepository.findById(id).flatMap(c -> {
            if (c.getCantidadAlumnos() >= c.getCapacidadMaxima()) {
                return Optional.empty();
            }
            c.setCantidadAlumnos(c.getCantidadAlumnos() + 1);
            return Optional.of(cursoRepository.save(c));
        });
    }

    public Optional<Curso> decrementarAlumnos(Long id) {
        return cursoRepository.findById(id).map(c -> {
            if (c.getCantidadAlumnos() > 0) {
                c.setCantidadAlumnos(c.getCantidadAlumnos() - 1);
            }
            return cursoRepository.save(c);
        });
    }

    public boolean eliminar(Long id) {
        if (cursoRepository.existsById(id)) {
            cursoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Curso> buscar(String nivel, Integer anio) {
        String n = (nivel != null && !nivel.isBlank()) ? nivel.trim() : null;
            return cursoRepository.buscarCombinado(n, anio);
    }   
}