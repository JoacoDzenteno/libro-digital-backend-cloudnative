package cl.bernardo.ohiggins.ms_usuarios.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private Long id;
    private String token;
    private String email;
    private String rol;
    private String nombre;
    private String apellido;
}