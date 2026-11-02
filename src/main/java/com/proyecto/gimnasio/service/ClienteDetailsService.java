package com.proyecto.gimnasio.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.proyecto.gimnasio.model.Cliente;
import com.proyecto.gimnasio.repository.ClienteRepository;

@Service
public class ClienteDetailsService implements UserDetailsService {

    private final ClienteRepository repo;

    public ClienteDetailsService(ClienteRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Cliente c = repo.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("El usuario no existe: " + email));

        return User.withUsername(c.getEmail())
                .password(c.getPassword())
                .roles(c.getRol())
                .disabled(!c.isActivo())
                .build();
    }

}