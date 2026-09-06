package cl.bernardo.ohiggins.ms_usuarios.repository;

import cl.bernardo.ohiggins.ms_usuarios.model.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {
    Optional<Persona> findByRut(String rut);
    Optional<Persona> findByEmail(String email);
}