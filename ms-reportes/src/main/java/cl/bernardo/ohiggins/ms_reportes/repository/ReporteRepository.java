package cl.bernardo.ohiggins.ms_reportes.repository;

import cl.bernardo.ohiggins.ms_reportes.model.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {
    List<Reporte> findByTipo(Reporte.TipoReporte tipo);
    List<Reporte> findByIdReferencia(Long idReferencia);
    List<Reporte> findByIdGeneradoPor(Long idGeneradoPor);
}