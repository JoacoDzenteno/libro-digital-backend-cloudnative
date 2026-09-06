package cl.bernardo.ohiggins.ms_academico.service;

import cl.bernardo.ohiggins.ms_academico.model.Asignatura;
import cl.bernardo.ohiggins.ms_academico.repository.AsignaturaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AsignaturaService {

    private final AsignaturaRepository asignaturaRepository;

    public List<Asignatura> obtenerTodas() {
        return asignaturaRepository.findAll();
    }

    public Optional<Asignatura> obtenerPorId(Long id) {
        return asignaturaRepository.findById(id);
    }

    public Asignatura crear(Asignatura asignatura) {
        return asignaturaRepository.save(asignatura);
    }

    public Optional<Asignatura> actualizar(Long id, Asignatura asignaturaActualizada) {
        return asignaturaRepository.findById(id).map(a -> {
            a.setNombre(asignaturaActualizada.getNombre());
            a.setCodigo(asignaturaActualizada.getCodigo());
            a.setDescripcion(asignaturaActualizada.getDescripcion());
            return asignaturaRepository.save(a);
        });
    }

    public boolean eliminar(Long id) {
        if (asignaturaRepository.existsById(id)) {
            asignaturaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Asignatura> buscar(String nombre, String codigo) {
        String n = (nombre != null && !nombre.isBlank()) ? nombre.trim() : null;
        String c = (codigo != null && !codigo.isBlank()) ? codigo.trim() : null;
        return asignaturaRepository.buscarCombinado(n, c);
    }
}