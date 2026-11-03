package com.proyecto.gimnasio.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.gimnasio.model.Cliente;
import com.proyecto.gimnasio.repository.ClienteRepository;

@Service
public class ClienteService {

    private final ClienteRepository repo;
    private final PasswordEncoder passwordEncoder;

    public ClienteService(ClienteRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Cliente> listar() {
        return repo.findAll();
    }

    public List<Cliente> buscar(String q) {
        if (q == null || q.isBlank()) {
            return repo.findAll();
        }
        return repo.findByNombreContainingIgnoreCaseOrCedulaContainingIgnoreCase(q, q);
    }

    public Cliente buscarPorId(Long id) {
        return repo.findById(id).orElse(null);
    }

    public boolean existeEmail(String email) {
        return repo.existsByEmail(email);
    }

    public Cliente buscarPorEmail(String email) {
        return repo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("El cliente con email " + email + " no existe."));
    }

    @Transactional
    public void actualizarPassword(String email, String nuevaPassword) {

        Cliente cliente = repo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("El correo no se encuentra registrado."));

        if (nuevaPassword == null || nuevaPassword.isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria.");
        }

        cliente.setPassword(passwordEncoder.encode(nuevaPassword));
        repo.save(cliente);
    }

    @Transactional
    public Cliente crear(Cliente cliente) {

        if (repo.existsByCedula(cliente.getCedula())) {
            throw new IllegalArgumentException("La cédula ya está registrada");
        }

        if (repo.existsByEmail(cliente.getEmail())) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }

        if (cliente.getPassword() == null || cliente.getPassword().isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }

        cliente.setPassword(passwordEncoder.encode(cliente.getPassword()));

        return repo.save(cliente);
    }

    @Transactional
    public Cliente actualizar(Long id, Cliente datos) {

        Cliente existente = buscarPorId(id);

        if (existente == null) {
            throw new IllegalArgumentException("Cliente no encontrado " + id);
        }

        if (!existente.getCedula().equals(datos.getCedula())
                && repo.existsByCedula(datos.getCedula())) {
            throw new IllegalArgumentException("La cédula ya está registrada");
        }

        if (!existente.getEmail().equals(datos.getEmail())
                && repo.existsByEmail(datos.getEmail())) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }

        existente.setNombre(datos.getNombre());
        existente.setCedula(datos.getCedula());
        existente.setEmail(datos.getEmail());
        existente.setRol(datos.getRol());
        existente.setTelefono(datos.getTelefono());
        existente.setActivo(datos.isActivo());

        if (datos.getPassword() != null && !datos.getPassword().isBlank()) {
            existente.setPassword(passwordEncoder.encode(datos.getPassword()));
        }

        return repo.save(existente);
    }

    @Transactional
    public void eliminar(Long id, String emailActual) {

        Cliente existente = buscarPorId(id);

        if (existente == null) {
            throw new IllegalArgumentException("Cliente no encontrado " + id);
        }

        if (existente.getEmail().equals(emailActual)
                && existente.getRol().equals("ADMIN")) {
            throw new IllegalArgumentException("No puedes eliminar tu propia cuenta de administrador");
        }

        repo.deleteById(id);
    }

}