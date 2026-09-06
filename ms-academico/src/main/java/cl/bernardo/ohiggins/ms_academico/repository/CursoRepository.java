package cl.bernardo.ohiggins.ms_academico.repository;

import cl.bernardo.ohiggins.ms_academico.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CursoRepository extends JpaRepository<Curso, Long> {
    List<Curso> findByNivel(String nivel);
    List<Curso> findByAnioEscolar(Integer anioEscolar);

    @Query("SELECT c FROM Curso c WHERE " +
           "(:nivel IS NULL OR c.nivel = CAST(:nivel AS string)) AND " +
           "(:anio IS NULL OR c.anioEscolar = :anio)")
    List<Curso> buscarCombinado(@Param("nivel") String nivel, @Param("anio") Integer anio);
}