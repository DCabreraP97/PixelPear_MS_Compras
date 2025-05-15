package com.pixelpear.perfulandia.model;

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
public class ItemCarrito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idItemCarrito;
    private Long idProducto;
    private String alias;
    private String nombreProducto;
    private Long precioUnitario;
    private Integer cantidad;
    private Long precioTotal;

    public ItemCarrito(Long idProducto, String alias, String nombreProducto, Long precioUnitario, Integer cantidad, Long precioTotal) {
        this.idProducto = idProducto;
        this.alias = alias;
        this.nombreProducto = nombreProducto;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
        this.precioTotal = precioUnitario * cantidad;
    }
}
