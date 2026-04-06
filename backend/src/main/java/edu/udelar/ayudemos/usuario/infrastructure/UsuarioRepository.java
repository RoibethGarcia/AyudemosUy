package edu.udelar.ayudemos.usuario.infrastructure;

import edu.udelar.ayudemos.usuario.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByCorreo(String correo);

    @Query("SELECT u FROM Usuario u WHERE TYPE(u) = Usuario")
    List<Usuario> findBaseUsuarios();

    @Query("SELECT u FROM Usuario u WHERE TYPE(u) = Usuario AND u.id = :id")
    Optional<Usuario> findBaseUsuarioById(@Param("id") Long id);
}