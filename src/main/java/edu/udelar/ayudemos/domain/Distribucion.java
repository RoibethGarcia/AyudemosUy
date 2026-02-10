package edu.udelar.ayudemos.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "distribuciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Distribucion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "La fecha de preparación es obligatoria")
    @Column(nullable = false)
    private LocalDate fechaPreparacion;
    
    private LocalDate fechaEntrega;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoDistribucion estado;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beneficiario_id", nullable = false)
    @NotNull(message = "El beneficiario es obligatorio")
    private Beneficiario beneficiario;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "distribucion_donacion",
        joinColumns = @JoinColumn(name = "distribucion_id"),
        inverseJoinColumns = @JoinColumn(name = "donacion_id")
    )
    private List<Donacion> donaciones = new ArrayList<>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repartidor_id")
    private Repartidor repartidor;
}