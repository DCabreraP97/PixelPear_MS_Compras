package com.pixelpear.perfulandia.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pixelpear.perfulandia.service.ItemCarritoService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/pedido")
@RequiredArgsConstructor
public class PedidoController {
    private final PedidoService pedidoService;
    //private final ItemCarritoService itemCarritoService;
    //private final DescuentoService descuentoService;
    //private final ItemPedidoService itemPedidoService;
    @PostMapping("/confirmar")
    public ResponseEntity<String> confirmarPedido(@RequestBody String entity) {
        
    }
    
    
}
