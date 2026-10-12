package cl.bernardo.ohiggins.ms_comunicaciones.service;

import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DlqListenerTest {

    @Mock
    private Channel channel;

    private final DlqListener listener = new DlqListener();

    private Message mensaje(long tag) {
        MessageProperties props = new MessageProperties();
        props.setDeliveryTag(tag);
        props.setHeader("x-first-death-reason", "rejected");
        props.setHeader("x-first-death-queue", "cola-origen");
        byte[] cuerpo = "{\"eventoId\":\"abc-123\"}".getBytes(StandardCharsets.UTF_8);
        return new Message(cuerpo, props);
    }

    @Test
    void asistencia_registraYConfirma() throws IOException {
        listener.asistencia(mensaje(1L), channel);
        verify(channel).basicAck(1L, false);
    }

    @Test
    void calificacion_registraYConfirma() throws IOException {
        listener.calificacion(mensaje(2L), channel);
        verify(channel).basicAck(2L, false);
    }

    @Test
    void hojaVida_registraYConfirma() throws IOException {
        listener.hojaVida(mensaje(3L), channel);
        verify(channel).basicAck(3L, false);
    }
}