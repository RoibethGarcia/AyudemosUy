package edu.udelar.ayudemos.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "beneficiarios")
@PrimaryKeyJoinColumn(name = "usuario_id")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Beneficiario extends Usuario {
    
    @NotBlank(message = "La dirección es obligatoria")
    @Column(nullable = false)
    private String direccion;
    
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Column(nullable = false)
    private LocalDate fechaNacimiento;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoBeneficiario estado;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Barrio barrio;
}