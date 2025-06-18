package com.pixelpear.perfulandia.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;

import com.pixelpear.perfulandia.dto.ItemCarritoDTO;
import com.pixelpear.perfulandia.model.Pedido;
import com.pixelpear.perfulandia.model.Perfume;
import com.pixelpear.perfulandia.service.FacturaService;
import com.pixelpear.perfulandia.service.PedidoService;
import com.pixelpear.perfulandia.service.PerfumeService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;



@RestController
@RequestMapping("/carrito")
@RequiredArgsConstructor

// Version A
//Eliminar uso de metodos https en carrito segun alias, el usuario sera uno y anonimo.
//Recortar pedido y factura. Dejar solo el carrito y la confirmacion de compra.
//Eliminar factura. Eliminar pedido/metodos o dejar solo generacion de pedidos.
//Eliminar descuento?

//1)Dejar carrito para agregar, restar stock de este carrito y eliminar perfumes del carrito. Luego poder confirmar el pedido  
//2)Dejar bodega para agregar/eliminar/actualizar/leer perfumes en stock 
//3)Dejar lectura de pedidos y facturas en BodegaController
//Falta confirmar feedback de aplicar descuento. 
//Recortar deciamales a la hora de guardar precios y fecha -> hora.
// Falta hacer bodegaControlador CRUD + mostrar pedido/factura.

public class CarritoController {


    public List<ItemCarritoDTO> carritoTemporal = new ArrayList<>();
    private final PerfumeService perfumeService;
    private final PedidoService pedidoService;
    private final FacturaService facturaService;
        
    @GetMapping("/mostrar")
    public ResponseEntity<List<ItemCarritoDTO>> mostrarItemsCarrito() {
        return ResponseEntity.ok(carritoTemporal);
    }

    @PostMapping("/agregarUnidades")
    public ResponseEntity<String> agregarUnidadesCarrito(@RequestParam Long idPerfume, @RequestParam Integer cantidad) {
        if(perfumeService.existePerfume(idPerfume)) 
        {
            Perfume perfumeAComprar = perfumeService.buscarPerfumePorId(idPerfume);

            if(cantidad <= perfumeAComprar.getStock()) 
            {
                ItemCarritoDTO itemCarrito = new ItemCarritoDTO();
                itemCarrito.setIdPerfume(perfumeAComprar.getIdPerfume());
                itemCarrito.setPrecio(perfumeAComprar.getPrecio());
                itemCarrito.setCantidad(cantidad);
                carritoTemporal.add(itemCarrito);
                return ResponseEntity.ok("El perfume ha sido agregado al carrito.");
            }
            else
            {
                return ResponseEntity.badRequest().body("No hay suficiente stock para el perfume solicitado.");
            }
        }
        else
        {
            return ResponseEntity.badRequest().body("El id del perfume no existe");
        }
    }

    @PostMapping("/restarUnidades")
    public ResponseEntity<String> restarUnidadesCarrito(@RequestParam Long idPerfume, @RequestParam Integer cantidadAReducir) {
        if(perfumeService.existePerfume(idPerfume)) 
        {
            for (ItemCarritoDTO perfumeCarrito : carritoTemporal) {
                if(perfumeCarrito.getIdPerfume().equals(idPerfume)) 
                {
                    if(cantidadAReducir <= perfumeCarrito.getCantidad()) 
                    {
                        if (cantidadAReducir == perfumeCarrito.getCantidad()) 
                        {
                            carritoTemporal.remove(perfumeCarrito);
                            return ResponseEntity.ok("El perfume ha sido eliminado del carrito.");
                        } 
                        else 
                        {
                            perfumeCarrito.setCantidad(perfumeCarrito.getCantidad() - cantidadAReducir);
                            return ResponseEntity.ok("La cantidad del perfume ha sido restada del carrito.");
                        }
                    } 
                    else 
                    {
                        return ResponseEntity.badRequest().body("La cantidad a restar es mayor que la cantidad en el carrito. Cantidad actual del carrito: " + perfumeCarrito.getCantidad() + " Cantidad a eliminar: " + cantidadAReducir);
                    }
                }
            }
            return ResponseEntity.badRequest().body("El perfume no se encuentra en el carrito.");
        } 
        else 
        {
            return ResponseEntity.badRequest().body("El id del perfume no existe");
        }
    }

    @PostMapping("/confirmar")
    public ResponseEntity<String> confirmarCompra(@RequestParam(required = false) String codigoDescuento) {
        if (carritoTemporal.isEmpty()) {
            return ResponseEntity.badRequest().body("El carrito está vacío.");
        }
        else
        {
            //Se calcula el total de la venta sin descuento
            double totalVenta = pedidoService.calcularTotalVenta(carritoTemporal);

            //Se crea el pedido y la factura
            Pedido pedido = pedidoService.confirmarPedido(codigoDescuento, totalVenta);
            facturaService.generarFactura(pedido);

            //Se actualiza el stock en la base de datos y luego se vacia el carrito
            for (ItemCarritoDTO perfumeCarrito : carritoTemporal) {
                Perfume perfumeBD = perfumeService.buscarPerfumePorId(perfumeCarrito.getIdPerfume());
                perfumeBD.setStock(perfumeBD.getStock() - perfumeCarrito.getCantidad());
                perfumeService.guardarPerfume(perfumeBD);
            }
            carritoTemporal.clear();
            return ResponseEntity.ok("La compra ha sido confirmada.");
        }
    }

    @DeleteMapping("vaciar")
    public ResponseEntity<String> vaciarCarrito() {
        carritoTemporal.clear();
        return ResponseEntity.ok("El carrito ha sido vaciado");
    }

}
