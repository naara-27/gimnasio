
package com.proyecto.gimnasio.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.proyecto.gimnasio.model.Cliente;
import com.proyecto.gimnasio.repository.ClaseGimnasioRepository;
import com.proyecto.gimnasio.repository.ClienteRepository;
import com.proyecto.gimnasio.service.ReservacionService;

@Controller
public class ReservacionController {

    private final ReservacionService service;
    private final ClienteRepository clienteRepository;
    private final ClaseGimnasioRepository claseRepository;

    public ReservacionController(
            ReservacionService service,
            ClienteRepository clienteRepository,
            ClaseGimnasioRepository claseRepository) {

        this.service = service;
        this.clienteRepository = clienteRepository;
        this.claseRepository = claseRepository;
    }

    // Lista todas las reservaciones (administrador)
    @GetMapping("/reservaciones")
    public String listar(Model model) {
        model.addAttribute("reservaciones", service.listar());
        return "reservaciones/lista";
    }

    // Lista únicamente las reservaciones del usuario autenticado
    @GetMapping("/reservaciones/mis-reservaciones")
    public String misReservaciones(
            Authentication authentication, Model model) {

        Cliente cliente = clienteRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("No se encontró el cliente"));

        model.addAttribute(
                "reservaciones",
                service.listarPorCliente(cliente.getId())
        );

        return "reservaciones/lista";
    }

    // Muestra las clases disponibles para reservar
    
    @GetMapping("/reservaciones/nueva")
    public String nueva(Model model) {

        var clases = claseRepository.findAll();

        var clasesDisponibles = clases.stream()
            .filter(clase -> service.contarDisponibles(clase) > 0)
            .toList();

        model.addAttribute("clases", clasesDisponibles);

        model.addAttribute(
            "cuposDisponibles",
            clasesDisponibles.stream().collect(
                    java.util.stream.Collectors.toMap(
                            clase -> clase.getId(),
                            clase -> service.contarDisponibles(clase)
                    )
            )
    );

    return "reservaciones/nueva";
    }


    // Reserva una clase utilizando el cliente que inició sesión
    @PostMapping("/reservaciones/guardar")
    public String reservar(
            @RequestParam Long claseId,
            Authentication authentication,
            RedirectAttributes flash) {

        try {
            Cliente cliente = clienteRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("No se encontró el cliente"));

            service.reservar(cliente.getId(), claseId);

            flash.addFlashAttribute(
                    "ok",
                    "¡Tu cupo fue reservado correctamente!"
            );

        } catch (RuntimeException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/reservaciones/mis-reservaciones";
    }

    // Cancela una reservación
    @PostMapping("/reservaciones/{id}/cancelar")
    public String cancelar(
        @PathVariable Long id,
        Authentication authentication,
        RedirectAttributes flash) {

    boolean esAdministrador = authentication.getAuthorities()
            .stream()
            .anyMatch(autoridad ->
                    autoridad.getAuthority().equals("ROLE_ADMIN"));

    try {
        service.cancelar(
                id,
                authentication.getName(),
                esAdministrador
        );

        flash.addFlashAttribute(
                "ok",
                "Reservación cancelada correctamente"
        );

    } catch (RuntimeException e) {
        flash.addFlashAttribute("error", e.getMessage());
    }

    if (esAdministrador) {
        return "redirect:/reservaciones";
    }

    return "redirect:/reservaciones/mis-reservaciones";
    }

}
