package cl.bernardo.ohiggins.ms_rabbit_admin.exception;

import cl.bernardo.ohiggins.ms_rabbit_admin.dto.RespuestaError;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpConnectException;
import org.springframework.amqp.AmqpException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Convierte las excepciones de todos los controllers en respuestas JSON
 * con el código HTTP correspondiente y un mensaje claro.
 */
@RestControllerAdvice
public class ManejadorGlobalErrores {

    private static final Logger log = LoggerFactory.getLogger(ManejadorGlobalErrores.class);

    /** DTO inválido, en el cuerpo JSON (@RequestBody) o en los query params (@ModelAttribute). */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<RespuestaError> datosInvalidos(BindException ex, HttpServletRequest request) {
        List<String> detalles = ex.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        return responder(HttpStatus.BAD_REQUEST, "Datos inválidos", detalles, request);
    }

    /** Parámetro de la ruta inválido. Ej: DELETE /queues/{nombre} con caracteres no permitidos. */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<RespuestaError> parametroInvalido(HandlerMethodValidationException ex,
                                                            HttpServletRequest request) {
        List<String> detalles = ex.getAllErrors().stream()
                .map(MessageSourceResolvable::getDefaultMessage)
                .toList();
        return responder(HttpStatus.BAD_REQUEST, "Parámetro inválido", detalles, request);
    }

    /** JSON mal formado o valor no permitido (ej: "tipo": "OTRO"). */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RespuestaError> jsonIlegible(HttpMessageNotReadableException ex,
                                                       HttpServletRequest request) {
        return responder(HttpStatus.BAD_REQUEST, "Cuerpo de la petición inválido",
                List.of("Revise que el JSON esté bien escrito y que 'tipo' sea DIRECT, TOPIC o FANOUT"),
                request);
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<RespuestaError> noEncontrado(RecursoNoEncontradoException ex, HttpServletRequest request) {
        return responder(HttpStatus.NOT_FOUND, "Recurso no encontrado", List.of(ex.getMessage()), request);
    }

    @ExceptionHandler(RecursoDuplicadoException.class)
    public ResponseEntity<RespuestaError> duplicado(RecursoDuplicadoException ex, HttpServletRequest request) {
        return responder(HttpStatus.CONFLICT, "Recurso duplicado", List.of(ex.getMessage()), request);
    }

    /** RabbitMQ apagado o inalcanzable. */
    @ExceptionHandler(AmqpConnectException.class)
    public ResponseEntity<RespuestaError> rabbitNoDisponible(AmqpConnectException ex, HttpServletRequest request) {
        log.error("No se pudo conectar con RabbitMQ: {}", ex.getMessage());
        return responder(HttpStatus.SERVICE_UNAVAILABLE, "RabbitMQ no disponible",
                List.of("No se pudo conectar con RabbitMQ. Intente más tarde."), request);
    }

    /** Cualquier otro error de RabbitMQ (ej: argumentos incompatibles con una cola existente). */
    @ExceptionHandler(AmqpException.class)
    public ResponseEntity<RespuestaError> errorRabbit(AmqpException ex, HttpServletRequest request) {
        log.error("Error de RabbitMQ: {}", ex.getMessage());
        return responder(HttpStatus.BAD_GATEWAY, "Error al operar sobre RabbitMQ",
                List.of(ex.getMessage()), request);
    }

    private ResponseEntity<RespuestaError> responder(HttpStatus status, String error,
                                                     List<String> detalles, HttpServletRequest request) {
        RespuestaError cuerpo = new RespuestaError(
                LocalDateTime.now(), status.value(), error, detalles, request.getRequestURI());
        return ResponseEntity.status(status).body(cuerpo);
    }
}
