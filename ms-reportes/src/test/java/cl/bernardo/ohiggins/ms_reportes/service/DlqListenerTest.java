package cl.bernardo.ohiggins.ms_reportes.service;

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

    @Test
    void reporte_registraYConfirma() throws IOException {
        MessageProperties props = new MessageProperties();
        props.setDeliveryTag(1L);
        props.setHeader("x-first-death-reason", "rejected");
        props.setHeader("x-first-death-queue", "reportes-calificacion-queue");
        byte[] cuerpo = "{\"eventoId\":\"abc-123\"}".getBytes(StandardCharsets.UTF_8);

        listener.reporte(new Message(cuerpo, props), channel);

        verify(channel).basicAck(1L, false);
    }
}