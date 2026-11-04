package com.proyecto.gimnasio.controller;

import com.proyecto.gimnasio.model.Usuario;
import com.proyecto.gimnasio.service.UsuarioService;
import jakarta.validation.Valid;

import java.security.Principal;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String q, Model model) {
        model.addAttribute("usuarios", service.buscar(q));
        model.addAttribute("q", q);
        return "usuarios/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("cliente", new Usuario());
        model.addAttribute("modo", "crear");
        return "usuarios/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("cliente") Usuario cliente,
            BindingResult errores, Model model, RedirectAttributes flash) {

        if (errores.hasErrors()) {
            model.addAttribute("modo", "crear");
            return "usuarios/formulario";
        }

        try {
            service.crear(cliente);
            flash.addFlashAttribute("mensaje", "Cliente creado correctamente");
            return "redirect:/usuarios";
        } catch (IllegalArgumentException e) {
            model.addAttribute("modo", "crear");
            model.addAttribute("error", e.getMessage());
            return "usuarios/formulario";
        }
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        Usuario c = service.buscarPorId(id);
        c.setPassword("");
        model.addAttribute("cliente", c);
        model.addAttribute("modo", "editar");
        return "usuarios/formulario";
    }

    @PostMapping("/{id}/actualizar")
    public String actualizar(@PathVariable Long id,
            @Valid @ModelAttribute("cliente") Usuario cliente,
            BindingResult errores, Model model, RedirectAttributes flash) {

        if (errores.hasErrors()) {
            model.addAttribute("modo", "editar");
            return "usuarios/formulario";
        }

        try {
            service.actualizar(id, cliente);
            flash.addFlashAttribute("mensaje", "Cliente actualizado correctamente");
            return "redirect:/usuarios";
        } catch (IllegalArgumentException e) {
            model.addAttribute("modo", "editar");
            model.addAttribute("error", e.getMessage());
            return "usuarios/formulario";
        }
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id,
            RedirectAttributes flash,
            Principal principal) {

        try {
            service.eliminar(id, principal.getName());
            flash.addFlashAttribute("mensaje", "Cliente eliminado con éxito");
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/usuarios";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id,
            Model model,
            RedirectAttributes flash,
            Authentication authentication) {

        Usuario clienteAutenticado = service.buscarPorEmail(authentication.getName());
        boolean esAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // Validación de ownership: un cliente no-admin solo puede ver su propio
        // detalle.
        if (!esAdmin && !clienteAutenticado.getId().equals(id)) {
            flash.addFlashAttribute("error",
                    "No tiene permisos para ver el perfil de otro cliente.");
            return "redirect:/usuarios/perfil";
        }

        Usuario c = service.buscarPorId(id);

        if (c == null) {
            flash.addFlashAttribute("error",
                    "El cliente con ID " + id + " no existe.");
            return "redirect:/usuarios";
        }

        model.addAttribute("cliente", c);

        return "usuarios/detalle";
    }

    @GetMapping("/recuperar-password")
    public String recuperarForm(Model model) {
        model.addAttribute("email", "");
        return "recuperar-password";
    }

    @PostMapping("/recuperar-password")
    public String recuperar(@RequestParam String email,
            RedirectAttributes flash) {

        if (service.existeEmail(email)) {
            flash.addFlashAttribute("mensaje",
                    "El correo ingresado está registrado. Puede proceder a reestablecer su contraseña.");
            return "redirect:/usuarios/reset-password?email=" + email;
        }

        flash.addFlashAttribute("error",
                "El correo ingresado no se encuentra registrado.");
        return "redirect:/usuarios/recuperar-password";
    }

    @GetMapping("/reset-password")
    public String resetForm(@RequestParam(required = false) String email, Model model) {
        model.addAttribute("email", email != null ? email : "");
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            RedirectAttributes flash) {

        if (!password.equals(confirmPassword)) {
            flash.addFlashAttribute("error", "Las contraseñas no coinciden.");
            flash.addFlashAttribute("email", email);
            return "redirect:/usuarios/reset-password?email=" + email;
        }

        try {
            service.actualizarPassword(email, password);
            flash.addFlashAttribute("mensaje", "Contraseña actualizada correctamente.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("error", e.getMessage());
            flash.addFlashAttribute("email", email);
            return "redirect:/usuarios/reset-password?email=" + email;
        }
    }
}