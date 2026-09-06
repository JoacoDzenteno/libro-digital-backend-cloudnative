package cl.bernardo.ohiggins.ms_comunicaciones.dto;

import cl.bernardo.ohiggins.ms_comunicaciones.model.Mensaje;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MensajeDTO {
    private Long id;
    private Long idRemitente;
    private Long idDestinatario;
    private String asunto;
    private String contenido;
    private LocalDateTime fechaEnvio;
    private Mensaje.TipoMensaje tipo;
    private Boolean leido;
}