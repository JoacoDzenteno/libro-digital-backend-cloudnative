package cl.bernardo.ohiggins.ms_usuarios.dto;

import cl.bernardo.ohiggins.ms_usuarios.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Vista minima de un usuario, para elegir destinatario de un mensaje.
 * A diferencia de UsuarioDTO, no expone email, rut, telefono ni direccion:
 * por eso puede consultarla cualquier usuario autenticado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactoDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private Usuario.Rol rol;
}