package cl.bernardo.ohiggins.ms_libro_digital.controller;

import cl.bernardo.ohiggins.ms_libro_digital.model.Calificacion;
import cl.bernardo.ohiggins.ms_libro_digital.service.CalificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/libro/calificaciones")
@RequiredArgsConstructor
public class CalificacionController {

    private final CalificacionService calificacionService;

    @GetMapping
    public ResponseEntity<List<Calificacion>> obtenerTodas() {
        return ResponseEntity.ok(calificacionService.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Calificacion> obtenerPorId(@PathVariable Long id) {
        return calificacionService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/estudiante/{idEstudiante}")
    public ResponseEntity<List<Calificacion>> obtenerPorEstudiante(@PathVariable Long idEstudiante) {
        return ResponseEntity.ok(calificacionService.obtenerPorEstudiante(idEstudiante));
    }

    @GetMapping("/estudiante/{idEstudiante}/asignatura/{idAsignatura}")
    public ResponseEntity<List<Calificacion>> obtenerPorEstudianteYAsignatura(
            @PathVariable Long idEstudiante,
            @PathVariable Long idAsignatura) {
        return ResponseEntity.ok(calificacionService.obtenerPorEstudianteYAsignatura(idEstudiante, idAsignatura));
    }

    @GetMapping("/curso/{idCurso}/asignatura/{idAsignatura}")
    public ResponseEntity<List<Calificacion>> obtenerPorCursoYAsignatura(
            @PathVariable Long idCurso,
            @PathVariable Long idAsignatura) {
        return ResponseEntity.ok(calificacionService.obtenerPorCursoYAsignatura(idCurso, idAsignatura));
    }

    @PostMapping
    public ResponseEntity<Calificacion> registrar(@RequestBody Calificacion calificacion) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(calificacionService.registrar(calificacion));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Calificacion> actualizar(@PathVariable Long id,
                                                    @RequestBody Calificacion calificacion) {
        return calificacionService.actualizar(id, calificacion)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (calificacionService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}