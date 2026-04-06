package edu.udelar.ayudemos.distribucion.domain;

import edu.udelar.ayudemos.beneficiario.domain.Beneficiario;
import edu.udelar.ayudemos.donacion.domain.Donacion;
import edu.udelar.ayudemos.repartidor.domain.Repartidor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "distribuciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Distribucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La fecha de preparacion es obligatoria")
    @Column(nullable = false)
    private LocalDate fechaPreparacion;

    private LocalDate fechaEntrega;

    @NotNull(message = "El estado es obligatorio")
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
