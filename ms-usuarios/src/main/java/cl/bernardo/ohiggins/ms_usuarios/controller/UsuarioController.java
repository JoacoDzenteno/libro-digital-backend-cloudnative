package cl.bernardo.ohiggins.ms_usuarios.controller;

import cl.bernardo.ohiggins.ms_usuarios.dto.ActualizarPerfilRequest;
import cl.bernardo.ohiggins.ms_usuarios.dto.UsuarioDTO;
import cl.bernardo.ohiggins.ms_usuarios.model.Usuario;
import cl.bernardo.ohiggins.ms_usuarios.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> obtenerTodos() {
        return ResponseEntity.ok(usuarioService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtenerPorId(@PathVariable Long id) {
        return usuarioService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UsuarioDTO> obtenerPorEmail(@PathVariable String email) {
        return usuarioService.obtenerPorEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/rol/{rol}")
    public ResponseEntity<List<UsuarioDTO>> obtenerPorRol(@PathVariable Usuario.Rol rol) {
        return ResponseEntity.ok(usuarioService.obtenerPorRol(rol));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<UsuarioDTO>> buscar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String rut,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Integer limite) {
        return ResponseEntity.ok(usuarioService.buscar(nombre, rut, email, limite));
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> crear(@RequestBody Usuario usuario) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioService.crear(usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizar(@PathVariable Long id,
                                                  @RequestBody Usuario usuario) {
        return usuarioService.actualizar(id, usuario)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (usuarioService.eliminar(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/perfil")
    public ResponseEntity<UsuarioDTO> actualizarPerfil(@PathVariable Long id,
                                                        @RequestBody ActualizarPerfilRequest request) {
        return usuarioService.actualizarPerfil(id, request.getNuevoEmail(), request.getNuevaPassword())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}