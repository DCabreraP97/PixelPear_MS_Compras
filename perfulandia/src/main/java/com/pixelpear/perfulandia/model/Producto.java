package com.pixelpear.perfulandia.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Producto {
    private Long idProducto;
    private String nombre;
    private double precio;
    private Integer stock;
}
