package com.pixelpear.perfulandia.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;

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
public class CarritoController {


    public List<Perfume> carritoTemporal = new ArrayList<>();
    private final PerfumeService perfumeService;
    private final PedidoService pedidoService;
    private final FacturaService facturaService;
        
    @GetMapping("/mostrar")
    public ResponseEntity<List<Perfume>> mostrarItemsCarrito() {
        return ResponseEntity.ok(carritoTemporal);
    }

    @PostMapping("/agregarUnidades")
    public ResponseEntity<String> agregarUnidadesCarrito(@RequestParam Long idPerfume, @RequestParam Integer cantidad) {
        if(perfumeService.existePerfume(idPerfume)) 
        {
            Perfume perfumeAComprar = perfumeService.buscarPerfumePorId(idPerfume);

            if(cantidad <= perfumeAComprar.getStock()) 
            {
                carritoTemporal.add(perfumeAComprar);
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
    public ResponseEntity<String> restarUnidadesCarrito(@RequestParam Long idPerfume, @RequestParam Integer cantidad) {
        if(perfumeService.existePerfume(idPerfume)) 
        {
            for (Perfume perfumeCarrito : carritoTemporal) {
                if(perfumeCarrito.getIdPerfume().equals(idPerfume)) 
                {
                    if(cantidad <= perfumeCarrito.getStock()) 
                    {
                        if (cantidad == perfumeCarrito.getStock()) 
                        {
                            carritoTemporal.remove(perfumeCarrito);
                            return ResponseEntity.ok("El perfume ha sido eliminado del carrito.");
                        } 
                        else 
                        {
                            perfumeCarrito.setStock(perfumeCarrito.getStock() - cantidad);
                            return ResponseEntity.ok("La cantidad del perfume ha sido restada del carrito.");
                        }
                    } 
                    else 
                    {
                        return ResponseEntity.badRequest().body("La cantidad a restar es mayor que la cantidad en el carrito. Cantidad actual del carrito: " + perfumeCarrito.getStock() + " Cantidad a eliminar: " + cantidad);
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
            for (Perfume perfumeCarrito : carritoTemporal) {
                Perfume perfumeBD = perfumeService.buscarPerfumePorId(perfumeCarrito.getIdPerfume());
                perfumeBD.setStock(perfumeBD.getStock() - perfumeCarrito.getStock());
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
