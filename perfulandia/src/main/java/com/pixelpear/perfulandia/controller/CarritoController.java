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
public class CarritoController {


    public List<Producto> listaProductos = new ArrayList<>(List.of(
        new Producto(1L, "Rosa Mística", 4990L, 10),
        new Producto(2L, "Luz de Luna", 5990L, 15),
        new Producto(3L, "Esencia del Alba", 6990L, 20),
        new Producto(4L, "Brisa Marina", 7990L, 25),
        new Producto(5L, "Jardín Secreto", 8990L, 30),
        new Producto(6L, "Fuego Nocturno", 9990L, 12),
        new Producto(7L, "Cielo Estrellado", 10990L, 18),
        new Producto(8L, "Aurora Boreal", 11990L, 22),
        new Producto(9L, "Oasis Tropical", 12990L, 16),
        new Producto(10L, "Sueño Dorado", 13990L, 14)
    ));
    private String alias;
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


    @GetMapping("/alias")
    public ResponseEntity<String> aliasActual() {
        return ResponseEntity.ok("El alias del usuario actual es " + alias);
    }
    
    @PostMapping("/cambiarAlias")
    public ResponseEntity<String> cambiarAlias(@RequestParam String aliasNuevo) {
        this.alias = aliasNuevo;
        return ResponseEntity.ok("Alias cambiado a " + alias);
    }
    
    @PostMapping("/listaProductos/agregar")
    public ResponseEntity<String> agregarProductoLista(@RequestBody Producto producto) {
        listaProductos.add(producto);
        return ResponseEntity.ok("El producto ha sido agregado " + producto);
    }
    @GetMapping("/listaProductos")
    public ResponseEntity<List<Producto>> obtenerListaProductos() {
        return ResponseEntity.ok(listaProductos);
    }
    @DeleteMapping("/listaProductos/eliminar")
    public ResponseEntity<String> eliminarProductoLista(@RequestParam Long id) {
        listaProductos.removeIf(producto -> producto.getIdProducto().equals(id));
        return ResponseEntity.ok("El producto ha sido eliminado");
    }
}
