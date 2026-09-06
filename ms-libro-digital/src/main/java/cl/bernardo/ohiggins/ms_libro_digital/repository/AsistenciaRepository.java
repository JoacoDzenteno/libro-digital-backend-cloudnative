package cl.bernardo.ohiggins.ms_libro_digital.repository;

import cl.bernardo.ohiggins.ms_libro_digital.model.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {
    List<Asistencia> findByIdEstudiante(Long idEstudiante);
    List<Asistencia> findByIdCurso(Long idCurso);
    List<Asistencia> findByIdCursoAndFecha(Long idCurso, LocalDate fecha);
    List<Asistencia> findByIdEstudianteAndEstado(Long idEstudiante, Asistencia.EstadoAsistencia estado);
}