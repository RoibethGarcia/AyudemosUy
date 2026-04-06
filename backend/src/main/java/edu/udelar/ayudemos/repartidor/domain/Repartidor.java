package edu.udelar.ayudemos.repartidor.domain;

import edu.udelar.ayudemos.usuario.domain.Usuario;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "repartidores")
@PrimaryKeyJoinColumn(name = "usuario_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Repartidor extends Usuario {

    @NotBlank(message = "El numero de licencia es obligatorio")
    @Column(nullable = false, unique = true)
    private String numeroLicencia;
}
