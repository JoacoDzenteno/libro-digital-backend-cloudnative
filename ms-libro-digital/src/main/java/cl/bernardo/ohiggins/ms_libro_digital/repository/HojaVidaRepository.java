package cl.bernardo.ohiggins.ms_libro_digital.repository;

import cl.bernardo.ohiggins.ms_libro_digital.model.HojaVida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HojaVidaRepository extends JpaRepository<HojaVida, Long> {
    List<HojaVida> findByIdEstudiante(Long idEstudiante);
    List<HojaVida> findByIdEstudianteAndTipo(Long idEstudiante, HojaVida.TipoAnotacion tipo);
    List<HojaVida> findByIdProfesor(Long idProfesor);
}