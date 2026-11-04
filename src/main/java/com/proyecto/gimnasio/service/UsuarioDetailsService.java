package com.proyecto.gimnasio.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.proyecto.gimnasio.model.Usuario;
import com.proyecto.gimnasio.repository.UsuarioRepository;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository repo;

    public UsuarioDetailsService(UsuarioRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Usuario c = repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario no existe: " + email));

        return User.withUsername(c.getEmail())
                .password(c.getPassword())
                .roles(c.getRol())
                .disabled(!c.isActivo())
                .build();
    }

}