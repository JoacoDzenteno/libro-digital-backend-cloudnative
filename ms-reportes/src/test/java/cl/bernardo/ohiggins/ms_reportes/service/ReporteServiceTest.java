package cl.bernardo.ohiggins.ms_reportes.service;

import cl.bernardo.ohiggins.ms_reportes.model.Reporte;
import cl.bernardo.ohiggins.ms_reportes.repository.ReporteRepository;
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
class ReporteServiceTest {

    @Mock
    private ReporteRepository reporteRepository;

    @Mock
    private UsuarioClientService usuarioClientService;

    @InjectMocks
    private ReporteService reporteService;

    private Reporte reporte;

    @BeforeEach
    void setUp() {
        reporte = Reporte.builder()
                .id(1L)
                .titulo("Reporte de prueba")
                .tipo(Reporte.TipoReporte.CALIFICACIONES)
                .idReferencia(2L)
                .contenido("Contenido de prueba")
                .fechaGeneracion(LocalDateTime.now())
                .idGeneradoPor(1L)
                .build();
    }

    @Test
    void obtenerTodos_retornaLista() {
        when(reporteRepository.findAll()).thenReturn(List.of(reporte));

        List<Reporte> resultado = reporteService.obtenerTodos();

        assertEquals(1, resultado.size());
    }

    @Test
    void obtenerPorId_existente_retornaReporte() {
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporte));

        Optional<Reporte> resultado = reporteService.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
    }

    @Test
    void obtenerPorId_noExistente_retornaVacio() {
        when(reporteRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Reporte> resultado = reporteService.obtenerPorId(99L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerPorTipo_retornaLista() {
        when(reporteRepository.findByTipo(Reporte.TipoReporte.CALIFICACIONES)).thenReturn(List.of(reporte));

        List<Reporte> resultado = reporteService.obtenerPorTipo(Reporte.TipoReporte.CALIFICACIONES);

        assertEquals(1, resultado.size());
    }

    @Test
    void obtenerPorReferencia_retornaLista() {
        when(reporteRepository.findByIdReferencia(2L)).thenReturn(List.of(reporte));

        List<Reporte> resultado = reporteService.obtenerPorReferencia(2L);

        assertEquals(1, resultado.size());
    }

    @Test
    void obtenerPorGenerador_retornaLista() {
        when(reporteRepository.findByIdGeneradoPor(1L)).thenReturn(List.of(reporte));

        List<Reporte> resultado = reporteService.obtenerPorGenerador(1L);

        assertEquals(1, resultado.size());
    }

    @Test
    void generar_generadorNoExiste_retornaVacio() {
        when(usuarioClientService.existeUsuario(1L)).thenReturn(false);

        Optional<Reporte> resultado = reporteService.generar(reporte);

        assertTrue(resultado.isEmpty());
        verify(reporteRepository, never()).save(any());
    }

    @Test
    void generar_referenciaNoExiste_retornaVacio() {
        when(usuarioClientService.existeUsuario(1L)).thenReturn(true);
        when(usuarioClientService.existeUsuario(2L)).thenReturn(false);

        Optional<Reporte> resultado = reporteService.generar(reporte);

        assertTrue(resultado.isEmpty());
        verify(reporteRepository, never()).save(any());
    }

    @Test
    void generar_exitoso_asignaFechaYGuarda() {
        when(usuarioClientService.existeUsuario(1L)).thenReturn(true);
        when(usuarioClientService.existeUsuario(2L)).thenReturn(true);
        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporte);

        Optional<Reporte> resultado = reporteService.generar(reporte);

        assertTrue(resultado.isPresent());
        assertNotNull(reporte.getFechaGeneracion());
    }

    @Test
    void eliminar_existente_retornaTrue() {
        when(reporteRepository.existsById(1L)).thenReturn(true);

        boolean resultado = reporteService.eliminar(1L);

        assertTrue(resultado);
        verify(reporteRepository).deleteById(1L);
    }

    @Test
    void eliminar_noExistente_retornaFalse() {
        when(reporteRepository.existsById(99L)).thenReturn(false);

        boolean resultado = reporteService.eliminar(99L);

        assertFalse(resultado);
    }
}