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
            BindingResult errores, RedirectAttributes flash) {

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
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/clientes/perfil";
        }
    }
}
