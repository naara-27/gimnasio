package com.proyecto.gimnasio.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.gimnasio.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByCedula(String cedula);

    Optional<Usuario> findByCedula(String cedula);

    List<Usuario> findByNombreContainingIgnoreCaseOrCedulaContainingIgnoreCase(String nombre, String cedula);

    List<Usuario> findByRolAndActivoTrue(String rol);
}
