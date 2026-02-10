package edu.udelar.ayudemos.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "articulos")
@PrimaryKeyJoinColumn(name = "donacion_id")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Articulo extends Donacion {
    
    @NotBlank(message = "La descripción es obligatoria")
    @Column(nullable = false)
    private String descripcion;
    
    @NotNull(message = "El peso es obligatorio")
    @Column(nullable = false)
    private Double peso; // en kg
    
    @NotBlank(message = "Las dimensiones son obligatorias")
    @Column(nullable = false)
    private String dimensiones; // ej: "30x40x50 cm"
}