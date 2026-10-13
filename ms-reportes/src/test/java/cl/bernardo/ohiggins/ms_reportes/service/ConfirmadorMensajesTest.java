package cl.bernardo.ohiggins.ms_reportes.service;

import cl.bernardo.ohiggins.ms_reportes.exception.ServicioNoDisponibleException;
import cl.bernardo.ohiggins.ms_reportes.exception.UsuarioNoEncontradoException;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ConfirmadorMensajesTest {

    @Mock
    private Channel channel;

    private ConfirmadorMensajes confirmador;

    @BeforeEach
    void setUp() {
        confirmador = new ConfirmadorMensajes();
        ReflectionTestUtils.setField(confirmador, "esperaMs", 0L); // sin esperas en los tests
    }

    @Test
    void exito_haceBasicAck() throws IOException {
        confirmador.procesar("test", "evt-1", channel, 10L, () -> { });

        verify(channel).basicAck(10L, false);
        verify(channel, never()).basicNack(anyLong(), anyBoolean(), anyBoolean());
    }

    @Test
    void errorNoRecuperable_nackSinReencolarYSinReintentos() throws IOException {
        AtomicInteger llamadas = new AtomicInteger();

        confirmador.procesar("test", "evt-2", channel, 11L, () -> {
            llamadas.incrementAndGet();
            throw new UsuarioNoEncontradoException("usuario no existe");
        });

        assertEquals(1, llamadas.get());
        verify(channel).basicNack(11L, false, false);
        verify(channel, never()).basicAck(anyLong(), anyBoolean());
    }

    @Test
    void errorRecuperablePersistente_reintentaYLuegoNackADlq() throws IOException {
        AtomicInteger llamadas = new AtomicInteger();

        confirmador.procesar("test", "evt-3", channel, 12L, () -> {
            llamadas.incrementAndGet();
            throw new ServicioNoDisponibleException("ms-usuarios caído", new RuntimeException());
        });

        assertEquals(3, llamadas.get());
        verify(channel).basicNack(12L, false, false);
        verify(channel, never()).basicAck(anyLong(), anyBoolean());
    }

    @Test
    void errorRecuperableQueSeResuelve_reintentaYHaceAck() throws IOException {
        AtomicInteger llamadas = new AtomicInteger();

        confirmador.procesar("test", "evt-4", channel, 13L, () -> {
            if (llamadas.incrementAndGet() < 3) {
                throw new ServicioNoDisponibleException("ms-usuarios caído", new RuntimeException());
            }
        });

        assertEquals(3, llamadas.get());
        verify(channel).basicAck(13L, false);
        verify(channel, never()).basicNack(anyLong(), anyBoolean(), anyBoolean());
    }

    @Test
    void errorInesperado_nackSinReencolar() throws IOException {
        confirmador.procesar("test", "evt-5", channel, 14L, () -> {
            throw new IllegalStateException("algo inesperado");
        });

        verify(channel).basicNack(14L, false, false);
        verify(channel, never()).basicAck(anyLong(), anyBoolean());
    }
}