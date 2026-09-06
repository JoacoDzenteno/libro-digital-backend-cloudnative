package cl.bernardo.ohiggins.ms_academico.controller;

import cl.bernardo.ohiggins.ms_academico.model.CargaHoraria;
import cl.bernardo.ohiggins.ms_academico.service.CargaHorariaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/academico/cargas")
@RequiredArgsConstructor
public class CargaHorariaController {

    private final CargaHorariaService cargaHorariaService;

    @GetMapping
    public ResponseEntity<List<CargaHoraria>> obtenerTodas() {
        return ResponseEntity.ok(cargaHorariaService.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CargaHoraria> obtenerPorId(@PathVariable Long id) {
        return cargaHorariaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/curso/{cursoId}")
    public ResponseEntity<List<CargaHoraria>> obtenerPorCurso(@PathVariable Long cursoId) {
        return ResponseEntity.ok(cargaHorariaService.obtenerPorCurso(cursoId));
    }

    @GetMapping("/profesor/{idProfesor}")
    public ResponseEntity<List<CargaHoraria>> obtenerPorProfesor(@PathVariable Long idProfesor) {
        return ResponseEntity.ok(cargaHorariaService.obtenerPorProfesor(idProfesor));
    }

    @PostMapping
    public ResponseEntity<CargaHoraria> crear(@RequestBody CargaHoraria cargaHoraria) {
        return cargaHorariaService.crear(cargaHoraria)
                .map(c -> ResponseEntity.status(HttpStatus.CREATED).body(c))
                .orElse(ResponseEntity.badRequest().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (cargaHorariaService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CargaHoraria> actualizar(@PathVariable Long id,
                                                    @RequestBody CargaHoraria cargaHoraria) {
        return cargaHorariaService.actualizar(id, cargaHoraria)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }
}