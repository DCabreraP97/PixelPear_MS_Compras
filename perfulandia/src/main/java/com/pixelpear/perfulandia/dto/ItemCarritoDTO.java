package com.pixelpear.perfulandia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCarritoDTO {
    private Long idPerfume;
    private double precio;
    private Integer cantidad;
}
