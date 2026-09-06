package cl.bernardo.ohiggins.ms_usuarios.exception;

import cl.bernardo.ohiggins.ms_usuarios.dto.ErrorResponse;
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
        String mensaje = "Ya existe un registro con esos datos únicos";
        
        String detalle = ex.getMostSpecificCause().getMessage();
        if (detalle != null) {
            if (detalle.contains("rut")) {
                mensaje = "Ya existe un usuario con ese RUT";
            } else if (detalle.contains("(email)")) {
                mensaje = "Ya existe un usuario con ese email de acceso";
            } else if (detalle.contains("email")) {
                mensaje = "Ya existe un usuario con ese email personal";
            }
        }

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ErrorResponse.builder()
                        .status(409)
                        .error("Conflict")
                        .mensaje(mensaje)
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