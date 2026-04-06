package edu.udelar.ayudemos.donacion.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "articulos")
@PrimaryKeyJoinColumn(name = "donacion_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Articulo extends Donacion {

    @NotBlank(message = "La descripcion es obligatoria")
    @Column(nullable = false)
    private String descripcion;

    @NotNull(message = "El peso es obligatorio")
    @Column(nullable = false)
    private Double peso;

    @NotBlank(message = "Las dimensiones son obligatorias")
    @Column(nullable = false)
    private String dimensiones;
}
