package cl.bernardo.ohiggins.ms_libro_digital.repository;

import cl.bernardo.ohiggins.ms_libro_digital.model.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, Long> {
    List<Calificacion> findByIdEstudiante(Long idEstudiante);
    List<Calificacion> findByIdEstudianteAndIdAsignatura(Long idEstudiante, Long idAsignatura);
    List<Calificacion> findByIdCursoAndIdAsignatura(Long idCurso, Long idAsignatura);
    List<Calificacion> findByIdEstudianteAndPeriodo(Long idEstudiante, String periodo);
}