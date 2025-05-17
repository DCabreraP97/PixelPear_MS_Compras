package com.pixelpear.perfulandia.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pixelpear.perfulandia.model.ItemCarrito;
import com.pixelpear.perfulandia.model.ItemPedido;
import com.pixelpear.perfulandia.model.Pedido;
import com.pixelpear.perfulandia.service.ItemCarritoService;
import com.pixelpear.perfulandia.service.ItemPedidoService;
import com.pixelpear.perfulandia.service.PedidoService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.ArrayList;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
@RequestMapping("/pedido")
@RequiredArgsConstructor
public class PedidoController {
    private final PedidoService pedidoService;
    private final ItemCarritoService itemCarritoService;
    //private final DescuentoService descuentoService;
    private final ItemPedidoService itemPedidoService;
    private String alias;
    @PostMapping("/confirmar")
    public ResponseEntity<Pedido> confirmarPedido(@RequestParam (required = false) String codigoDescuento) {
        if(alias == null) {
            return ResponseEntity.badRequest().header("Error", "No se ha definido un alias, ingrese un alias antes de continuar").build();
        }else
        {
            double totalVenta = 0;
            totalVenta = itemCarritoService.obtenerItemsCarritoPorAlias(alias).stream().mapToDouble(i -> i.getPrecioTotal()).sum();
            Pedido pedidoCreado = pedidoService.confirmarPedido(alias, codigoDescuento,totalVenta);
            List<ItemCarrito> itemsCarrito = new ArrayList<>(itemCarritoService.obtenerItemsCarritoPorAlias(alias));
            List<ItemPedido> itemsPedidos = itemsCarrito.stream()
                                           .map(item -> ItemPedido.builder()
                                           .idProducto(item.getIdProducto())
                                           .nombreProducto(item.getNombreProducto())
                                           .cantidad(item.getCantidad())
                                           .precio(item.getPrecioUnitario())
                                           .alias(item.getAlias())
                                           .pedido(pedidoCreado)
                                           .build())
                                           .toList();
            itemPedidoService.guardarItemsPedido(itemsPedidos);
            itemCarritoService.vaciarCarrito(alias);
            return ResponseEntity.ok(pedidoCreado);
        }
    }
    @GetMapping("/mostrarPedidos")
    public ResponseEntity<List<Pedido>> mostrarPedidos() {
        
        List<Pedido> listaRetorno = new ArrayList<>(pedidoService.mostrarPedidos());
        return ResponseEntity.ok(listaRetorno);
        
    }
    @GetMapping("/mostrarPedidos/usuario")
    public ResponseEntity<List<Pedido>> mostrarPedidosUsuario() {
        if(alias == null) {
            return ResponseEntity.badRequest().body(null);
        }else{
            List<Pedido> listaRetorno = new ArrayList<>(pedidoService.mostrarPedidosPorAlias(alias));
            return ResponseEntity.ok(listaRetorno);
        }
    }

    @GetMapping("/mostrarPedidos/{idPedido}")
    public ResponseEntity<Pedido> mostrarPedidoPorId(@PathVariable Long idPedido) {
        Pedido pedido = pedidoService.mostrarPedidoPorId(idPedido);
        if (pedido != null) {
            return ResponseEntity.ok(pedido);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/alias")
    public ResponseEntity<String> aliasActual() {
        return ResponseEntity.ok("El alias del usuario actual es " + alias);
    }
    
    @PostMapping("/cambiarAlias")
    public ResponseEntity<String> cambiarAlias(@RequestParam String aliasNuevo) {
        this.alias = aliasNuevo;
        return ResponseEntity.ok("Alias cambiado a " + alias);
    }
    
}
