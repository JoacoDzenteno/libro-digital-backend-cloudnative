package cl.bernardo.ohiggins.ms_kafka_admin.exception;

import cl.bernardo.ohiggins.ms_kafka_admin.dto.RespuestaError;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Convierte las excepciones de todos los controllers en respuestas JSON
 * con el código HTTP correspondiente y un mensaje claro.
 */
@Slf4j
@RestControllerAdvice
public class ManejadorGlobalErrores {

    /** DTO inválido en el cuerpo JSON (@Valid @RequestBody). */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<RespuestaError> datosInvalidos(BindException ex, HttpServletRequest request) {
        List<String> detalles = ex.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        return responder(HttpStatus.BAD_REQUEST, "Datos inválidos", detalles, request);
    }

    /** Parámetro inválido en la URL (@PathVariable con @Pattern), o cuerpo inválido junto a uno de ellos. */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<RespuestaError> parametrosInvalidos(HandlerMethodValidationException ex,
                                                              HttpServletRequest request) {
        List<String> detalles = ex.getAllErrors().stream()
                .map(this::describir)
                .toList();
        return responder(HttpStatus.BAD_REQUEST, "Datos inválidos", detalles, request);
    }

    /** JSON mal escrito, o un número enviado como texto. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RespuestaError> cuerpoIlegible(HttpMessageNotReadableException ex,
                                                         HttpServletRequest request) {
        return responder(HttpStatus.BAD_REQUEST, "Cuerpo de la petición inválido",
                List.of("Revise que el JSON esté bien escrito y que particiones y replicas sean números"), request);
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<RespuestaError> noEncontrado(RecursoNoEncontradoException ex, HttpServletRequest request) {
        return responder(HttpStatus.NOT_FOUND, "Recurso no encontrado", List.of(ex.getMessage()), request);
    }

    @ExceptionHandler(RecursoDuplicadoException.class)
    public ResponseEntity<RespuestaError> duplicado(RecursoDuplicadoException ex, HttpServletRequest request) {
        return responder(HttpStatus.CONFLICT, "Recurso duplicado", List.of(ex.getMessage()), request);
    }

    @ExceptionHandler(OperacionInvalidaException.class)
    public ResponseEntity<RespuestaError> operacionInvalida(OperacionInvalidaException ex, HttpServletRequest request) {
        return responder(HttpStatus.BAD_REQUEST, "Operación inválida", List.of(ex.getMessage()), request);
    }

    @ExceptionHandler(KafkaNoDisponibleException.class)
    public ResponseEntity<RespuestaError> kafkaNoDisponible(KafkaNoDisponibleException ex, HttpServletRequest request) {
        return responder(HttpStatus.SERVICE_UNAVAILABLE, "Kafka no disponible", List.of(ex.getMessage()), request);
    }

    // ---------- Apoyo ----------

    private String describir(MessageSourceResolvable error) {
        // Si el error viene de un campo del DTO, se antepone el nombre del campo
        return error instanceof FieldError campo
                ? campo.getField() + ": " + campo.getDefaultMessage()
                : error.getDefaultMessage();
    }

    private ResponseEntity<RespuestaError> responder(HttpStatus status, String error,
                                                     List<String> detalles, HttpServletRequest request) {
        log.warn("{} {} -> {} {}", request.getMethod(), request.getRequestURI(), status.value(), detalles);
        RespuestaError cuerpo = new RespuestaError(LocalDateTime.now(), status.value(), error, detalles,
                request.getRequestURI());
        return ResponseEntity.status(status).body(cuerpo);
    }
}