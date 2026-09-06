package cl.bernardo.ohiggins.ms_academico.controller;

import cl.bernardo.ohiggins.ms_academico.model.Asignatura;
import cl.bernardo.ohiggins.ms_academico.service.AsignaturaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/academico/asignaturas")
@RequiredArgsConstructor
public class AsignaturaController {

    private final AsignaturaService asignaturaService;

    @GetMapping
    public ResponseEntity<List<Asignatura>> obtenerTodas() {
        return ResponseEntity.ok(asignaturaService.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Asignatura> obtenerPorId(@PathVariable Long id) {
        return asignaturaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Asignatura> crear(@RequestBody Asignatura asignatura) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(asignaturaService.crear(asignatura));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Asignatura> actualizar(@PathVariable Long id,
                                                  @RequestBody Asignatura asignatura) {
        return asignaturaService.actualizar(id, asignatura)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (asignaturaService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Asignatura>> buscar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String codigo) {
        return ResponseEntity.ok(asignaturaService.buscar(nombre, codigo));
    }
}