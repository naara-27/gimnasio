

package com.proyecto.gimnasio.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.proyecto.gimnasio.model.ClaseGimnasio;

public interface ClaseGimnasioRepository extends JpaRepository<ClaseGimnasio, Long> {

    List<ClaseGimnasio> findByNombreContainingIgnoreCaseOrTipoContainingIgnoreCase(
            String nombre, String tipo);

    @Query("""
        SELECT COUNT(c)
        FROM ClaseGimnasio c
        WHERE c.entrenador.id = :entrenadorId
        AND c.fecha = :fecha
        AND (:claseId IS NULL OR c.id <> :claseId)
        AND c.horaInicio < :horaFinal
        AND c.horaFinalizacion > :horaInicio
        """)
    long contarCrucesHorario(
            @Param("entrenadorId") Long entrenadorId,
            @Param("fecha") LocalDate fecha,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFinal") LocalTime horaFinal,
            @Param("claseId") Long claseId);
}

