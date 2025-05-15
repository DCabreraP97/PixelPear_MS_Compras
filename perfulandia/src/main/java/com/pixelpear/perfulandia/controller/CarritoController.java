package com.pixelpear.perfulandia.controller;

import java.text.MessageFormat;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/carrito")
@RequiredArgsConstructor

// Falta hacer controller de pedidos,facturas,etc y implementar los cupones(clases con descuento) en pedidos
// Falta implementar los dtos 

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
          

    // Mejoar este endpoint para que devuelva un mensaje de error si el alias es null
    // y que devuelva un mensaje de error si no hay productos en el carrito
    @GetMapping("/mostrar")
    public ResponseEntity<List<ItemCarrito>> obtenerCarritoUsuario(@RequestParam String Alias) {
        if (Alias == null) {
            return ResponseEntity.badRequest().body(null);
        }
        else{
            List<ItemCarrito> listaRetorno = new ArrayList<>(itemCarritoService.obtenerItemsCarritoPorAlias(Alias));
            return ResponseEntity.ok(listaRetorno);
        }
    }

    @PostMapping("/agregar")
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
                Long precioTotal = producto.getPrecio().longValue() * cantidad;
                itemCarritoService.agregarItemCarrito(new ItemCarrito(idProducto, alias, producto.getNombre(), producto.getPrecio(), cantidad, precioTotal));
                return ResponseEntity.ok(MessageFormat.format("El producto %s ha sido agregado al carrito. El Precio total seria: $%d", producto.getNombre(), precioTotal));
            } else {
                return ResponseEntity.badRequest().body("No hay suficiente stock para el producto solicitado.");
            }
        }
    }
    //Faltan endpoints
    // para eliminar productos del carrito y para vaciar el carrito /carrito/remover/{productoId} Remover producto del carrito
    // /carrito/vaciar/usuarioId Vaciar carrito de un usuario

    @GetMapping("/alias")
    public ResponseEntity<String> aliasActual() {
        return ResponseEntity.ok("El alias del usuario actual es " + alias);
    }
    
    @PostMapping("/cambiarAlias")
    public ResponseEntity<String> cambiarAlias(@RequestBody String aliasNuevo) {
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
