package edu.udelar.ayudemos.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "alimentos")
@PrimaryKeyJoinColumn(name = "donacion_id")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Alimento extends Donacion {
    
    @NotBlank(message = "La descripción es obligatoria")
    @Column(nullable = false)
    private String descripcion;
    
    @NotNull(message = "La cantidad es obligatoria")
    @Column(nullable = false)
    private Integer cantidad;
}