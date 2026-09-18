package cl.bernardo.ohiggins.ms_usuarios.repository;

import cl.bernardo.ohiggins.ms_usuarios.model.Usuario;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    Optional<Usuario> findByAzureOid(String azureOid);
    List<Usuario> findByRol(Usuario.Rol rol);

    @Query("SELECT u FROM Usuario u JOIN u.persona p WHERE " +
           "(:nombre IS NULL OR LOWER(CONCAT(p.nombre, ' ', p.apellido)) LIKE LOWER(CONCAT('%', CAST(:nombre AS string), '%'))) AND " +
           "(:rut IS NULL OR LOWER(p.rut) LIKE LOWER(CONCAT('%', CAST(:rut AS string), '%'))) AND " +
           "(:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', CAST(:email AS string), '%')))")
    List<Usuario> buscarCombinado(@Param("nombre") String nombre,
                                    @Param("rut") String rut,
                                    @Param("email") String email,
                                    Pageable pageable);
}