package com.proyecto.gimnasio.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.gimnasio.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByCedula(String cedula);

    List<Cliente> findByNombreContainingIgnoreCaseOrCedulaContainingIgnoreCase(String nombre, String cedula);
}
