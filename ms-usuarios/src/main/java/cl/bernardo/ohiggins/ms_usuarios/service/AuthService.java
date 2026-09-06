package cl.bernardo.ohiggins.ms_usuarios.service;

import cl.bernardo.ohiggins.ms_usuarios.dto.LoginRequest;
import cl.bernardo.ohiggins.ms_usuarios.dto.LoginResponse;
import cl.bernardo.ohiggins.ms_usuarios.model.Persona;
import cl.bernardo.ohiggins.ms_usuarios.model.Usuario;
import cl.bernardo.ohiggins.ms_usuarios.repository.UsuarioRepository;
import cl.bernardo.ohiggins.ms_usuarios.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public Optional<LoginResponse> login(LoginRequest request) {
        return usuarioRepository.findByEmail(request.getEmail())
                .filter(u -> passwordEncoder.matches(request.getPassword(), u.getPassword()))
                .map(u -> LoginResponse.builder()
                        .id(u.getId())
                        .token(jwtService.generateToken(u.getEmail(), u.getRol().name()))
                        .email(u.getEmail())
                        .rol(u.getRol().name())
                        .nombre(u.getPersona() != null ? u.getPersona().getNombre() : null)
                        .apellido(u.getPersona() != null ? u.getPersona().getApellido() : null)
                        .build());
    }

    public Usuario registrar(Usuario usuario) {
        // La contraseña temporal es el RUT de la persona
        String passwordTemporal = usuario.getPersona() != null
                ? usuario.getPersona().getRut()
                : usuario.getEmail();
        usuario.setPassword(passwordEncoder.encode(passwordTemporal));
        return usuarioRepository.save(usuario);
    }
}