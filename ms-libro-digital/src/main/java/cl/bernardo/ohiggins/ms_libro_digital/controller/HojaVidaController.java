package cl.bernardo.ohiggins.ms_libro_digital.controller;

import cl.bernardo.ohiggins.ms_libro_digital.model.HojaVida;
import cl.bernardo.ohiggins.ms_libro_digital.service.HojaVidaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/libro/hojavida")
@RequiredArgsConstructor
public class HojaVidaController {

    private final HojaVidaService hojaVidaService;

    @GetMapping
    public ResponseEntity<List<HojaVida>> obtenerTodas() {
        return ResponseEntity.ok(hojaVidaService.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HojaVida> obtenerPorId(@PathVariable Long id) {
        return hojaVidaService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/estudiante/{idEstudiante}")
    public ResponseEntity<List<HojaVida>> obtenerPorEstudiante(@PathVariable Long idEstudiante) {
        return ResponseEntity.ok(hojaVidaService.obtenerPorEstudiante(idEstudiante));
    }

    @GetMapping("/estudiante/{idEstudiante}/tipo/{tipo}")
    public ResponseEntity<List<HojaVida>> obtenerPorEstudianteYTipo(
            @PathVariable Long idEstudiante,
            @PathVariable HojaVida.TipoAnotacion tipo) {
        return ResponseEntity.ok(hojaVidaService.obtenerPorEstudianteYTipo(idEstudiante, tipo));
    }

    @PostMapping
    public ResponseEntity<HojaVida> registrar(@RequestBody HojaVida hojaVida) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(hojaVidaService.registrar(hojaVida));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (hojaVidaService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}