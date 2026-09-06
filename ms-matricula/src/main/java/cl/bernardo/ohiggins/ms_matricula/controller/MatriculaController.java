package cl.bernardo.ohiggins.ms_matricula.controller;

import cl.bernardo.ohiggins.ms_matricula.model.Matricula;
import cl.bernardo.ohiggins.ms_matricula.service.MatriculaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matricula")
@RequiredArgsConstructor
public class MatriculaController {

    private final MatriculaService matriculaService;

    @GetMapping
    public ResponseEntity<List<Matricula>> obtenerTodas() {
        return ResponseEntity.ok(matriculaService.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Matricula> obtenerPorId(@PathVariable Long id) {
        return matriculaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/estudiante/{idEstudiante}")
    public ResponseEntity<List<Matricula>> obtenerPorEstudiante(@PathVariable Long idEstudiante) {
        return ResponseEntity.ok(matriculaService.obtenerPorEstudiante(idEstudiante));
    }

    @GetMapping("/curso/{idCurso}")
    public ResponseEntity<List<Matricula>> obtenerPorCurso(@PathVariable Long idCurso) {
        return ResponseEntity.ok(matriculaService.obtenerPorCurso(idCurso));
    }

    @GetMapping("/anio/{anioEscolar}")
    public ResponseEntity<List<Matricula>> obtenerPorAnio(@PathVariable Integer anioEscolar) {
        return ResponseEntity.ok(matriculaService.obtenerPorAnio(anioEscolar));
    }

    @PostMapping
    public ResponseEntity<Matricula> crear(@RequestBody Matricula matricula) {
        return matriculaService.crear(matricula)
                .map(m -> ResponseEntity.status(HttpStatus.CREATED).body(m))
                .orElse(ResponseEntity.status(HttpStatus.CONFLICT).build());
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<Matricula> cambiarEstado(@PathVariable Long id,
                                                    @RequestParam Matricula.EstadoMatricula estado) {
        return matriculaService.cambiarEstado(id, estado)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (matriculaService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/apoderado/{idApoderado}")
    public ResponseEntity<List<Matricula>> obtenerPorApoderado(@PathVariable Long idApoderado) {
        return ResponseEntity.ok(matriculaService.obtenerPorApoderado(idApoderado));
    }
}