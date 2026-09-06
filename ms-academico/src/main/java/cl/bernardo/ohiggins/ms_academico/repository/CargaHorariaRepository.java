package cl.bernardo.ohiggins.ms_academico.repository;

import cl.bernardo.ohiggins.ms_academico.model.CargaHoraria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CargaHorariaRepository extends JpaRepository<CargaHoraria, Long> {
    List<CargaHoraria> findByCursoId(Long cursoId);
    List<CargaHoraria> findByIdProfesor(Long idProfesor);
    List<CargaHoraria> findByAsignaturaId(Long asignaturaId);
}