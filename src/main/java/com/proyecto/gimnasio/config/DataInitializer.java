package com.proyecto.gimnasio.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.proyecto.gimnasio.model.Cliente;
import com.proyecto.gimnasio.repository.ClienteRepository;
import com.proyecto.gimnasio.service.ClienteService;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner cargarUsuarios(ClienteRepository repo, ClienteService service) {
        return args -> {
            if (repo.count() == 0) {
                service.crear(new Cliente(
                        "Administrador",
                        "101010101",
                        "admin@gimnasio.com",
                        "admin123",
                        "ADMIN",
                        "88888888",
                        true));

                service.crear(new Cliente(
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