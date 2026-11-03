package com.proyecto.gimnasio.controller;

import com.proyecto.gimnasio.model.Cliente;
import com.proyecto.gimnasio.service.ClienteService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PerfilController {

    private final ClienteService service;

    public PerfilController(ClienteService service) {
        this.service = service;
    }

    @GetMapping({ "/perfil", "/clientes/perfil" })
    public String perfil(Authentication authentication, Model model) {
        String email = authentication.getName();
        Cliente cliente = service.buscarPorEmail(email);
        model.addAttribute("cliente", cliente);
        return "clientes/perfil";
    }

    @PostMapping({ "/perfil/actualizar", "/clientes/perfil/actualizar" })
    public String actualizarPerfil(@ModelAttribute("cliente") Cliente cliente,
            @RequestParam(required = false) String nuevaPassword,
            @RequestParam(required = false) String confirmPassword,
            Authentication authentication,
            BindingResult errores, RedirectAttributes flash) {

        // Validación de ownership: el ID enviado en el formulario debe coincidir
        // estrictamente con el usuario autenticado. Esto previene manipulaciones
        // de parámetros HTTP (IDs alterados en campos ocultos).
        Cliente clienteAutenticado = service.buscarPorEmail(authentication.getName());
        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!esAdmin && !clienteAutenticado.getId().equals(cliente.getId())) {
            errores.reject("error.perfil",
                    "No tiene permisos para modificar el perfil de otro cliente.");
            return "clientes/perfil";
        }

        if (cliente.getNombre() == null || cliente.getNombre().isBlank()) {
            errores.rejectValue("nombre", "error.nombre", "El nombre es requerido");
        }
        if (cliente.getCedula() == null || cliente.getCedula().isBlank()) {
            errores.rejectValue("cedula", "error.cedula", "La cédula es requerida");
        }
        if (cliente.getTelefono() == null || cliente.getTelefono().isBlank()) {
            errores.rejectValue("telefono", "error.telefono", "El teléfono es requerido");
        }

        if (errores.hasErrors()) {
            return "clientes/perfil";
        }

        if (nuevaPassword != null && !nuevaPassword.isBlank()) {
            if (confirmPassword == null || !nuevaPassword.equals(confirmPassword)) {
                errores.reject("error.password", "Las contraseñas no coinciden.");
                return "clientes/perfil";
            }
        }

        try {
            service.actualizarPerfilCliente(cliente, nuevaPassword);
            flash.addFlashAttribute("mensaje", "Perfil actualizado correctamente.");
            return "redirect:/clientes/perfil";
        } catch (IllegalArgumentException e) {
            String mensaje = e.getMessage();
            if (mensaje != null && mensaje.contains("cédula")) {
                errores.rejectValue("cedula", "error.cedula", mensaje);
            } else if (mensaje != null && mensaje.contains("correo")) {
                errores.rejectValue("email", "error.email", mensaje);
            } else {
                errores.reject("error.global", mensaje);
            }
            return "clientes/perfil";
        }
    }
}
