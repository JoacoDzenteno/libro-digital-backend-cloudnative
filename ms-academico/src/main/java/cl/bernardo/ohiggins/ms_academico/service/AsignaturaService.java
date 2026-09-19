package cl.bernardo.ohiggins.ms_academico.service;

import cl.bernardo.ohiggins.ms_academico.model.Asignatura;
import cl.bernardo.ohiggins.ms_academico.repository.AsignaturaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AsignaturaService {

    private final AsignaturaRepository asignaturaRepository;

    @Transactional(readOnly = true)
    public List<Asignatura> obtenerTodas() {
        return asignaturaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Asignatura> obtenerPorId(Long id) {
        return asignaturaRepository.findById(id);
    }

    @Transactional
    public Asignatura crear(Asignatura asignatura) {
        return asignaturaRepository.save(asignatura);
    }

    @Transactional
    public Optional<Asignatura> actualizar(Long id, Asignatura asignaturaActualizada) {
        return asignaturaRepository.findById(id).map(a -> {
            if (asignaturaActualizada.getNombre() != null && !asignaturaActualizada.getNombre().isBlank()) {
                a.setNombre(asignaturaActualizada.getNombre());
            }
            if (asignaturaActualizada.getCodigo() != null && !asignaturaActualizada.getCodigo().isBlank()) {
                a.setCodigo(asignaturaActualizada.getCodigo());
            }
            if (asignaturaActualizada.getDescripcion() != null) {
                a.setDescripcion(asignaturaActualizada.getDescripcion());
            }
            return asignaturaRepository.save(a);
        });
    }

    @Transactional
    public boolean eliminar(Long id) {
        if (asignaturaRepository.existsById(id)) {
            asignaturaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public List<Asignatura> buscar(String nombre, String codigo) {
        String n = (nombre != null && !nombre.isBlank()) ? nombre.trim() : null;
        String c = (codigo != null && !codigo.isBlank()) ? codigo.trim() : null;
        return asignaturaRepository.buscarCombinado(n, c);
    }
}