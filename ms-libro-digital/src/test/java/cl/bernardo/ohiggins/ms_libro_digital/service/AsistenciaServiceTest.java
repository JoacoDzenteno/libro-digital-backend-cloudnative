package cl.bernardo.ohiggins.ms_libro_digital.service;

import cl.bernardo.ohiggins.ms_libro_digital.model.Asistencia;
import cl.bernardo.ohiggins.ms_libro_digital.repository.AsistenciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsistenciaServiceTest {

    @Mock
    private AsistenciaRepository asistenciaRepository;

    @Mock
    private AcademicoClientService academicoClientService;

    @Mock
    private NotificacionPublisher notificacionPublisher;

    @InjectMocks
    private AsistenciaService asistenciaService;

    private Asistencia asistencia;

    @BeforeEach
    void setUp() {
        asistencia = Asistencia.builder()
                .id(1L)
                .idEstudiante(2L)
                .idCurso(1L)
                .fecha(LocalDate.now())
                .estado(Asistencia.EstadoAsistencia.PRESENTE)
                .build();
    }

    @Test
    void obtenerTodas_retornaLista() {
        when(asistenciaRepository.findAll()).thenReturn(List.of(asistencia));

        List<Asistencia> resultado = asistenciaService.obtenerTodas();

        assertEquals(1, resultado.size());
    }

    @Test
    void obtenerPorId_existente_retornaAsistencia() {
        when(asistenciaRepository.findById(1L)).thenReturn(Optional.of(asistencia));

        Optional<Asistencia> resultado = asistenciaService.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
    }

    @Test
    void obtenerPorId_noExistente_retornaVacio() {
        when(asistenciaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Asistencia> resultado = asistenciaService.obtenerPorId(99L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerPorEstudiante_retornaLista() {
        when(asistenciaRepository.findByIdEstudiante(2L)).thenReturn(List.of(asistencia));

        List<Asistencia> resultado = asistenciaService.obtenerPorEstudiante(2L);

        assertEquals(1, resultado.size());
    }

    @Test
    void obtenerPorCursoYFecha_retornaLista() {
        LocalDate fecha = LocalDate.now();
        when(asistenciaRepository.findByIdCursoAndFecha(1L, fecha)).thenReturn(List.of(asistencia));

        List<Asistencia> resultado = asistenciaService.obtenerPorCursoYFecha(1L, fecha);

        assertEquals(1, resultado.size());
    }

    @Test
    void registrar_cursoNoExiste_retornaVacio() {
        when(academicoClientService.existeCurso(1L)).thenReturn(false);

        Optional<Asistencia> resultado = asistenciaService.registrar(asistencia);

        assertTrue(resultado.isEmpty());
        verify(asistenciaRepository, never()).save(any());
        verify(notificacionPublisher, never()).publicarNotificacion(any());
    }

    @Test
    void registrar_cursoExiste_conFechaNull_guardaYNotifica() {
        Asistencia nueva = Asistencia.builder()
                .idEstudiante(2L)
                .idCurso(1L)
                .estado(Asistencia.EstadoAsistencia.AUSENTE)
                .build();

        when(academicoClientService.existeCurso(1L)).thenReturn(true);
        when(asistenciaRepository.save(any(Asistencia.class))).thenReturn(asistencia);

        Optional<Asistencia> resultado = asistenciaService.registrar(nueva);

        assertNotNull(nueva.getFecha());
        assertTrue(resultado.isPresent());
        verify(notificacionPublisher).publicarNotificacion(any());
    }

    @Test
    void registrar_conFechaExistente_noSobrescribeFecha() {
        LocalDate fechaOriginal = LocalDate.of(2026, 1, 1);
        asistencia.setFecha(fechaOriginal);

        when(academicoClientService.existeCurso(1L)).thenReturn(true);
        when(asistenciaRepository.save(any(Asistencia.class))).thenReturn(asistencia);

        asistenciaService.registrar(asistencia);

        assertEquals(fechaOriginal, asistencia.getFecha());
    }

    @Test
    void actualizar_existente_actualizaCampos() {
        Asistencia actualizada = Asistencia.builder()
                .estado(Asistencia.EstadoAsistencia.TARDANZA)
                .observacion("Llegó tarde")
                .build();

        when(asistenciaRepository.findById(1L)).thenReturn(Optional.of(asistencia));
        when(asistenciaRepository.save(any(Asistencia.class))).thenReturn(asistencia);

        Optional<Asistencia> resultado = asistenciaService.actualizar(1L, actualizada);

        assertTrue(resultado.isPresent());
        verify(asistenciaRepository).save(asistencia);
    }

    @Test
    void actualizar_noExistente_retornaVacio() {
        when(asistenciaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Asistencia> resultado = asistenciaService.actualizar(99L, asistencia);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void eliminar_existente_retornaTrue() {
        when(asistenciaRepository.existsById(1L)).thenReturn(true);

        boolean resultado = asistenciaService.eliminar(1L);

        assertTrue(resultado);
        verify(asistenciaRepository).deleteById(1L);
    }

    @Test
    void eliminar_noExistente_retornaFalse() {
        when(asistenciaRepository.existsById(99L)).thenReturn(false);

        boolean resultado = asistenciaService.eliminar(99L);

        assertFalse(resultado);
    }
}