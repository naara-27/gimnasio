
package com.proyecto.gimnasio.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "reservaciones")
public class Reservacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Debe seleccionar un cliente")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    @NotNull(message = "Debe seleccionar una clase")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clase_id", nullable = false)
    private ClaseGimnasio clase;

    @Column(name = "nombre_clase", nullable = false)
    private String nombreClase;

    @Column(nullable = false)
    private String estado = "CONFIRMADA";

    @Column(name = "fecha_reservacion", nullable = false)
    private LocalDateTime fechaReservacion;

    public Reservacion() {
    }

    @PrePersist
    public void antesDeGuardar() {
        if (fechaReservacion == null) {
            fechaReservacion = LocalDateTime.now();
        }

        if (clase != null) {
            nombreClase = clase.getNombre().trim().toLowerCase();
        }
    }

    public Long getId() {
        return id;
    }

    public Usuario getCliente() {
        return cliente;
    }

    public void setCliente(Usuario cliente) {
        this.cliente = cliente;
    }

    public ClaseGimnasio getClase() {
        return clase;
    }

    public void setClase(ClaseGimnasio clase) {
        this.clase = clase;
        if (clase != null && clase.getNombre() != null) {
            this.nombreClase = clase.getNombre().trim().toLowerCase();
        }
    }

    public String getNombreClase() {
        return nombreClase;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaReservacion() {
        return fechaReservacion;
    }
}
