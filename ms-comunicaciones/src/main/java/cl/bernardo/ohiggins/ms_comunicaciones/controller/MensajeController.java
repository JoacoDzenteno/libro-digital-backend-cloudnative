package cl.bernardo.ohiggins.ms_comunicaciones.controller;

import cl.bernardo.ohiggins.ms_comunicaciones.model.Mensaje;
import cl.bernardo.ohiggins.ms_comunicaciones.service.MensajeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comunicaciones")
@RequiredArgsConstructor
public class MensajeController {

    private final MensajeService mensajeService;

    @GetMapping
    public ResponseEntity<List<Mensaje>> obtenerTodos() {
        return ResponseEntity.ok(mensajeService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Mensaje> obtenerPorId(@PathVariable Long id) {
        return mensajeService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/destinatario/{idDestinatario}")
    public ResponseEntity<List<Mensaje>> obtenerPorDestinatario(@PathVariable Long idDestinatario) {
        return ResponseEntity.ok(mensajeService.obtenerPorDestinatario(idDestinatario));
    }

    @GetMapping("/destinatario/{idDestinatario}/no-leidos")
    public ResponseEntity<List<Mensaje>> obtenerNoLeidos(@PathVariable Long idDestinatario) {
        return ResponseEntity.ok(mensajeService.obtenerNoLeidos(idDestinatario));
    }

    @GetMapping("/destinatario/{idDestinatario}/tipo/{tipo}")
    public ResponseEntity<List<Mensaje>> obtenerPorTipo(
            @PathVariable Long idDestinatario,
            @PathVariable Mensaje.TipoMensaje tipo) {
        return ResponseEntity.ok(mensajeService.obtenerPorTipo(idDestinatario, tipo));
    }

    @PostMapping
    public ResponseEntity<Mensaje> enviar(@RequestBody Mensaje mensaje) {
        return mensajeService.enviar(mensaje)
                .map(m -> ResponseEntity.status(HttpStatus.CREATED).body(m))
                .orElse(ResponseEntity.status(HttpStatus.CONFLICT).build());
    }

    @PatchMapping("/{id}/leer")
    public ResponseEntity<Mensaje> marcarComoLeido(@PathVariable Long id) {
        return mensajeService.marcarComoLeido(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (mensajeService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}