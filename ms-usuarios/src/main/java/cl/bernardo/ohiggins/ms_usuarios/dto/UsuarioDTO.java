package cl.bernardo.ohiggins.ms_usuarios.dto;

import cl.bernardo.ohiggins.ms_usuarios.model.Usuario;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioDTO {
    private Long id;
    private String email;
    private Usuario.Rol rol;
    private String nombre;
    private String apellido;
    private String rut;
    private String emailPersona;
    private String telefono;
    private String direccion;
}