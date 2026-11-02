
package com.proyecto.gimnasio.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.gimnasio.model.ClaseGimnasio;
import com.proyecto.gimnasio.model.Cliente;
import com.proyecto.gimnasio.model.Reservacion;
import com.proyecto.gimnasio.repository.ClaseGimnasioRepository;
import com.proyecto.gimnasio.repository.ClienteRepository;
import com.proyecto.gimnasio.repository.ReservacionRepository;

@Service
public class ReservacionService {

    private final ReservacionRepository reservacionRepository;
    private final ClienteRepository clienteRepository;
    private final ClaseGimnasioRepository claseRepository;

    public ReservacionService(
            ReservacionRepository reservacionRepository,
            ClienteRepository clienteRepository,
            ClaseGimnasioRepository claseRepository) {

        this.reservacionRepository = reservacionRepository;
        this.clienteRepository = clienteRepository;
        this.claseRepository = claseRepository;
    }

    // Listar todas las reservaciones
    public List<Reservacion> listar() {
        return reservacionRepository.findAll();
    }

    // Consultar reservaciones de un cliente
    public List<Reservacion> listarPorCliente(Long clienteId) {
        return reservacionRepository.findByClienteId(clienteId);
    }

    // Registrar una reservación
    @Transactional
    public Reservacion reservar(Long clienteId, Long claseId) {

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() ->
                        new RuntimeException("No se encontró el cliente"));

        ClaseGimnasio clase = claseRepository.findById(claseId)
                .orElseThrow(() ->
                        new RuntimeException("No se encontró la clase"));

        if (!cliente.isActivo()
                || !"CLIENTE".equalsIgnoreCase(cliente.getRol())) {
            throw new RuntimeException(
                    "Solo los clientes activos pueden reservar clases");
        }

        String nombreClase = clase.getNombre().trim().toLowerCase();

        boolean yaReservo = reservacionRepository
                .existsByClienteIdAndNombreClaseAndEstado(
                        clienteId, nombreClase, "CONFIRMADA");

        if (yaReservo) {
            throw new RuntimeException(
                    "Ya tiene una reservación para esta clase, aunque sea con otro entrenador");
        }

        long ocupados = reservacionRepository
                .countByClaseIdAndEstado(claseId, "CONFIRMADA");

        if (ocupados >= clase.getCupoMaximo()) {
            throw new RuntimeException(
                    "La clase ya alcanzó su cupo máximo");
        }

        Reservacion reservacion = new Reservacion();
        reservacion.setCliente(cliente);
        reservacion.setClase(clase);
        reservacion.setEstado("CONFIRMADA");

        return reservacionRepository.save(reservacion);
    }

    // Cancelar una reservación
    @Transactional
    public void cancelar(Long id) {

        Reservacion reservacion = reservacionRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("No se encontró la reservación"));

        reservacion.setEstado("CANCELADA");

        reservacionRepository.save(reservacion);
    }

    // Contar espacios ocupados
    public long contarOcupados(Long claseId) {
        return reservacionRepository
                .countByClaseIdAndEstado(claseId, "CONFIRMADA");
    }

    // Calcular espacios disponibles
    public long contarDisponibles(ClaseGimnasio clase) {
        long ocupados = contarOcupados(clase.getId());

        return Math.max(0, clase.getCupoMaximo() - ocupados);
    }

    
    @Transactional
    public void cancelar(Long id, String emailUsuario, boolean esAdministrador) {

    Reservacion reservacion = reservacionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException(
                    "No se encontró la reservación"
            ));

    // Verificar que el cliente sea dueño de la reservación
    if (!esAdministrador &&
            !reservacion.getCliente().getEmail()
                    .equalsIgnoreCase(emailUsuario)) {

        throw new RuntimeException(
                "No puede cancelar una reservación de otro cliente"
        );
    }

    if ("CANCELADA".equals(reservacion.getEstado())) {
        throw new RuntimeException(
                "Esta reservación ya fue cancelada"
        );
    }

    reservacion.setEstado("CANCELADA");

    reservacionRepository.save(reservacion);
}

}

