package com.proyecto.gimnasio.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.proyecto.gimnasio.model.Usuario;
import com.proyecto.gimnasio.repository.UsuarioRepository;
import com.proyecto.gimnasio.service.UsuarioService;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner cargarUsuarios(UsuarioRepository repo, UsuarioService service) {
        return args -> {
            if (repo.count() == 0) {
                service.crear(new Usuario(
                        "Administrador",
                        "101010101",
                        "admin@gimnasio.com",
                        "admin123",
                        "ADMIN",
                        "88888888",
                        true));

                service.crear(new Usuario(
                        "Maria",
                        "202020202",
                        "maria@gimnasio.com",
                        "maria123",
                        "CLIENTE",
                        "77777777",
                        true));
            }
        };
    }
}