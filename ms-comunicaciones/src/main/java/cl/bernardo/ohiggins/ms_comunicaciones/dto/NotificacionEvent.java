package cl.bernardo.ohiggins.ms_comunicaciones.dto;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificacionEvent implements Serializable {
    private Long idEstudiante;
    private String asunto;
    private String contenido;
    private String tipoEvento;
}