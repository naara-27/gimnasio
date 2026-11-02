
package com.proyecto.gimnasio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.gimnasio.model.Reservacion;

public interface ReservacionRepository extends JpaRepository<Reservacion, Long> {

    // Consultar reservaciones de un cliente
    List<Reservacion> findByClienteId(Long clienteId);

    // Consultar reservaciones de una clase
    List<Reservacion> findByClaseIdAndEstado(Long claseId, String estado);

    // Verificar si el cliente ya reservó una clase con ese nombre
    boolean existsByClienteIdAndNombreClaseAndEstado(
            Long clienteId,
            String nombreClase,
            String estado);

    // Contar reservaciones confirmadas de una clase
    long countByClaseIdAndEstado(Long claseId, String estado);
}

