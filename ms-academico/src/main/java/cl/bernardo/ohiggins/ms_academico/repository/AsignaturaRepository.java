package cl.bernardo.ohiggins.ms_academico.repository;

import cl.bernardo.ohiggins.ms_academico.model.Asignatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AsignaturaRepository extends JpaRepository<Asignatura, Long> {
    Optional<Asignatura> findByCodigo(String codigo);

    @Query("SELECT a FROM Asignatura a WHERE " +
           "(:nombre IS NULL OR LOWER(a.nombre) LIKE LOWER(CONCAT('%', CAST(:nombre AS string), '%'))) AND " +
           "(:codigo IS NULL OR LOWER(a.codigo) LIKE LOWER(CONCAT('%', CAST(:codigo AS string), '%')))")
    List<Asignatura> buscarCombinado(@Param("nombre") String nombre, @Param("codigo") String codigo);
}