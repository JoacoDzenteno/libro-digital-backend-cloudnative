package cl.bernardo.ohiggins.ms_reportes.service;

import cl.bernardo.ohiggins.ms_reportes.model.Reporte;
import cl.bernardo.ohiggins.ms_reportes.repository.ReporteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final ReporteRepository reporteRepository;
    private final UsuarioClientService usuarioClientService;

    public List<Reporte> obtenerTodos() {
        return reporteRepository.findAll();
    }

    public Optional<Reporte> obtenerPorId(Long id) {
        return reporteRepository.findById(id);
    }

    public List<Reporte> obtenerPorTipo(Reporte.TipoReporte tipo) {
        return reporteRepository.findByTipo(tipo);
    }

    public List<Reporte> obtenerPorReferencia(Long idReferencia) {
        return reporteRepository.findByIdReferencia(idReferencia);
    }

    public List<Reporte> obtenerPorGenerador(Long idGeneradoPor) {
        return reporteRepository.findByIdGeneradoPor(idGeneradoPor);
    }

    public Optional<Reporte> generar(Reporte reporte) {
        if (!usuarioClientService.existeUsuario(reporte.getIdGeneradoPor())
                || !usuarioClientService.existeUsuario(reporte.getIdReferencia())) {
            return Optional.empty();
        }
        reporte.setFechaGeneracion(LocalDateTime.now());
        return Optional.of(reporteRepository.save(reporte));
    }

    public boolean eliminar(Long id) {
        if (reporteRepository.existsById(id)) {
            reporteRepository.deleteById(id);
            return true;
        }
        return false;
    }
}