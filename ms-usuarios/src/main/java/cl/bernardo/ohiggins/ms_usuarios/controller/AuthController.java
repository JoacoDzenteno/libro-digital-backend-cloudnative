package cl.bernardo.ohiggins.ms_usuarios.controller;

import cl.bernardo.ohiggins.ms_usuarios.dto.LoginRequest;
import cl.bernardo.ohiggins.ms_usuarios.dto.LoginResponse;
import cl.bernardo.ohiggins.ms_usuarios.model.Usuario;
import cl.bernardo.ohiggins.ms_usuarios.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return authService.login(request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    // Solo el admin puede crear usuarios desde el frontend
    @PostMapping("/register")
    public ResponseEntity<Usuario> registrar(@RequestBody Usuario usuario) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.registrar(usuario));
    }
}