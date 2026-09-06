package cl.bernardo.ohiggins.ms_usuarios.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarPerfilRequest {
    private String nuevoEmail;
    private String passwordActual;
    private String nuevaPassword;
}