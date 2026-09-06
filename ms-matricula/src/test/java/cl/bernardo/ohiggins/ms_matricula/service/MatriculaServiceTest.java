package cl.bernardo.ohiggins.ms_matricula.service;

import cl.bernardo.ohiggins.ms_matricula.model.Matricula;
import cl.bernardo.ohiggins.ms_matricula.repository.MatriculaRepository;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatriculaServiceTest {

    @Mock
    private MatriculaRepository matriculaRepository;

    @Mock
    private UsuarioClientService usuarioClientService;

    @Mock
    private AcademicoClientService academicoClientService;

    @InjectMocks
    private MatriculaService matriculaService;

    private Matricula matricula;

    @BeforeEach
    void setUp() {
        matricula = Matricula.builder()
                .id(1L)
                .idEstudiante(2L)
                .idCurso(1L)
                .idApoderado(3L)
                .anioEscolar(2026)
                .estado(Matricula.EstadoMatricula.ACTIVA)
                .fechaMatricula(LocalDate.now())
                .build();
    }

    @Test
    void obtenerTodas_retornaLista() {
        when(matriculaRepository.findAll()).thenReturn(List.of(matricula));

        List<Matricula> resultado = matriculaService.obtenerTodas();

        assertEquals(1, resultado.size());
    }

    @Test
    void obtenerPorId_existente_retornaMatricula() {
        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(matricula));

        Optional<Matricula> resultado = matriculaService.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
    }

    @Test
    void obtenerPorId_noExistente_retornaVacio() {
        when(matriculaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Matricula> resultado = matriculaService.obtenerPorId(99L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerPorEstudiante_retornaLista() {
        when(matriculaRepository.findByIdEstudiante(2L)).thenReturn(List.of(matricula));

        List<Matricula> resultado = matriculaService.obtenerPorEstudiante(2L);

        assertEquals(1, resultado.size());
    }

    @Test
    void obtenerPorCurso_retornaLista() {
        when(matriculaRepository.findByIdCurso(1L)).thenReturn(List.of(matricula));

        List<Matricula> resultado = matriculaService.obtenerPorCurso(1L);

        assertEquals(1, resultado.size());
    }

    @Test
    void obtenerPorAnio_retornaLista() {
        when(matriculaRepository.findByAnioEscolar(2026)).thenReturn(List.of(matricula));

        List<Matricula> resultado = matriculaService.obtenerPorAnio(2026);

        assertEquals(1, resultado.size());
    }

    @Test
    void crear_estudianteNoExiste_retornaVacio() {
        Matricula nueva = Matricula.builder().idEstudiante(2L).idApoderado(3L).idCurso(1L).anioEscolar(2026).build();

        when(usuarioClientService.existeUsuario(2L)).thenReturn(false);

        Optional<Matricula> resultado = matriculaService.crear(nueva);

        assertTrue(resultado.isEmpty());
        verify(matriculaRepository, never()).save(any());
    }

    @Test
    void crear_apoderadoNoExiste_retornaVacio() {
        Matricula nueva = Matricula.builder().idEstudiante(2L).idApoderado(3L).idCurso(1L).anioEscolar(2026).build();

        when(usuarioClientService.existeUsuario(2L)).thenReturn(true);
        when(usuarioClientService.existeUsuario(3L)).thenReturn(false);

        Optional<Matricula> resultado = matriculaService.crear(nueva);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void crear_matriculaActivaExistenteMismoAnio_retornaVacio() {
        Matricula nueva = Matricula.builder().idEstudiante(2L).idApoderado(3L).idCurso(1L).anioEscolar(2026).build();

        when(usuarioClientService.existeUsuario(2L)).thenReturn(true);
        when(usuarioClientService.existeUsuario(3L)).thenReturn(true);
        when(matriculaRepository.findByIdEstudianteAndAnioEscolar(2L, 2026)).thenReturn(Optional.of(matricula));

        Optional<Matricula> resultado = matriculaService.crear(nueva);

        assertTrue(resultado.isEmpty());
        verify(academicoClientService, never()).incrementarAlumnos(any());
    }

    @Test
    void crear_matriculaExistenteNoActiva_permiteContinuar() {
        Matricula nueva = Matricula.builder().idEstudiante(2L).idApoderado(3L).idCurso(1L).anioEscolar(2026).build();
        Matricula retirada = Matricula.builder().estado(Matricula.EstadoMatricula.RETIRADA).build();

        when(usuarioClientService.existeUsuario(2L)).thenReturn(true);
        when(usuarioClientService.existeUsuario(3L)).thenReturn(true);
        when(matriculaRepository.findByIdEstudianteAndAnioEscolar(2L, 2026)).thenReturn(Optional.of(retirada));
        when(academicoClientService.incrementarAlumnos(1L)).thenReturn(true);
        when(matriculaRepository.save(any(Matricula.class))).thenReturn(nueva);

        Optional<Matricula> resultado = matriculaService.crear(nueva);

        assertTrue(resultado.isPresent());
    }

    @Test
    void crear_cursoSinCupos_retornaVacio() {
        Matricula nueva = Matricula.builder().idEstudiante(2L).idApoderado(3L).idCurso(1L).anioEscolar(2026).build();

        when(usuarioClientService.existeUsuario(2L)).thenReturn(true);
        when(usuarioClientService.existeUsuario(3L)).thenReturn(true);
        when(matriculaRepository.findByIdEstudianteAndAnioEscolar(2L, 2026)).thenReturn(Optional.empty());
        when(academicoClientService.incrementarAlumnos(1L)).thenReturn(false);

        Optional<Matricula> resultado = matriculaService.crear(nueva);

        assertTrue(resultado.isEmpty());
        verify(matriculaRepository, never()).save(any());
    }

    @Test
    void crear_exitoso_guardaConFechaYEstadoActiva() {
        Matricula nueva = Matricula.builder().idEstudiante(2L).idApoderado(3L).idCurso(1L).anioEscolar(2026).build();

        when(usuarioClientService.existeUsuario(2L)).thenReturn(true);
        when(usuarioClientService.existeUsuario(3L)).thenReturn(true);
        when(matriculaRepository.findByIdEstudianteAndAnioEscolar(2L, 2026)).thenReturn(Optional.empty());
        when(academicoClientService.incrementarAlumnos(1L)).thenReturn(true);
        when(matriculaRepository.save(any(Matricula.class))).thenReturn(nueva);

        Optional<Matricula> resultado = matriculaService.crear(nueva);

        assertTrue(resultado.isPresent());
        assertEquals(Matricula.EstadoMatricula.ACTIVA, nueva.getEstado());
        assertNotNull(nueva.getFechaMatricula());
    }

    @Test
    void cambiarEstado_existente_actualizaEstado() {
        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(matricula));
        when(matriculaRepository.save(any(Matricula.class))).thenReturn(matricula);

        Optional<Matricula> resultado = matriculaService.cambiarEstado(1L, Matricula.EstadoMatricula.RETIRADA);

        assertTrue(resultado.isPresent());
        assertEquals(Matricula.EstadoMatricula.RETIRADA, matricula.getEstado());
    }

    @Test
    void cambiarEstado_noExistente_retornaVacio() {
        when(matriculaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Matricula> resultado = matriculaService.cambiarEstado(99L, Matricula.EstadoMatricula.RETIRADA);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void eliminar_existente_eliminaYDecrementaAlumnos() {
        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(matricula));

        boolean resultado = matriculaService.eliminar(1L);

        assertTrue(resultado);
        verify(matriculaRepository).deleteById(1L);
        verify(academicoClientService).decrementarAlumnos(1L);
    }

    @Test
    void eliminar_noExistente_retornaFalse() {
        when(matriculaRepository.findById(99L)).thenReturn(Optional.empty());

        boolean resultado = matriculaService.eliminar(99L);

        assertFalse(resultado);
        verify(academicoClientService, never()).decrementarAlumnos(any());
    }
}