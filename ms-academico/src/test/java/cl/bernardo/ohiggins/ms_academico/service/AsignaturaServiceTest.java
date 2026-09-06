package cl.bernardo.ohiggins.ms_academico.service;

import cl.bernardo.ohiggins.ms_academico.model.Asignatura;
import cl.bernardo.ohiggins.ms_academico.repository.AsignaturaRepository;
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
class AsignaturaServiceTest {

    @Mock
    private AsignaturaRepository asignaturaRepository;

    @InjectMocks
    private AsignaturaService asignaturaService;

    private Asignatura asignatura;

    @BeforeEach
    void setUp() {
        asignatura = Asignatura.builder()
                .id(1L)
                .nombre("Matemática")
                .codigo("MAT-101")
                .descripcion("Matemática básica")
                .build();
    }

    @Test
    void obtenerTodas_retornaLista() {
        when(asignaturaRepository.findAll()).thenReturn(List.of(asignatura));

        List<Asignatura> resultado = asignaturaService.obtenerTodas();

        assertEquals(1, resultado.size());
    }

    @Test
    void obtenerPorId_existente_retornaAsignatura() {
        when(asignaturaRepository.findById(1L)).thenReturn(Optional.of(asignatura));

        Optional<Asignatura> resultado = asignaturaService.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
    }

    @Test
    void obtenerPorId_noExistente_retornaVacio() {
        when(asignaturaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Asignatura> resultado = asignaturaService.obtenerPorId(99L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void crear_guardaYRetorna() {
        when(asignaturaRepository.save(any(Asignatura.class))).thenReturn(asignatura);

        Asignatura resultado = asignaturaService.crear(asignatura);

        assertEquals("Matemática", resultado.getNombre());
    }

    @Test
    void actualizar_existente_actualizaCampos() {
        Asignatura actualizada = Asignatura.builder()
                .nombre("Física")
                .codigo("FIS-201")
                .descripcion("Física general")
                .build();

        when(asignaturaRepository.findById(1L)).thenReturn(Optional.of(asignatura));
        when(asignaturaRepository.save(any(Asignatura.class))).thenReturn(asignatura);

        Optional<Asignatura> resultado = asignaturaService.actualizar(1L, actualizada);

        assertTrue(resultado.isPresent());
        assertEquals("Física", asignatura.getNombre());
    }

    @Test
    void actualizar_noExistente_retornaVacio() {
        when(asignaturaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Asignatura> resultado = asignaturaService.actualizar(99L, asignatura);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void eliminar_existente_retornaTrue() {
        when(asignaturaRepository.existsById(1L)).thenReturn(true);

        boolean resultado = asignaturaService.eliminar(1L);

        assertTrue(resultado);
        verify(asignaturaRepository).deleteById(1L);
    }

    @Test
    void eliminar_noExistente_retornaFalse() {
        when(asignaturaRepository.existsById(99L)).thenReturn(false);

        boolean resultado = asignaturaService.eliminar(99L);

        assertFalse(resultado);
    }

    @Test
    void buscar_conAmbosValores() {
        when(asignaturaRepository.buscarCombinado("Matemática", "MAT-101")).thenReturn(List.of(asignatura));

        List<Asignatura> resultado = asignaturaService.buscar("Matemática", "MAT-101");

        assertEquals(1, resultado.size());
    }

    @Test
    void buscar_ambosNulos() {
        when(asignaturaRepository.buscarCombinado(null, null)).thenReturn(List.of(asignatura));

        List<Asignatura> resultado = asignaturaService.buscar(null, null);

        assertEquals(1, resultado.size());
    }

    @Test
    void buscar_ambosBlancos() {
        when(asignaturaRepository.buscarCombinado(null, null)).thenReturn(List.of(asignatura));

        List<Asignatura> resultado = asignaturaService.buscar("  ", "");

        assertEquals(1, resultado.size());
    }

    @Test
    void buscar_soloNombre() {
        when(asignaturaRepository.buscarCombinado("Matemática", null)).thenReturn(List.of(asignatura));

        List<Asignatura> resultado = asignaturaService.buscar("Matemática", null);

        assertEquals(1, resultado.size());
    }

    @Test
    void buscar_soloCodigo() {
        when(asignaturaRepository.buscarCombinado(null, "MAT-101")).thenReturn(List.of(asignatura));

        List<Asignatura> resultado = asignaturaService.buscar(null, "MAT-101");

        assertEquals(1, resultado.size());
    }
}