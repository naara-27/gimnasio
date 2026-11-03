package com.proyecto.gimnasio.controller;

import com.proyecto.gimnasio.model.Cliente;
import com.proyecto.gimnasio.service.ClienteService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PerfilController {

    private final ClienteService service;

    public PerfilController(ClienteService service) {
        this.service = service;
    }

    @GetMapping({"/perfil", "/clientes/perfil"})
    public String perfil(Authentication authentication, Model model) {
        String email = authentication.getName();
        Cliente cliente = service.buscarPorEmail(email);
        model.addAttribute("cliente", cliente);
        return "clientes/perfil";
    }
}
