package cl.bernardo.ohiggins.ms_matricula.repository;

import cl.bernardo.ohiggins.ms_matricula.model.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatriculaRepository extends JpaRepository<Matricula, Long> {
    List<Matricula> findByIdEstudiante(Long idEstudiante);
    List<Matricula> findByIdCurso(Long idCurso);
    List<Matricula> findByAnioEscolar(Integer anioEscolar);
    Optional<Matricula> findByIdEstudianteAndAnioEscolar(Long idEstudiante, Integer anioEscolar);
    List<Matricula> findByEstado(Matricula.EstadoMatricula estado);
    List<Matricula> findByIdApoderado(Long idApoderado);
}