package com.pixelpear.perfulandia.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pixelpear.perfulandia.model.Factura;
import com.pixelpear.perfulandia.model.Pedido;
import com.pixelpear.perfulandia.service.FacturaService;
import com.pixelpear.perfulandia.service.PedidoService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@RequestMapping("/ventas")
@RequiredArgsConstructor

//Controller simple de lectura de Pedidos/Facturas
public class VentasController {

    private final PedidoService pedidoService;
    private final FacturaService facturaService;
    
    @GetMapping("/pedidos")
    public ResponseEntity<List<Pedido>> mostrarPedidos() {
        return ResponseEntity.ok(pedidoService.mostrarPedidos());
    }
    
    @GetMapping("/pedido/{id}")
    public ResponseEntity<Pedido> mostrarPedido(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.mostrarPedidoPorId(id));
    }

    @GetMapping("/facturas")
    public ResponseEntity<List<Factura>> mostrarFacturas() {
        return ResponseEntity.ok(facturaService.mostrarFacturas());
    }
    
    @GetMapping("/factura/{id}")
    public ResponseEntity<Factura> mostrarFactura(@PathVariable Long id) {
        return ResponseEntity.ok(facturaService.mostrarFacturaPorId(id));
    }
    
}
