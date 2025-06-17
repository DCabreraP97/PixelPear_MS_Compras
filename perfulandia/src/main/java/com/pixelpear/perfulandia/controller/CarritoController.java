package com.pixelpear.perfulandia.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;

import com.pixelpear.perfulandia.model.ItemCarrito;
import com.pixelpear.perfulandia.model.Producto;
import com.pixelpear.perfulandia.service.ItemCarritoService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;



@RestController
@RequestMapping("/carrito")
@RequiredArgsConstructor
// localhost:8080/api/v1/carrito/mostrarItems GET
// localhost:8080/api/v1/carrito/agregarItem POST
// localhost:8080/api/v1/carrito/eliminar/{idProducto} DELETE
// localhost:8080/api/v1/carrito/alias GET
// localhost:8080/api/v1/carrito/cambiarAlias POST

// localhost:8080/api/v1/pedido/confirmar?codigoDescuento=SUPEROFERTA
// localhost:8080/api/v1/pedido/cambiarAlias POST
// localhost:8080/api/v1/pedido/mostrarPedidos GET

// Version A
//Eliminar uso de metodos https en carrito segun alias, el usuario sera uno y anonimo.
//Recortar pedido y factura. Dejar solo el carrito y la confirmacion de compra.
//Eliminar factura. Eliminar pedido/metodos o dejar solo generacion de pedidos.
//Eliminar descuento?

//1)Dejar carrito para agregar, restar stock de este carrito y eliminar perfumes del carrito. Luego poder confirmar el pedido  
//2)Dejar bodega para agregar/eliminar/actualizar/leer perfumes en stock 
//3)Dejar lectura de pedidos y facturas en BodegaController
public class CarritoController {


    public List<ItemCarrito> carritoTemporal = new ArrayList<>();
    private final ItemCarritoService itemCarritoService;
        
    @GetMapping("/mostrarItems")
    public ResponseEntity<List<ItemCarrito>> obtenerCarritoUsuario() {
        if (alias == null) {
            return ResponseEntity.badRequest().body(null);
        }
        else{
            List<ItemCarrito> listaRetorno = new ArrayList<>(itemCarritoService.obtenerItemsCarritoPorAlias(alias));
            return ResponseEntity.ok(listaRetorno);
        }
    }

    @PostMapping("/agregarItem")
    public ResponseEntity<String> agregarProductoCarrito(@RequestParam Long idProducto, @RequestParam Integer cantidad) {
        Producto producto = listaProductos.stream()
                .filter(p -> p.getIdProducto().equals(idProducto))
                .findFirst()
                .orElse(null);
        if(alias == null) {
            return ResponseEntity.badRequest().body("No se ha definido un alias, ingrese un alias antes de continuar");
        } else {
            if (producto != null && producto.getStock() >= cantidad) {
                producto.setStock(producto.getStock() - cantidad);
                double precioTotal = producto.getPrecio() * cantidad;
                itemCarritoService.agregarItemCarrito(new ItemCarrito(idProducto, alias, producto.getNombre(), producto.getPrecio(), cantidad, precioTotal));
                return ResponseEntity.ok("El producto " + producto.getNombre() + " ha sido agregado al carrito. El Precio total seria: $" + precioTotal);
            } else {
                return ResponseEntity.badRequest().body("No hay suficiente stock para el producto solicitado.");
            }
        }
    }

    @DeleteMapping("eliminar/carrito")
    public ResponseEntity<String> vaciarCarrito() {
        if(alias == null) {
            return ResponseEntity.badRequest().body("No se ha definido un alias, ingrese un alias antes de continuar");
        }else{
            itemCarritoService.vaciarCarrito(alias);
            return ResponseEntity.ok("El carrito ha sido vaciado");
        }
    }

    @DeleteMapping("/eliminar/{idProducto}")
    public ResponseEntity<String> eliminarProductoCarrito(@PathVariable Long idProducto) {
        if(alias == null) {
            return ResponseEntity.badRequest().body("No se ha definido un alias, ingrese un alias antes de continuar");
        }else{
            List<ItemCarrito> itemsCarrito = itemCarritoService.obtenerItemsCarritoPorAlias(alias);
            ItemCarrito itemCarrito = itemsCarrito.stream()
                .filter(p -> p.getIdProducto().equals(idProducto))
                .findFirst()
                .orElse(null);
            if(itemCarrito != null){
                itemCarritoService.eliminarItemCarrito(idProducto, alias);
                return ResponseEntity.ok("El producto ha sido eliminado del carrito");
            }
            else{
                return ResponseEntity.badRequest().body("No se ha encontrado el producto en el carrito");
            }
        }
    }
    @PostMapping("/actualizarStock/{idProducto}")
    public ResponseEntity<String> actualizarStock(@PathVariable Long idProducto, @RequestParam Integer cantidad,@RequestParam String operacion) {
        if(alias == null) {
                return ResponseEntity.badRequest().body("No se ha definido un alias, ingrese un alias antes de continuar");
            }
            else{
                boolean exito = false;
                exito = itemCarritoService.actualizarStock(alias,idProducto, cantidad, operacion);
                if(exito == true){
                    if(operacion.equals("sumar")) {
                        listaProductos.stream()
                            .filter(p -> p.getIdProducto().equals(idProducto))
                            .findFirst()
                            .ifPresent(p -> p.setStock(p.getStock() + cantidad));
                        return ResponseEntity.ok("Se han sumado " + cantidad + " unidades al producto con id " + idProducto);
                    } else if (operacion.equals("restar")) {
                        listaProductos.stream()
                            .filter(p -> p.getIdProducto().equals(idProducto))
                            .findFirst()
                            .ifPresent(p -> p.setStock(p.getStock() - cantidad));
                        return ResponseEntity.ok("Se han restado " + cantidad + " unidades al producto con id " + idProducto);
                    } else {
                        return ResponseEntity.badRequest().body("No se ha podido actualizar el stock");
                    }
                } else {
                    return ResponseEntity.badRequest().body("Hubo un error al actualizar el stock");
                }
            }
    }
}
