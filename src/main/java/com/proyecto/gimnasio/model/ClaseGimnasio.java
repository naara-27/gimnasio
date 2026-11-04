
package com.proyecto.gimnasio.model;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "clases")
public class ClaseGimnasio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre de la clase es obligatorio")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El tipo de clase es obligatorio")
    @Column(nullable = false, length = 60)
    private String tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entrenador_id", nullable = false)
    private Usuario entrenador;

    @NotNull(message = "La fecha es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(nullable = false)
    private LocalDate fecha;

    @NotNull(message = "La hora de inicio es obligatoria")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_finalizacion", nullable = false)
    private LocalTime horaFinalizacion;

    @NotNull(message = "El cupo máximo es obligatorio")
    @Min(value = 1, message = "El cupo debe ser al menos de una persona")
    @Column(name = "cupo_maximo", nullable = false)
    private Integer cupoMaximo;

    public ClaseGimnasio() {
    }

    // Calcular automáticamente una hora de duración
    @PrePersist
    @PreUpdate
    public void calcularHoraFinalizacion() {
        if (horaInicio != null) {
            horaFinalizacion = horaInicio.plusHours(1);
        }
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Usuario getEntrenador() {
        return entrenador;
    }

    public void setEntrenador(Usuario entrenador) {
        this.entrenador = entrenador;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
        calcularHoraFinalizacion();
    }

    public LocalTime getHoraFinalizacion() {
        return horaFinalizacion;
    }

    public Integer getCupoMaximo() {
        return cupoMaximo;
    }

    public void setCupoMaximo(Integer cupoMaximo) {
        this.cupoMaximo = cupoMaximo;
    }
}
