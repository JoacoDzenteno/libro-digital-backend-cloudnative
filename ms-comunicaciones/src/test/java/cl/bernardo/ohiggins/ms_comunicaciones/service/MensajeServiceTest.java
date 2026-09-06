package cl.bernardo.ohiggins.ms_comunicaciones.service;

import cl.bernardo.ohiggins.ms_comunicaciones.model.Mensaje;
import cl.bernardo.ohiggins.ms_comunicaciones.repository.MensajeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MensajeServiceTest {

    @Mock
    private MensajeRepository mensajeRepository;

    @Mock
    private UsuarioClientService usuarioClientService;

    @InjectMocks
    private MensajeService mensajeService;

    private Mensaje mensaje;

    @BeforeEach
    void setUp() {
        mensaje = Mensaje.builder()
                .id(1L)
                .idRemitente(2L)
                .idDestinatario(3L)
                .asunto("Aviso")
                .contenido("Contenido del mensaje")
                .fechaEnvio(LocalDateTime.now())
                .tipo(Mensaje.TipoMensaje.NOTIFICACION)
                .leido(false)
                .build();
    }

    @Test
    void obtenerTodos_retornaLista() {
        when(mensajeRepository.findAll()).thenReturn(List.of(mensaje));

        List<Mensaje> resultado = mensajeService.obtenerTodos();

        assertEquals(1, resultado.size());
    }

    @Test
    void obtenerPorId_existente_retornaMensaje() {
        when(mensajeRepository.findById(1L)).thenReturn(Optional.of(mensaje));

        Optional<Mensaje> resultado = mensajeService.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
    }

    @Test
    void obtenerPorId_noExistente_retornaVacio() {
        when(mensajeRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Mensaje> resultado = mensajeService.obtenerPorId(99L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerPorDestinatario_retornaLista() {
        when(mensajeRepository.findByIdDestinatario(3L)).thenReturn(List.of(mensaje));

        List<Mensaje> resultado = mensajeService.obtenerPorDestinatario(3L);

        assertEquals(1, resultado.size());
    }

    @Test
    void obtenerNoLeidos_retornaLista() {
        when(mensajeRepository.findByIdDestinatarioAndLeido(3L, false)).thenReturn(List.of(mensaje));

        List<Mensaje> resultado = mensajeService.obtenerNoLeidos(3L);

        assertEquals(1, resultado.size());
    }

    @Test
    void obtenerPorTipo_retornaLista() {
        when(mensajeRepository.findByIdDestinatarioAndTipo(3L, Mensaje.TipoMensaje.NOTIFICACION))
                .thenReturn(List.of(mensaje));

        List<Mensaje> resultado = mensajeService.obtenerPorTipo(3L, Mensaje.TipoMensaje.NOTIFICACION);

        assertEquals(1, resultado.size());
    }

    @Test
    void enviar_destinatarioNoExiste_retornaVacio() {
        when(usuarioClientService.existeUsuario(3L)).thenReturn(false);

        Optional<Mensaje> resultado = mensajeService.enviar(mensaje);

        assertTrue(resultado.isEmpty());
        verify(mensajeRepository, never()).save(any());
    }

    @Test
    void enviar_exitoso_asignaFechaYNoLeido() {
        Mensaje nuevo = Mensaje.builder()
                .idRemitente(2L)
                .idDestinatario(3L)
                .asunto("Aviso")
                .contenido("Contenido")
                .tipo(Mensaje.TipoMensaje.COMUNICADO)
                .build();

        when(usuarioClientService.existeUsuario(3L)).thenReturn(true);
        when(mensajeRepository.save(any(Mensaje.class))).thenReturn(nuevo);

        Optional<Mensaje> resultado = mensajeService.enviar(nuevo);

        assertTrue(resultado.isPresent());
        assertNotNull(nuevo.getFechaEnvio());
        assertFalse(nuevo.getLeido());
    }

    @Test
    void marcarComoLeido_existente_actualizaLeido() {
        when(mensajeRepository.findById(1L)).thenReturn(Optional.of(mensaje));
        when(mensajeRepository.save(any(Mensaje.class))).thenReturn(mensaje);

        Optional<Mensaje> resultado = mensajeService.marcarComoLeido(1L);

        assertTrue(resultado.isPresent());
        assertTrue(mensaje.getLeido());
    }

    @Test
    void marcarComoLeido_noExistente_retornaVacio() {
        when(mensajeRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Mensaje> resultado = mensajeService.marcarComoLeido(99L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void eliminar_existente_retornaTrue() {
        when(mensajeRepository.existsById(1L)).thenReturn(true);

        boolean resultado = mensajeService.eliminar(1L);

        assertTrue(resultado);
        verify(mensajeRepository).deleteById(1L);
    }

    @Test
    void eliminar_noExistente_retornaFalse() {
        when(mensajeRepository.existsById(99L)).thenReturn(false);

        boolean resultado = mensajeService.eliminar(99L);

        assertFalse(resultado);
    }
}