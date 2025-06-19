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

//CarritoController esta encargado de Añadir/restar unidades en carrito y confirmar la compra.
//Urls de ejemplo
//GET localhost:8080/api/v2/carrito/mostrar
//POST localhost:8080/api/v2/carrito/agregarUnidades?idPerfume=5&cantidad=2
//POST localhost:8080/api/v2/carrito/restarUnidades?idPerfume=5&cantidadAReducir=1
//POST localhost:8080/api/v2/carrito/confirmar?codigoDescuento=OFERTONJUNIO
//
// Falta implementar pruebas unitarias. Usar JUNIT y mockito.  POR HACER
// Falta usar swagger para documentar y hateoas para navegar por la API. POR HACER
// usar Spring Boot Test? POR HACER
//Hacer pruebas unitarias de los controller y service POR HACER

//Pregunta P: Cuantas pruebas unitarias se deben hacer y puedo hacer solo unas cuantas
// Debo usar JUNIT, mockito, swagger y hateoas o solo algunas?
// Puedo implementar hateoas en ciertas partes o completamente
//Swagger se implementa completamente?
//Que exactamente testear de los metodos? 200, resultados,etc

public class CarritoController {

    public List<ItemCarritoDTO> carritoTemporal = new ArrayList<>();

    private final PerfumeService perfumeService;
    private final PedidoService pedidoService;
    private final FacturaService facturaService;
        
    @GetMapping("/mostrar")
    public ResponseEntity<List<ItemCarritoDTO>> mostrarItemsCarrito() {
        return ResponseEntity.ok(carritoTemporal);
    }

    //Agrega unidades al carrito si hay stock suficiente, no actualiza la BD
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

    //El metodo solo resta unidades del carrito no de la BD
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

    //Se encarga de confirmar la compra de items del carrito, genera el pedido/factura, actualiza la BD y vacia el carro
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
