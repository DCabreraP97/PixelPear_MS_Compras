package com.pixelpear.perfulandia.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder

public class Descuento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDescuento;
    private String codigoDescuento;
    private String descripcion;
    private double porcentajeDescuento;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

}
