package cl.bernardo.ohiggins.ms_comunicaciones.service;

import cl.bernardo.ohiggins.ms_comunicaciones.exception.ApoderadoNoEncontradoException;
import cl.bernardo.ohiggins.ms_comunicaciones.exception.ServicioNoDisponibleException;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Confirma (ACK) o rechaza (NACK) cada mensaje según el tipo de error.
 *
 * - Éxito: basicAck, el mensaje se borra de la cola.
 * - Error RECUPERABLE (ServicioNoDisponibleException): ms-matricula puede volver,
 *   así que se reintenta. Si se agotan los intentos, basicNack sin reencolar y
 *   el mensaje pasa a la DLQ.
 * - Error NO RECUPERABLE (ApoderadoNoEncontradoException): los datos no van a
 *   cambiar, así que no se reintenta. basicNack sin reencolar, directo a la DLQ.
 * - Cualquier otro error inesperado: se trata como no recuperable para evitar
 *   un loop infinito con un mensaje que siempre falla.
 *
 * Nunca se usa requeue=true: reencolar un mensaje que siempre falla lo haría
 * volver sin fin (mensaje venenoso).
 */
@Slf4j
@Component
public class ConfirmadorMensajes {

    @Value("${mensajeria.reintentos.max:3}")
    private int maxIntentos = 3;

    @Value("${mensajeria.reintentos.espera-ms:2000}")
    private long esperaMs = 2000;

    public void procesar(String origen, Object eventoId, Channel channel, long tag, Runnable accion)
            throws IOException {

        for (int intento = 1; intento <= maxIntentos; intento++) {
            try {
                accion.run();
            } catch (ServicioNoDisponibleException e) {
                // RECUPERABLE: se reintenta
                log.warn("[{}] eventoId={} servicio no disponible (intento {}/{}): {}",
                        origen, eventoId, intento, maxIntentos, e.getMessage());
                if (intento < maxIntentos) {
                    esperar();
                }
                continue;
            } catch (ApoderadoNoEncontradoException e) {
                // NO RECUPERABLE: directo a la DLQ
                log.error("[{}] eventoId={} enviado a la DLQ (no recuperable): {}",
                        origen, eventoId, e.getMessage());
                channel.basicNack(tag, false, false);
                return;
            } catch (RuntimeException e) {
                // Inesperado: se trata como no recuperable
                log.error("[{}] eventoId={} enviado a la DLQ (error inesperado)", origen, eventoId, e);
                channel.basicNack(tag, false, false);
                return;
            }

            channel.basicAck(tag, false);
            return;
        }

        // Se agotaron los reintentos del error recuperable
        log.error("[{}] eventoId={} enviado a la DLQ (reintentos agotados)", origen, eventoId);
        channel.basicNack(tag, false, false);
    }

    private void esperar() {
        try {
            Thread.sleep(esperaMs);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}