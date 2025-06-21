package com.pixelpear.perfulandia.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pixelpear.perfulandia.model.Factura;
import com.pixelpear.perfulandia.model.Pedido;
import com.pixelpear.perfulandia.service.FacturaService;
import com.pixelpear.perfulandia.service.PedidoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@Tag(name = "Ventas", description = "Controlador para leer los pedidos y facturas")
@RestController
@RequestMapping("/ventas")
@RequiredArgsConstructor

//Controller simple de lectura de Pedidos/Facturas
public class VentasController {

    private final PedidoService pedidoService;
    private final FacturaService facturaService;
    
    @Operation(summary = "Mostrar todos los pedidos", description = "Devuelve una lista de todos los pedidos realizados")
    @GetMapping("/pedidos")
    public ResponseEntity<List<Pedido>> mostrarPedidos() {
        return ResponseEntity.ok(pedidoService.mostrarPedidos());
    }
    
    @Operation(summary = "Mostrar un pedido por ID", description = "Devuelve el pedido correspondiente al ID proporcionado")
    @GetMapping("/pedido/{id}")
    public ResponseEntity<Pedido> mostrarPedido(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.mostrarPedidoPorId(id));
    }

    @Operation(summary = "Mostrar todas las facturas", description = "Devuelve una lista de todas las facturas generadas")
    @GetMapping("/facturas")
    public ResponseEntity<List<Factura>> mostrarFacturas() {
        return ResponseEntity.ok(facturaService.mostrarFacturas());
    }

    @Operation(summary = "Mostrar una factura por ID", description = "Devuelve la factura correspondiente al ID proporcionado")
    @GetMapping("/factura/{id}")
    public ResponseEntity<Factura> mostrarFactura(@PathVariable Long id) {
        return ResponseEntity.ok(facturaService.mostrarFacturaPorId(id));
    }
    
}
