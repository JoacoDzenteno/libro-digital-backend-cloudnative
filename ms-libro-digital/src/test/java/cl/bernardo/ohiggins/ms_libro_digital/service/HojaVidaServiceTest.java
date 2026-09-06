package cl.bernardo.ohiggins.ms_libro_digital.service;

import cl.bernardo.ohiggins.ms_libro_digital.model.HojaVida;
import cl.bernardo.ohiggins.ms_libro_digital.repository.HojaVidaRepository;
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
class HojaVidaServiceTest {

    @Mock
    private HojaVidaRepository hojaVidaRepository;

    @Mock
    private NotificacionPublisher notificacionPublisher;

    @InjectMocks
    private HojaVidaService hojaVidaService;

    private HojaVida hojaVida;

    @BeforeEach
    void setUp() {
        hojaVida = HojaVida.builder()
                .id(1L)
                .idEstudiante(2L)
                .idProfesor(5L)
                .tipo(HojaVida.TipoAnotacion.POSITIVA)
                .descripcion("Destacó en clases")
                .fecha(LocalDate.now())
                .build();
    }

    @Test
    void obtenerTodas_retornaLista() {
        when(hojaVidaRepository.findAll()).thenReturn(List.of(hojaVida));

        List<HojaVida> resultado = hojaVidaService.obtenerTodas();

        assertEquals(1, resultado.size());
    }

    @Test
    void obtenerPorId_existente_retornaHojaVida() {
        when(hojaVidaRepository.findById(1L)).thenReturn(Optional.of(hojaVida));

        Optional<HojaVida> resultado = hojaVidaService.obtenerPorId(1L);

        assertTrue(resultado.isPresent());
    }

    @Test
    void obtenerPorId_noExistente_retornaVacio() {
        when(hojaVidaRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<HojaVida> resultado = hojaVidaService.obtenerPorId(99L);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void obtenerPorEstudiante_retornaLista() {
        when(hojaVidaRepository.findByIdEstudiante(2L)).thenReturn(List.of(hojaVida));

        List<HojaVida> resultado = hojaVidaService.obtenerPorEstudiante(2L);

        assertEquals(1, resultado.size());
    }

    @Test
    void obtenerPorEstudianteYTipo_retornaLista() {
        when(hojaVidaRepository.findByIdEstudianteAndTipo(2L, HojaVida.TipoAnotacion.POSITIVA))
                .thenReturn(List.of(hojaVida));

        List<HojaVida> resultado = hojaVidaService.obtenerPorEstudianteYTipo(2L, HojaVida.TipoAnotacion.POSITIVA);

        assertEquals(1, resultado.size());
    }

    @Test
    void registrar_conFechaNull_asignaFechaYNotifica() {
        HojaVida nueva = HojaVida.builder()
                .idEstudiante(2L)
                .idProfesor(5L)
                .tipo(HojaVida.TipoAnotacion.NEGATIVA)
                .descripcion("Llegó sin materiales")
                .build();

        when(hojaVidaRepository.save(any(HojaVida.class))).thenReturn(hojaVida);

        HojaVida resultado = hojaVidaService.registrar(nueva);

        assertNotNull(nueva.getFecha());
        assertNotNull(resultado);
        verify(notificacionPublisher).publicarNotificacion(any());
    }

    @Test
    void registrar_conFechaExistente_noSobrescribeFecha() {
        LocalDate fechaOriginal = LocalDate.of(2026, 1, 1);
        hojaVida.setFecha(fechaOriginal);

        when(hojaVidaRepository.save(any(HojaVida.class))).thenReturn(hojaVida);

        hojaVidaService.registrar(hojaVida);

        assertEquals(fechaOriginal, hojaVida.getFecha());
    }

    @Test
    void eliminar_existente_retornaTrue() {
        when(hojaVidaRepository.existsById(1L)).thenReturn(true);

        boolean resultado = hojaVidaService.eliminar(1L);

        assertTrue(resultado);
        verify(hojaVidaRepository).deleteById(1L);
    }

    @Test
    void eliminar_noExistente_retornaFalse() {
        when(hojaVidaRepository.existsById(99L)).thenReturn(false);

        boolean resultado = hojaVidaService.eliminar(99L);

        assertFalse(resultado);
    }
}