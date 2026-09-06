package cl.bernardo.ohiggins.ms_academico.controller;

import cl.bernardo.ohiggins.ms_academico.model.Curso;
import cl.bernardo.ohiggins.ms_academico.service.CursoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/academico/cursos")
@RequiredArgsConstructor
public class CursoController {

    private final CursoService cursoService;

    @GetMapping
    public ResponseEntity<List<Curso>> obtenerTodos() {
        return ResponseEntity.ok(cursoService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Curso> obtenerPorId(@PathVariable Long id) {
        return cursoService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/nivel/{nivel}")
    public ResponseEntity<List<Curso>> obtenerPorNivel(@PathVariable String nivel) {
        return ResponseEntity.ok(cursoService.obtenerPorNivel(nivel));
    }

    @GetMapping("/anio/{anio}")
    public ResponseEntity<List<Curso>> obtenerPorAnio(@PathVariable Integer anio) {
        return ResponseEntity.ok(cursoService.obtenerPorAnio(anio));
    }

    @PostMapping
    public ResponseEntity<Curso> crear(@RequestBody Curso curso) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cursoService.crear(curso));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Curso> actualizar(@PathVariable Long id,
                                             @RequestBody Curso curso) {
        return cursoService.actualizar(id, curso)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/incrementar-alumnos")
    public ResponseEntity<Curso> incrementarAlumnos(@PathVariable Long id) {
        return cursoService.incrementarAlumnos(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.CONFLICT).build());
    }

    @PostMapping("/{id}/decrementar-alumnos")
    public ResponseEntity<Curso> decrementarAlumnos(@PathVariable Long id) {
        return cursoService.decrementarAlumnos(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (cursoService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/buscar")
    public ResponseEntity<List<Curso>> buscar(
            @RequestParam(required = false) String nivel,
            @RequestParam(required = false) Integer anio) {
        return ResponseEntity.ok(cursoService.buscar(nivel, anio));
    }
}