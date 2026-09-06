package cl.bernardo.ohiggins.ms_reportes.controller;

import cl.bernardo.ohiggins.ms_reportes.model.Reporte;
import cl.bernardo.ohiggins.ms_reportes.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping
    public ResponseEntity<List<Reporte>> obtenerTodos() {
        return ResponseEntity.ok(reporteService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reporte> obtenerPorId(@PathVariable Long id) {
        return reporteService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Reporte>> obtenerPorTipo(@PathVariable Reporte.TipoReporte tipo) {
        return ResponseEntity.ok(reporteService.obtenerPorTipo(tipo));
    }

    @GetMapping("/referencia/{idReferencia}")
    public ResponseEntity<List<Reporte>> obtenerPorReferencia(@PathVariable Long idReferencia) {
        return ResponseEntity.ok(reporteService.obtenerPorReferencia(idReferencia));
    }

    @GetMapping("/generador/{idGeneradoPor}")
    public ResponseEntity<List<Reporte>> obtenerPorGenerador(@PathVariable Long idGeneradoPor) {
        return ResponseEntity.ok(reporteService.obtenerPorGenerador(idGeneradoPor));
    }

    @PostMapping
    public ResponseEntity<Reporte> generar(@RequestBody Reporte reporte) {
        return reporteService.generar(reporte)
                .map(r -> ResponseEntity.status(HttpStatus.CREATED).body(r))
                .orElse(ResponseEntity.status(HttpStatus.CONFLICT).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (reporteService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}