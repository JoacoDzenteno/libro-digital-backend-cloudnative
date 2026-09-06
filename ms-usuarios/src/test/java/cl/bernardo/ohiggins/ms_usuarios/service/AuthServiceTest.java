package cl.bernardo.ohiggins.ms_usuarios.service;

import cl.bernardo.ohiggins.ms_usuarios.dto.LoginRequest;
import cl.bernardo.ohiggins.ms_usuarios.dto.LoginResponse;
import cl.bernardo.ohiggins.ms_usuarios.model.Persona;
import cl.bernardo.ohiggins.ms_usuarios.model.Usuario;
import cl.bernardo.ohiggins.ms_usuarios.repository.UsuarioRepository;
import cl.bernardo.ohiggins.ms_usuarios.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private Usuario usuario;
    private Persona persona;

    @BeforeEach
    void setUp() {
        persona = Persona.builder()
                .nombre("Pedro")
                .apellido("Soto")
                .rut("19876543-2")
                .build();

        usuario = Usuario.builder()
                .id(2L)
                .email("pedro@colegio.cl")
                .password("hashedPassword")
                .rol(Usuario.Rol.ESTUDIANTE)
                .persona(persona)
                .build();
    }

    @Test
    void login_credencialesValidas_retornaLoginResponse() {
        LoginRequest request = new LoginRequest("pedro@colegio.cl", "rut-como-clave");

        when(usuarioRepository.findByEmail("pedro@colegio.cl")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("rut-como-clave", "hashedPassword")).thenReturn(true);
        when(jwtService.generateToken("pedro@colegio.cl", "ESTUDIANTE")).thenReturn("token-falso");

        Optional<LoginResponse> resultado = authService.login(request);

        assertTrue(resultado.isPresent());
        assertEquals("token-falso", resultado.get().getToken());
        assertEquals("Pedro", resultado.get().getNombre());
    }

    @Test
    void login_passwordIncorrecta_retornaVacio() {
        LoginRequest request = new LoginRequest("pedro@colegio.cl", "claveMala");

        when(usuarioRepository.findByEmail("pedro@colegio.cl")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("claveMala", "hashedPassword")).thenReturn(false);

        Optional<LoginResponse> resultado = authService.login(request);

        assertTrue(resultado.isEmpty());
        verify(jwtService, never()).generateToken(any(), any());
    }

    @Test
    void login_emailNoExiste_retornaVacio() {
        LoginRequest request = new LoginRequest("noexiste@colegio.cl", "clave");

        when(usuarioRepository.findByEmail("noexiste@colegio.cl")).thenReturn(Optional.empty());

        Optional<LoginResponse> resultado = authService.login(request);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void registrar_usaRutComoPasswordTemporal() {
        Usuario nuevo = Usuario.builder()
                .email("nuevo@colegio.cl")
                .rol(Usuario.Rol.APODERADO)
                .persona(Persona.builder().rut("18765432-1").build())
                .build();

        when(passwordEncoder.encode("18765432-1")).thenReturn("hashRut");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(nuevo);

        authService.registrar(nuevo);

        assertEquals("hashRut", nuevo.getPassword());
        verify(passwordEncoder).encode("18765432-1");
    }

    @Test
    void registrar_sinPersona_usaEmailComoPasswordTemporal() {
        Usuario nuevo = Usuario.builder()
                .email("sinpersona@colegio.cl")
                .rol(Usuario.Rol.ADMINISTRATIVO)
                .build();

        when(passwordEncoder.encode("sinpersona@colegio.cl")).thenReturn("hashEmail");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(nuevo);

        authService.registrar(nuevo);

        verify(passwordEncoder).encode("sinpersona@colegio.cl");
    }
    
    @Test
    void login_usuarioSinPersona_retornaNombreYApellidoNulos() {
        Usuario sinPersona = Usuario.builder()
                .id(9L)
                .email("sinpersona@colegio.cl")
                .password("hashedPassword")
                .rol(Usuario.Rol.ADMINISTRATIVO)
                .build();

        LoginRequest request = new LoginRequest("sinpersona@colegio.cl", "clave");

        when(usuarioRepository.findByEmail("sinpersona@colegio.cl")).thenReturn(Optional.of(sinPersona));
        when(passwordEncoder.matches("clave", "hashedPassword")).thenReturn(true);
        when(jwtService.generateToken("sinpersona@colegio.cl", "ADMINISTRATIVO")).thenReturn("token");

        Optional<LoginResponse> resultado = authService.login(request);

        assertTrue(resultado.isPresent());
        assertNull(resultado.get().getNombre());
        assertNull(resultado.get().getApellido());
    }
}