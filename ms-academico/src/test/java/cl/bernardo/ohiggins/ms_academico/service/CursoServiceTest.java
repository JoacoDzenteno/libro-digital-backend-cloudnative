package cl.bernardo.ohiggins.ms_academico.service;

import cl.bernardo.ohiggins.ms_academico.model.Curso;
import cl.bernardo.ohiggins.ms_academico.repository.CursoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CursoServiceTest {

    @Mock
    private CursoRepository cursoRepository;

    @InjectMocks
    private CursoService cursoService;

    private Curso curso;

    @BeforeEach
    void setUp() {
        curso = Curso.builder()
                .id(1L)
                .nombre("1° Básico A")
                .nivel("1° Básico")
                .letra("A")
                .anioEscolar(2026)
                .cantidadAlumnos(20)
                .capacidadMaxima(30)
                .build();
    }

    @Test
    void obtenerTodos_retornaLista() {
        when(cursoRepository.findAll()).thenReturn(List.of(curso));

        List<Curso> resultado = cursoService.obtenerTodos();

        assertEquals(1, resultado.size());
    }

    @Test
    void obtenerPorId_existente_retornaCurso() {
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));

        Optional<Curso> resultado = cursoService.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
    }

    @Test
    void obtenerPorId_noExistente_retornaVacio() {
        when(cursoRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Curso> resultado = cursoService.obtenerPorId(99L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerPorNivel_retornaLista() {
        when(cursoRepository.findByNivel("1° Básico")).thenReturn(List.of(curso));

        List<Curso> resultado = cursoService.obtenerPorNivel("1° Básico");

        assertEquals(1, resultado.size());
    }

    @Test
    void obtenerPorAnio_retornaLista() {
        when(cursoRepository.findByAnioEscolar(2026)).thenReturn(List.of(curso));

        List<Curso> resultado = cursoService.obtenerPorAnio(2026);

        assertEquals(1, resultado.size());
    }

    @Test
    void crear_guardaYRetorna() {
        when(cursoRepository.save(any(Curso.class))).thenReturn(curso);

        Curso resultado = cursoService.crear(curso);

        assertEquals("1° Básico A", resultado.getNombre());
    }

    @Test
    void actualizar_existente_actualizaCampos() {
        Curso actualizado = Curso.builder()
                .nombre("2° Básico B")
                .nivel("2° Básico")
                .letra("B")
                .anioEscolar(2027)
                .build();

        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(cursoRepository.save(any(Curso.class))).thenReturn(curso);

        Optional<Curso> resultado = cursoService.actualizar(1L, actualizado);

        assertTrue(resultado.isPresent());
        assertEquals("2° Básico B", curso.getNombre());
    }

    @Test
    void actualizar_noExistente_retornaVacio() {
        when(cursoRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Curso> resultado = cursoService.actualizar(99L, curso);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void incrementarAlumnos_conCupo_incrementaContador() {
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(cursoRepository.save(any(Curso.class))).thenReturn(curso);

        Optional<Curso> resultado = cursoService.incrementarAlumnos(1L);

        assertTrue(resultado.isPresent());
        assertEquals(21, curso.getCantidadAlumnos());
    }

    @Test
    void incrementarAlumnos_cursoLleno_retornaVacio() {
        curso.setCantidadAlumnos(30);

        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));

        Optional<Curso> resultado = cursoService.incrementarAlumnos(1L);

        assertTrue(resultado.isEmpty());
        verify(cursoRepository, never()).save(any());
    }

    @Test
    void incrementarAlumnos_noExistente_retornaVacio() {
        when(cursoRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Curso> resultado = cursoService.incrementarAlumnos(99L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void decrementarAlumnos_conAlumnos_decrementaContador() {
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(cursoRepository.save(any(Curso.class))).thenReturn(curso);

        Optional<Curso> resultado = cursoService.decrementarAlumnos(1L);

        assertTrue(resultado.isPresent());
        assertEquals(19, curso.getCantidadAlumnos());
    }

    @Test
    void decrementarAlumnos_sinAlumnos_noDecrementaBajoCero() {
        curso.setCantidadAlumnos(0);

        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(cursoRepository.save(any(Curso.class))).thenReturn(curso);

        Optional<Curso> resultado = cursoService.decrementarAlumnos(1L);

        assertTrue(resultado.isPresent());
        assertEquals(0, curso.getCantidadAlumnos());
    }

    @Test
    void decrementarAlumnos_noExistente_retornaVacio() {
        when(cursoRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Curso> resultado = cursoService.decrementarAlumnos(99L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void eliminar_existente_retornaTrue() {
        when(cursoRepository.existsById(1L)).thenReturn(true);

        boolean resultado = cursoService.eliminar(1L);

        assertTrue(resultado);
        verify(cursoRepository).deleteById(1L);
    }

    @Test
    void eliminar_noExistente_retornaFalse() {
        when(cursoRepository.existsById(99L)).thenReturn(false);

        boolean resultado = cursoService.eliminar(99L);

        assertFalse(resultado);
    }

    @Test
    void buscar_conNivelYAnio() {
        when(cursoRepository.buscarCombinado("1° Básico", 2026)).thenReturn(List.of(curso));

        List<Curso> resultado = cursoService.buscar("1° Básico", 2026);

        assertEquals(1, resultado.size());
    }

    @Test
    void buscar_nivelNuloYAnioNulo() {
        when(cursoRepository.buscarCombinado(null, null)).thenReturn(List.of(curso));

        List<Curso> resultado = cursoService.buscar(null, null);

        assertEquals(1, resultado.size());
    }

    @Test
    void buscar_nivelBlanco() {
        when(cursoRepository.buscarCombinado(null, 2026)).thenReturn(List.of(curso));

        List<Curso> resultado = cursoService.buscar("  ", 2026);

        assertEquals(1, resultado.size());
    }
}