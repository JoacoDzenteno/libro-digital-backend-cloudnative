package cl.bernardo.ohiggins.ms_usuarios.service;

import cl.bernardo.ohiggins.ms_usuarios.dto.UsuarioDTO;
import cl.bernardo.ohiggins.ms_usuarios.model.Persona;
import cl.bernardo.ohiggins.ms_usuarios.model.Usuario;
import cl.bernardo.ohiggins.ms_usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private Persona persona;

    @BeforeEach
    void setUp() {
        persona = Persona.builder()
                .nombre("Pedro")
                .apellido("Soto")
                .rut("19876543-2")
                .email("pedro@personal.cl")
                .telefono("+56912345678")
                .direccion("Calle Falsa 123")
                .build();

        usuario = Usuario.builder()
                .id(2L)
                .email("pedro@colegio.cl")
                .password("hashed")
                .rol(Usuario.Rol.ESTUDIANTE)
                .persona(persona)
                .build();
    }

    @Test
    void obtenerTodos_retornaListaDeDTOs() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        List<UsuarioDTO> resultado = usuarioService.obtenerTodos();

        assertEquals(1, resultado.size());
        assertEquals("Pedro", resultado.get(0).getNombre());
    }

    @Test
    void obtenerPorId_existente_retornaDTO() {
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario));

        Optional<UsuarioDTO> resultado = usuarioService.obtenerPorId(2L);

        assertTrue(resultado.isPresent());
        assertEquals("19876543-2", resultado.get().getRut());
    }

    @Test
    void obtenerPorId_noExistente_retornaVacio() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<UsuarioDTO> resultado = usuarioService.obtenerPorId(99L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerPorEmail_existente_retornaDTO() {
        when(usuarioRepository.findByEmail("pedro@colegio.cl")).thenReturn(Optional.of(usuario));

        Optional<UsuarioDTO> resultado = usuarioService.obtenerPorEmail("pedro@colegio.cl");

        assertTrue(resultado.isPresent());
    }

    @Test
    void obtenerPorEmail_noExistente_retornaVacio() {
        when(usuarioRepository.findByEmail("noexiste@colegio.cl")).thenReturn(Optional.empty());

        Optional<UsuarioDTO> resultado = usuarioService.obtenerPorEmail("noexiste@colegio.cl");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerPorRol_retornaLista() {
        when(usuarioRepository.findByRol(Usuario.Rol.ESTUDIANTE)).thenReturn(List.of(usuario));

        List<UsuarioDTO> resultado = usuarioService.obtenerPorRol(Usuario.Rol.ESTUDIANTE);

        assertEquals(1, resultado.size());
    }

    @Test
    void buscar_conLimite_usaPageRequest() {
        when(usuarioRepository.buscarCombinado(eq("Pedro"), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(List.of(usuario));

        List<UsuarioDTO> resultado = usuarioService.buscar("Pedro", null, null, 5);

        assertEquals(1, resultado.size());
    }

    @Test
    void buscar_sinLimiteNiParametros_usaUnpaged() {
        when(usuarioRepository.buscarCombinado(isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(List.of(usuario));

        List<UsuarioDTO> resultado = usuarioService.buscar("", "  ", null, null);

        assertEquals(1, resultado.size());
    }

    @Test
    void crear_retornaDTO() {
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        UsuarioDTO resultado = usuarioService.crear(usuario);

        assertEquals("pedro@colegio.cl", resultado.getEmail());
    }

    @Test
    void actualizar_existenteConPersona_actualizaCampos() {
        Persona personaNueva = Persona.builder()
                .nombre("Pedro Actualizado")
                .apellido("Soto")
                .telefono("+56999999999")
                .direccion("Nueva Direccion")
                .build();

        Usuario usuarioActualizado = Usuario.builder()
                .email("pedro.nuevo@colegio.cl")
                .rol(Usuario.Rol.ESTUDIANTE)
                .persona(personaNueva)
                .build();

        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Optional<UsuarioDTO> resultado = usuarioService.actualizar(2L, usuarioActualizado);

        assertTrue(resultado.isPresent());
        assertEquals("pedro.nuevo@colegio.cl", usuario.getEmail());
        assertEquals("Pedro Actualizado", usuario.getPersona().getNombre());
        assertEquals("19876543-2", usuario.getPersona().getRut());
    }

    @Test
    void actualizar_noExistente_retornaVacio() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<UsuarioDTO> resultado = usuarioService.actualizar(99L, usuario);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void eliminar_existente_retornaTrue() {
        when(usuarioRepository.existsById(2L)).thenReturn(true);

        boolean resultado = usuarioService.eliminar(2L);

        assertTrue(resultado);
        verify(usuarioRepository).deleteById(2L);
    }

    @Test
    void eliminar_noExistente_retornaFalse() {
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        boolean resultado = usuarioService.eliminar(99L);

        assertFalse(resultado);
    }

    @Test
    void actualizarPerfil_conEmailYPassword_actualizaAmbos() {
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("nuevaClave")).thenReturn("hashNuevo");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Optional<UsuarioDTO> resultado = usuarioService.actualizarPerfil(2L, "nuevo@colegio.cl", "nuevaClave");

        assertTrue(resultado.isPresent());
        assertEquals("nuevo@colegio.cl", usuario.getEmail());
        assertEquals("hashNuevo", usuario.getPassword());
    }

    @Test
    void actualizarPerfil_sinCambios_noModificaNada() {
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        usuarioService.actualizarPerfil(2L, null, null);

        assertEquals("pedro@colegio.cl", usuario.getEmail());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void actualizarPerfil_noExistente_retornaVacio() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<UsuarioDTO> resultado = usuarioService.actualizarPerfil(99L, "x@x.cl", "pass");

        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerPorId_usuarioSinPersona_retornaDTOConCamposNulos() {
        Usuario sinPersona = Usuario.builder()
                .id(5L)
                .email("sinpersona@colegio.cl")
                .rol(Usuario.Rol.ADMINISTRATIVO)
                .build();

        when(usuarioRepository.findById(5L)).thenReturn(Optional.of(sinPersona));

        Optional<UsuarioDTO> resultado = usuarioService.obtenerPorId(5L);

        assertTrue(resultado.isPresent());
        assertNull(resultado.get().getNombre());
        assertNull(resultado.get().getRut());
    }

    @Test
    void buscar_todosLosParametrosConValor() {
        when(usuarioRepository.buscarCombinado(eq("Pedro"), eq("19876543-2"), eq("pedro@colegio.cl"), any(Pageable.class)))
                .thenReturn(List.of(usuario));

        List<UsuarioDTO> resultado = usuarioService.buscar("Pedro", "19876543-2", "pedro@colegio.cl", 5);

        assertEquals(1, resultado.size());
    }

    @Test
    void buscar_nombreNull_rutYEmailConValor() {
        when(usuarioRepository.buscarCombinado(isNull(), eq("19876543-2"), eq("pedro@colegio.cl"), any(Pageable.class)))
                .thenReturn(List.of(usuario));

        List<UsuarioDTO> resultado = usuarioService.buscar(null, "19876543-2", "pedro@colegio.cl", null);

        assertEquals(1, resultado.size());
    }

    @Test
    void buscar_limiteCero_usaUnpaged() {
        when(usuarioRepository.buscarCombinado(isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(List.of(usuario));

        List<UsuarioDTO> resultado = usuarioService.buscar(null, null, null, 0);

        assertEquals(1, resultado.size());
    }

    @Test
    void actualizar_actualizadoSinPersona_noTocaPersonaOriginal() {
        Usuario actualizadoSinPersona = Usuario.builder()
                .email("nuevo@colegio.cl")
                .rol(Usuario.Rol.ESTUDIANTE)
                .build();

        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Optional<UsuarioDTO> resultado = usuarioService.actualizar(2L, actualizadoSinPersona);

        assertTrue(resultado.isPresent());
        assertEquals("Pedro", usuario.getPersona().getNombre());
    }

    @Test
    void actualizar_originalSinPersona_noFallaAunqueActualizadoTraigaPersona() {
        Usuario sinPersona = Usuario.builder()
                .id(7L)
                .email("sinpersona@colegio.cl")
                .rol(Usuario.Rol.ESTUDIANTE)
                .build();

        Usuario actualizadoConPersona = Usuario.builder()
                .email("nuevo@colegio.cl")
                .rol(Usuario.Rol.ESTUDIANTE)
                .persona(persona)
                .build();

        when(usuarioRepository.findById(7L)).thenReturn(Optional.of(sinPersona));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(sinPersona);

        Optional<UsuarioDTO> resultado = usuarioService.actualizar(7L, actualizadoConPersona);

        assertTrue(resultado.isPresent());
        assertNull(sinPersona.getPersona());
    }

    @Test
    void actualizarPerfil_soloEmail_noTocaPassword() {
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        usuarioService.actualizarPerfil(2L, "soloemail@colegio.cl", null);

        assertEquals("soloemail@colegio.cl", usuario.getEmail());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void actualizarPerfil_soloPassword_noTocaEmail() {
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("nuevaClave")).thenReturn("hashNuevo");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        String emailOriginal = usuario.getEmail();
        usuarioService.actualizarPerfil(2L, null, "nuevaClave");

        assertEquals(emailOriginal, usuario.getEmail());
        assertEquals("hashNuevo", usuario.getPassword());
    }
}