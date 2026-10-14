package cl.bernardo.ohiggins.ms_reportes.exception;

import cl.bernardo.ohiggins.ms_reportes.dto.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.builder()
                        .status(409)
                        .error("Conflict")
                        .mensaje("Ya existe un registro con esos datos únicos")
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    // Error RECUPERABLE: un servicio externo (ms-usuarios) no responde.
    // Se informa como 503 (no disponible) y no como 500 (fallo interno).
    @ExceptionHandler(ServicioNoDisponibleException.class)
    public ResponseEntity<ErrorResponse> handleServicioNoDisponible(ServicioNoDisponibleException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ErrorResponse.builder()
                        .status(503)
                        .error("Service Unavailable")
                        .mensaje("Un servicio necesario no está disponible, intenta nuevamente más tarde")
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder()
                        .status(500)
                        .error("Internal Server Error")
                        .mensaje("Error interno del servidor: " + ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build());
    }
}