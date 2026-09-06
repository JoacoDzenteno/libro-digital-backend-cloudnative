package cl.bernardo.ohiggins.ms_academico.service;

import cl.bernardo.ohiggins.ms_academico.model.Asignatura;
import cl.bernardo.ohiggins.ms_academico.model.CargaHoraria;
import cl.bernardo.ohiggins.ms_academico.model.Curso;
import cl.bernardo.ohiggins.ms_academico.repository.AsignaturaRepository;
import cl.bernardo.ohiggins.ms_academico.repository.CargaHorariaRepository;
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
class CargaHorariaServiceTest {

    @Mock
    private CargaHorariaRepository cargaHorariaRepository;

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private AsignaturaRepository asignaturaRepository;

    @InjectMocks
    private CargaHorariaService cargaHorariaService;

    private Curso curso;
    private Asignatura asignatura;
    private CargaHoraria cargaHoraria;

    @BeforeEach
    void setUp() {
        curso = Curso.builder().id(1L).build();
        asignatura = Asignatura.builder().id(1L).build();
        cargaHoraria = CargaHoraria.builder()
                .id(1L)
                .curso(curso)
                .asignatura(asignatura)
                .idProfesor(5L)
                .horasSemanales(6)
                .build();
    }

    @Test
    void obtenerTodas_retornaLista() {
        when(cargaHorariaRepository.findAll()).thenReturn(List.of(cargaHoraria));

        List<CargaHoraria> resultado = cargaHorariaService.obtenerTodas();

        assertEquals(1, resultado.size());
    }

    @Test
    void obtenerPorId_existente_retornaCarga() {
        when(cargaHorariaRepository.findById(1L)).thenReturn(Optional.of(cargaHoraria));

        Optional<CargaHoraria> resultado = cargaHorariaService.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
    }

    @Test
    void obtenerPorCurso_retornaLista() {
        when(cargaHorariaRepository.findByCursoId(1L)).thenReturn(List.of(cargaHoraria));

        List<CargaHoraria> resultado = cargaHorariaService.obtenerPorCurso(1L);

        assertEquals(1, resultado.size());
    }

    @Test
    void obtenerPorProfesor_retornaLista() {
        when(cargaHorariaRepository.findByIdProfesor(5L)).thenReturn(List.of(cargaHoraria));

        List<CargaHoraria> resultado = cargaHorariaService.obtenerPorProfesor(5L);

        assertEquals(1, resultado.size());
    }

    @Test
    void crear_cursoNull_retornaVacio() {
        CargaHoraria nueva = CargaHoraria.builder().asignatura(asignatura).idProfesor(5L).build();

        Optional<CargaHoraria> resultado = cargaHorariaService.crear(nueva);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void crear_cursoIdNull_retornaVacio() {
        Curso cursoSinId = Curso.builder().build();
        CargaHoraria nueva = CargaHoraria.builder().curso(cursoSinId).asignatura(asignatura).idProfesor(5L).build();

        Optional<CargaHoraria> resultado = cargaHorariaService.crear(nueva);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void crear_asignaturaNull_retornaVacio() {
        CargaHoraria nueva = CargaHoraria.builder().curso(curso).idProfesor(5L).build();

        Optional<CargaHoraria> resultado = cargaHorariaService.crear(nueva);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void crear_asignaturaIdNull_retornaVacio() {
        Asignatura asignaturaSinId = Asignatura.builder().build();
        CargaHoraria nueva = CargaHoraria.builder().curso(curso).asignatura(asignaturaSinId).idProfesor(5L).build();

        Optional<CargaHoraria> resultado = cargaHorariaService.crear(nueva);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void crear_cursoNoExiste_retornaVacio() {
        CargaHoraria nueva = CargaHoraria.builder().curso(curso).asignatura(asignatura).idProfesor(5L).build();

        when(cursoRepository.existsById(1L)).thenReturn(false);
        when(asignaturaRepository.existsById(1L)).thenReturn(true);

        Optional<CargaHoraria> resultado = cargaHorariaService.crear(nueva);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void crear_asignaturaNoExiste_retornaVacio() {
        CargaHoraria nueva = CargaHoraria.builder().curso(curso).asignatura(asignatura).idProfesor(5L).build();

        when(cursoRepository.existsById(1L)).thenReturn(true);
        when(asignaturaRepository.existsById(1L)).thenReturn(false);

        Optional<CargaHoraria> resultado = cargaHorariaService.crear(nueva);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void crear_exitoso_guardaCarga() {
        CargaHoraria nueva = CargaHoraria.builder().curso(curso).asignatura(asignatura).idProfesor(5L).build();

        when(cursoRepository.existsById(1L)).thenReturn(true);
        when(asignaturaRepository.existsById(1L)).thenReturn(true);
        when(cargaHorariaRepository.save(any(CargaHoraria.class))).thenReturn(nueva);

        Optional<CargaHoraria> resultado = cargaHorariaService.crear(nueva);

        assertTrue(resultado.isPresent());
    }

    @Test
    void actualizar_cursoNull_retornaVacio() {
        CargaHoraria actualizada = CargaHoraria.builder().asignatura(asignatura).build();

        Optional<CargaHoraria> resultado = cargaHorariaService.actualizar(1L, actualizada);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void actualizar_asignaturaNull_retornaVacio() {
        CargaHoraria actualizada = CargaHoraria.builder().curso(curso).build();

        Optional<CargaHoraria> resultado = cargaHorariaService.actualizar(1L, actualizada);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void actualizar_cursoNoExiste_retornaVacio() {
        CargaHoraria actualizada = CargaHoraria.builder().curso(curso).asignatura(asignatura).horasSemanales(4).build();

        when(cursoRepository.existsById(1L)).thenReturn(false);
        when(asignaturaRepository.existsById(1L)).thenReturn(true);

        Optional<CargaHoraria> resultado = cargaHorariaService.actualizar(1L, actualizada);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void actualizar_asignaturaNoExiste_retornaVacio() {
        CargaHoraria actualizada = CargaHoraria.builder().curso(curso).asignatura(asignatura).horasSemanales(4).build();

        when(cursoRepository.existsById(1L)).thenReturn(true);
        when(asignaturaRepository.existsById(1L)).thenReturn(false);

        Optional<CargaHoraria> resultado = cargaHorariaService.actualizar(1L, actualizada);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void actualizar_existente_actualizaCampos() {
        CargaHoraria actualizada = CargaHoraria.builder().curso(curso).asignatura(asignatura).horasSemanales(8).build();

        when(cursoRepository.existsById(1L)).thenReturn(true);
        when(asignaturaRepository.existsById(1L)).thenReturn(true);
        when(cargaHorariaRepository.findById(1L)).thenReturn(Optional.of(cargaHoraria));
        when(cargaHorariaRepository.save(any(CargaHoraria.class))).thenReturn(cargaHoraria);

        Optional<CargaHoraria> resultado = cargaHorariaService.actualizar(1L, actualizada);

        assertTrue(resultado.isPresent());
        assertEquals(8, cargaHoraria.getHorasSemanales());
    }

    @Test
    void actualizar_idNoExistente_retornaVacio() {
        CargaHoraria actualizada = CargaHoraria.builder().curso(curso).asignatura(asignatura).horasSemanales(8).build();

        when(cursoRepository.existsById(1L)).thenReturn(true);
        when(asignaturaRepository.existsById(1L)).thenReturn(true);
        when(cargaHorariaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<CargaHoraria> resultado = cargaHorariaService.actualizar(99L, actualizada);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void eliminar_existente_retornaTrue() {
        when(cargaHorariaRepository.existsById(1L)).thenReturn(true);

        boolean resultado = cargaHorariaService.eliminar(1L);

        assertTrue(resultado);
        verify(cargaHorariaRepository).deleteById(1L);
    }

    @Test
    void eliminar_noExistente_retornaFalse() {
        when(cargaHorariaRepository.existsById(99L)).thenReturn(false);

        boolean resultado = cargaHorariaService.eliminar(99L);

        assertFalse(resultado);
    }
}