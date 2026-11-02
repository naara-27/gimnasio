
package com.proyecto.gimnasio.service;

import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.proyecto.gimnasio.model.ClaseGimnasio;
import com.proyecto.gimnasio.repository.ClaseGimnasioRepository;

@Service
public class ClaseGimnasioService {

    private final ClaseGimnasioRepository repository;

    public ClaseGimnasioService(ClaseGimnasioRepository repository) {
        this.repository = repository;
    }

    // Listar todas las clases
    public List<ClaseGimnasio> listar() {
        return repository.findAll();
    }

    // Buscar una clase por su identificador
    public ClaseGimnasio buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontró la clase"));
    }

    // Buscar clases por nombre o tipo
    public List<ClaseGimnasio> buscar(String q) {
        if (q == null || q.isBlank()) {
            return repository.findAll();
        }

        return repository
                .findByNombreContainingIgnoreCaseOrTipoContainingIgnoreCase(q, q);
    }

    // Guardar una clase
    public ClaseGimnasio guardar(ClaseGimnasio clase) {

        validarHorario(clase);

        clase.calcularHoraFinalizacion();

        return repository.save(clase);
    }

    // Validar que el entrenador no tenga horarios cruzados
    private void validarHorario(ClaseGimnasio clase) {

        if (clase.getFecha() == null) {
            throw new RuntimeException("Debe indicar la fecha de la clase");
        }

        if (clase.getHoraInicio() == null) {
            throw new RuntimeException("Debe indicar la hora de inicio");
        }

        if (clase.getEntrenador() == null) {
            throw new RuntimeException("Debe seleccionar un entrenador");
        }

        LocalTime horaInicio = clase.getHoraInicio();
        LocalTime horaFinal = horaInicio.plusHours(1);

        // Evitar que una clase pase al día siguiente
        if (!horaFinal.isAfter(horaInicio)) {
            throw new RuntimeException(
                    "La clase no puede finalizar en el día siguiente");
        }

        Long idClase = clase.getId();

        long cruces = repository.contarCrucesHorario(
                clase.getEntrenador().getId(),
                clase.getFecha(),
                horaInicio,
                horaFinal,
                idClase
        );

        if (cruces > 0) {
            throw new RuntimeException(
                    "El entrenador ya tiene otra clase asignada en ese horario");
        }
    }

    // Eliminar una clase
    public void eliminar(Long id) {
        ClaseGimnasio clase = buscarPorId(id);
        repository.delete(clase);
    }
}


