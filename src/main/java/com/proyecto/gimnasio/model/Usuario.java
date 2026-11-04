package com.proyecto.gimnasio.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es requerido!")
    @Size(max = 100)
    private String nombre;

    @NotBlank(message = "La cédula es requerida!")
    @Column(unique = true, nullable = false)
    private String cedula;

    @NotBlank(message = "El email es requerido!")
    @Email(message = "Correo no valido!")
    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String rol = "CLIENTE";

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "\\d{8}", message = "El teléfono debe tener 8 dígitos")
    private String telefono;

    @Column(nullable = false)
    private boolean activo = true;

    public Usuario() {
    }

    public Usuario(String nombre, String cedula, String email, String password, String rol, String telefono,
            boolean activo) {
        this.nombre = nombre;
        this.cedula = cedula;
        this.email = email;
        this.password = password;
        this.rol = rol;
        this.telefono = telefono;
        this.activo = activo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}