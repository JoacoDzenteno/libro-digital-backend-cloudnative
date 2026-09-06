package cl.bernardo.ohiggins.ms_libro_digital.service;

import cl.bernardo.ohiggins.ms_libro_digital.model.Calificacion;
import cl.bernardo.ohiggins.ms_libro_digital.repository.CalificacionRepository;
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
class CalificacionServiceTest {

    @Mock
    private CalificacionRepository calificacionRepository;

    @Mock
    private NotificacionPublisher notificacionPublisher;

    @InjectMocks
    private CalificacionService calificacionService;

    private Calificacion calificacion;

    @BeforeEach
    void setUp() {
        calificacion = Calificacion.builder()
                .id(1L)
                .idEstudiante(2L)
                .idAsignatura(1L)
                .idCurso(1L)
                .nota(6.5)
                .periodo("2026-1")
                .fecha(LocalDate.now())
                .build();
    }

    @Test
    void obtenerTodas_retornaLista() {
        when(calificacionRepository.findAll()).thenReturn(List.of(calificacion));

        List<Calificacion> resultado = calificacionService.obtenerTodas();

        assertEquals(1, resultado.size());
        verify(calificacionRepository).findAll();
    }

    @Test
    void obtenerPorId_existente_retornaCalificacion() {
        when(calificacionRepository.findById(1L)).thenReturn(Optional.of(calificacion));

        Optional<Calificacion> resultado = calificacionService.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(6.5, resultado.get().getNota());
    }

    @Test
    void obtenerPorId_noExistente_retornaVacio() {
        when(calificacionRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Calificacion> resultado = calificacionService.obtenerPorId(99L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerPorEstudiante_retornaLista() {
        when(calificacionRepository.findByIdEstudiante(2L)).thenReturn(List.of(calificacion));

        List<Calificacion> resultado = calificacionService.obtenerPorEstudiante(2L);

        assertEquals(1, resultado.size());
    }

    @Test
    void obtenerPorEstudianteYAsignatura_retornaLista() {
        when(calificacionRepository.findByIdEstudianteAndIdAsignatura(2L, 1L))
                .thenReturn(List.of(calificacion));

        List<Calificacion> resultado = calificacionService.obtenerPorEstudianteYAsignatura(2L, 1L);

        assertEquals(1, resultado.size());
    }

    @Test
    void obtenerPorCursoYAsignatura_retornaLista() {
        when(calificacionRepository.findByIdCursoAndIdAsignatura(1L, 1L))
                .thenReturn(List.of(calificacion));

        List<Calificacion> resultado = calificacionService.obtenerPorCursoYAsignatura(1L, 1L);

        assertEquals(1, resultado.size());
    }

    @Test
    void registrar_conFechaNull_asignaFechaActualYPublicaEventos() {
        Calificacion nueva = Calificacion.builder()
                .idEstudiante(2L)
                .idAsignatura(1L)
                .idCurso(1L)
                .nota(5.0)
                .periodo("2026-1")
                .build();

        when(calificacionRepository.save(any(Calificacion.class))).thenReturn(calificacion);

        Calificacion resultado = calificacionService.registrar(nueva);

        assertNotNull(nueva.getFecha());
        assertNotNull(resultado);
        verify(notificacionPublisher).publicarNotificacion(any());
        verify(notificacionPublisher).publicarReporte(any());
    }

    @Test
    void registrar_conFechaExistente_noSobrescribeFecha() {
        LocalDate fechaOriginal = LocalDate.of(2026, 1, 1);
        calificacion.setFecha(fechaOriginal);

        when(calificacionRepository.save(any(Calificacion.class))).thenReturn(calificacion);

        calificacionService.registrar(calificacion);

        assertEquals(fechaOriginal, calificacion.getFecha());
    }

    @Test
    void actualizar_existente_actualizaCampos() {
        Calificacion actualizada = Calificacion.builder()
                .nota(7.0)
                .descripcion("Mejoró")
                .periodo("2026-2")
                .build();

        when(calificacionRepository.findById(1L)).thenReturn(Optional.of(calificacion));
        when(calificacionRepository.save(any(Calificacion.class))).thenReturn(calificacion);

        Optional<Calificacion> resultado = calificacionService.actualizar(1L, actualizada);

        assertTrue(resultado.isPresent());
        verify(calificacionRepository).save(calificacion);
    }

    @Test
    void actualizar_noExistente_retornaVacio() {
        when(calificacionRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Calificacion> resultado = calificacionService.actualizar(99L, calificacion);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void eliminar_existente_retornaTrue() {
        when(calificacionRepository.existsById(1L)).thenReturn(true);

        boolean resultado = calificacionService.eliminar(1L);

        assertTrue(resultado);
        verify(calificacionRepository).deleteById(1L);
    }

    @Test
    void eliminar_noExistente_retornaFalse() {
        when(calificacionRepository.existsById(99L)).thenReturn(false);

        boolean resultado = calificacionService.eliminar(99L);

        assertFalse(resultado);
        verify(calificacionRepository, never()).deleteById(any());
    }
}