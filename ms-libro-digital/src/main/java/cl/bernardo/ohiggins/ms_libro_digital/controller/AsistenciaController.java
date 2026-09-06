package cl.bernardo.ohiggins.ms_libro_digital.controller;

import cl.bernardo.ohiggins.ms_libro_digital.model.Asistencia;
import cl.bernardo.ohiggins.ms_libro_digital.service.AsistenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/libro/asistencias")
@RequiredArgsConstructor
public class AsistenciaController {

    private final AsistenciaService asistenciaService;

    @GetMapping
    public ResponseEntity<List<Asistencia>> obtenerTodas() {
        return ResponseEntity.ok(asistenciaService.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Asistencia> obtenerPorId(@PathVariable Long id) {
        return asistenciaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/estudiante/{idEstudiante}")
    public ResponseEntity<List<Asistencia>> obtenerPorEstudiante(@PathVariable Long idEstudiante) {
        return ResponseEntity.ok(asistenciaService.obtenerPorEstudiante(idEstudiante));
    }

    @GetMapping("/curso/{idCurso}/fecha/{fecha}")
    public ResponseEntity<List<Asistencia>> obtenerPorCursoYFecha(
            @PathVariable Long idCurso,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(asistenciaService.obtenerPorCursoYFecha(idCurso, fecha));
    }

    @PostMapping
    public ResponseEntity<Asistencia> registrar(@RequestBody Asistencia asistencia) {
        return asistenciaService.registrar(asistencia)
                .map(a -> ResponseEntity.status(HttpStatus.CREATED).body(a))
                .orElse(ResponseEntity.status(HttpStatus.CONFLICT).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Asistencia> actualizar(@PathVariable Long id,
                                                  @RequestBody Asistencia asistencia) {
        return asistenciaService.actualizar(id, asistencia)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (asistenciaService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}