
package com.proyecto.gimnasio.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.proyecto.gimnasio.model.ClaseGimnasio;
import com.proyecto.gimnasio.model.Cliente;
import com.proyecto.gimnasio.repository.ClienteRepository;
import com.proyecto.gimnasio.service.ClaseGimnasioService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/clases")
public class ClaseController {

    private final ClaseGimnasioService service;
    private final ClienteRepository clienteRepository;

    public ClaseController(
            ClaseGimnasioService service,
            ClienteRepository clienteRepository) {

        this.service = service;
        this.clienteRepository = clienteRepository;
    }

    // Listar y buscar clases
    @GetMapping
    public String listar(
            @RequestParam(required = false) String q,
            Model model) {

        model.addAttribute("clases", service.buscar(q));
        model.addAttribute("q", q);

        return "clases/lista";
    }

    // Mostrar formulario para crear una clase
    @GetMapping("/nuevo")
    public String nuevo(Model model) {

        model.addAttribute("clase", new ClaseGimnasio());
        model.addAttribute("entrenadores", obtenerEntrenadores());
        model.addAttribute("modo", "crear");

        return "clases/formulario";
    }

    // Guardar una clase nueva
    @PostMapping("/guardar")
    public String guardar(
            @Valid @ModelAttribute("clase") ClaseGimnasio clase,
            BindingResult errores,
            @RequestParam(required = false) Long entrenadorId,
            Model model,
            RedirectAttributes flash) {

        if (entrenadorId == null) {
            errores.rejectValue(
                    "entrenador",
                    "entrenador.requerido",
                    "Debe seleccionar un entrenador");
        } else {
            asignarEntrenador(clase, entrenadorId, errores);
        }

        if (errores.hasErrors()) {
            model.addAttribute("entrenadores", obtenerEntrenadores());
            model.addAttribute("modo", "crear");
            return "clases/formulario";
        }

        try {
            service.guardar(clase);
            flash.addFlashAttribute("ok", "Clase registrada correctamente");
            return "redirect:/clases";

        } catch (RuntimeException e) {
            model.addAttribute("entrenadores", obtenerEntrenadores());
            model.addAttribute("modo", "crear");
            model.addAttribute("error", e.getMessage());
            return "clases/formulario";
        }
    }

    // Mostrar formulario para editar
    @GetMapping("/{id}/editar")
    public String editar(
            @PathVariable Long id,
            Model model,
            RedirectAttributes flash) {

        try {
            model.addAttribute("clase", service.buscarPorId(id));
            model.addAttribute("entrenadores", obtenerEntrenadores());
            model.addAttribute("modo", "editar");

            return "clases/formulario";

        } catch (RuntimeException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/clases";
        }
    }

    // Actualizar una clase
    @PostMapping("/{id}/actualizar")
    public String actualizar(
            @PathVariable Long id,
            @Valid @ModelAttribute("clase") ClaseGimnasio datos,
            BindingResult errores,
            @RequestParam(required = false) Long entrenadorId,
            Model model,
            RedirectAttributes flash) {

        if (entrenadorId == null) {
            errores.rejectValue(
                    "entrenador",
                    "entrenador.requerido",
                    "Debe seleccionar un entrenador");
        } else {
            asignarEntrenador(datos, entrenadorId, errores);
        }

        if (errores.hasErrors()) {
            model.addAttribute("entrenadores", obtenerEntrenadores());
            model.addAttribute("modo", "editar");
            return "clases/formulario";
        }

        try {
            ClaseGimnasio existente = service.buscarPorId(id);

            existente.setNombre(datos.getNombre());
            existente.setTipo(datos.getTipo());
            existente.setEntrenador(datos.getEntrenador());
            existente.setFecha(datos.getFecha());
            existente.setHoraInicio(datos.getHoraInicio());
            existente.setCupoMaximo(datos.getCupoMaximo());

            service.guardar(existente);

            flash.addFlashAttribute("ok", "Clase actualizada correctamente");
            return "redirect:/clases";

        } catch (RuntimeException e) {
            model.addAttribute("entrenadores", obtenerEntrenadores());
            model.addAttribute("modo", "editar");
            model.addAttribute("error", e.getMessage());
            return "clases/formulario";
        }
    }

    // Consultar detalles de una clase
    @GetMapping("/{id}")
    public String detalle(
            @PathVariable Long id,
            Model model,
            RedirectAttributes flash) {

        try {
            model.addAttribute("clase", service.buscarPorId(id));
            return "clases/detalle";

        } catch (RuntimeException e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/clases";
        }
    }

    // Eliminar una clase
    @PostMapping("/{id}/eliminar")
    public String eliminar(
            @PathVariable Long id,
            RedirectAttributes flash) {

        try {
            service.eliminar(id);
            flash.addFlashAttribute("ok", "Clase eliminada correctamente");

        } catch (RuntimeException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/clases";
    }

    // Obtener entrenadores activos
    private List<Cliente> obtenerEntrenadores() {

    List<Cliente> entrenadores =
            clienteRepository.findByRolAndActivoTrue("ENTRENADOR");

    System.out.println("ENTRENADORES ENCONTRADOS: " + entrenadores.size());

    for (Cliente entrenador : entrenadores) {
        System.out.println(
                "ID: " + entrenador.getId()
                + " - Nombre: " + entrenador.getNombre()
                + " - Rol: " + entrenador.getRol()
                + " - Activo: " + entrenador.isActivo()
        );
    }

    return entrenadores;
    }   

    // Comprobar que el usuario seleccionado sea un entrenador activo
    private void asignarEntrenador(
            ClaseGimnasio clase,
            Long entrenadorId,
            BindingResult errores) {

        Cliente entrenador = clienteRepository.findById(entrenadorId)
                .orElse(null);

        if (entrenador == null
                || !entrenador.isActivo()
                || !"ENTRENADOR".equalsIgnoreCase(entrenador.getRol())) {

            errores.rejectValue(
                    "entrenador",
                    "entrenador.invalido",
                    "El entrenador seleccionado no es válido o está inactivo");

        } else {
            clase.setEntrenador(entrenador);
        }
    }
}
