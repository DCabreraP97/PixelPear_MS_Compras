package com.pixelpear.perfulandia.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;

import com.pixelpear.perfulandia.dto.ItemCarritoDTO;
import com.pixelpear.perfulandia.dto.MensajeRespuesta;
import com.pixelpear.perfulandia.model.Pedido;
import com.pixelpear.perfulandia.model.Perfume;
import com.pixelpear.perfulandia.service.FacturaService;
import com.pixelpear.perfulandia.service.PedidoService;
import com.pixelpear.perfulandia.service.PerfumeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Carrito", description = "Controlador para simular el carrito de compras")
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
//http://localhost:8080/api/v2/swagger-ui Para ver la documentacion de swagger
//o http://localhost:8080/api/v2/swagger-ui/index.html


public class CarritoController {

    public List<ItemCarritoDTO> carritoTemporal = new ArrayList<>();

    private final PerfumeService perfumeService;
    private final PedidoService pedidoService;
    private final FacturaService facturaService;
    
    @Operation(summary = "Mostrar items del carrito", description = "Devuelve una lista de los perfumes en el carrito temporal")
    @GetMapping("/mostrar")
    public ResponseEntity<CollectionModel<ItemCarritoDTO>> mostrarItemsCarrito() {
        CollectionModel<ItemCarritoDTO> carritoConLinks = CollectionModel.of(carritoTemporal,
        linkTo(methodOn(CarritoController.class).confirmarCompra(null)).withRel("confirmar-compra"),
        linkTo(methodOn(CarritoController.class).agregarUnidadesCarrito(1L, 1)).withRel("agregar-unidades-carrito"),
        linkTo(methodOn(CarritoController.class).restarUnidadesCarrito(1L, 1)).withRel("restar-unidades-carrito"),
        linkTo(methodOn(CarritoController.class).vaciarCarrito()).withRel("vaciar-carrito"),
        linkTo(methodOn(CarritoController.class).mostrarItemsCarrito()).withSelfRel()
        );
        return ResponseEntity.ok(carritoConLinks);
    }

    //Agrega unidades al carrito si hay stock suficiente, no actualiza la BD
    @Operation(summary = "Agregar unidades al carrito", description = "Agrega unidades de un perfume al carrito temporal")
    @PostMapping("/agregarUnidades")
    public ResponseEntity<EntityModel<MensajeRespuesta>> agregarUnidadesCarrito(@RequestParam Long idPerfume, @RequestParam Integer cantidad) {
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

                MensajeRespuesta mensaje = new MensajeRespuesta("El perfume ha sido agregado al carrito.");
                EntityModel<MensajeRespuesta> respuesta = EntityModel.of(mensaje,
                linkTo(methodOn(CarritoController.class).mostrarItemsCarrito()).withRel("ver-carrito"),
                linkTo(methodOn(CarritoController.class).confirmarCompra(null)).withRel("confirmar-compra")
                );
                return ResponseEntity.ok(respuesta);
            }
            else
            {
                MensajeRespuesta mensaje = new MensajeRespuesta("No hay suficiente stock para el perfume solicitado.");
                EntityModel<MensajeRespuesta> respuesta = EntityModel.of(mensaje);
                return ResponseEntity.badRequest().body(respuesta);
            }
        }
        else
        {
            MensajeRespuesta mensaje = new MensajeRespuesta("El id del perfume no existe");
            EntityModel<MensajeRespuesta> respuesta = EntityModel.of(mensaje);
            return ResponseEntity.badRequest().body(respuesta);
        }
    }

    //El metodo solo resta unidades del carrito no de la BD
    @Operation(summary = "Restar unidades del carrito", description = "Resta unidades de un perfume en el carrito temporal")
    @PostMapping("/restarUnidades")
    public ResponseEntity<EntityModel<MensajeRespuesta>> restarUnidadesCarrito(@RequestParam Long idPerfume, @RequestParam Integer cantidadAReducir) {
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
                            MensajeRespuesta mensaje = new MensajeRespuesta("El perfume ha sido eliminado del carrito.");
                            EntityModel<MensajeRespuesta> respuesta = EntityModel.of(mensaje,
                            linkTo(methodOn(CarritoController.class).mostrarItemsCarrito()).withRel("ver-carrito"),
                            linkTo(methodOn(CarritoController.class).confirmarCompra(null)).withRel("confirmar-compra")
                            );            
                            return ResponseEntity.ok(respuesta);
                        } 
                        else 
                        {
                            perfumeCarrito.setCantidad(perfumeCarrito.getCantidad() - cantidadAReducir);
                            MensajeRespuesta mensaje = new MensajeRespuesta("La cantidad del perfume ha sido restada del carrito.");
                            EntityModel<MensajeRespuesta> respuesta = EntityModel.of(mensaje,
                            linkTo(methodOn(CarritoController.class).mostrarItemsCarrito()).withRel("ver-carrito"),
                            linkTo(methodOn(CarritoController.class).confirmarCompra(null)).withRel("confirmar-compra")
                            );
                            return ResponseEntity.ok(respuesta);
                        }
                    } 
                    else 
                    {
                        MensajeRespuesta mensaje = new MensajeRespuesta("La cantidad a restar es mayor que la cantidad en el carrito. Cantidad actual del carrito: " + perfumeCarrito.getCantidad() + " Cantidad a eliminar: " + cantidadAReducir);
                        EntityModel<MensajeRespuesta> respuesta = EntityModel.of(mensaje);
                        return ResponseEntity.badRequest().body(respuesta);
                    }
                }
            }
            MensajeRespuesta mensaje = new MensajeRespuesta("El perfume no se encuentra en el carrito.");
            EntityModel<MensajeRespuesta> respuesta = EntityModel.of(mensaje);
            return ResponseEntity.badRequest().body(respuesta);
        } 
        else 
        {
            MensajeRespuesta mensaje = new MensajeRespuesta("El id del perfume no existe");
            EntityModel<MensajeRespuesta> respuesta = EntityModel.of(mensaje);
            return ResponseEntity.badRequest().body(respuesta);
        }
    }

    //Se encarga de confirmar la compra de items del carrito, genera el pedido/factura, actualiza la BD y vacia el carro
    @Operation(summary = "Confirmar compra", description = "Confirma la compra de los perfumes en el carrito temporal. Genera un pedido y factura. Vacia el carrito.")
    @PostMapping("/confirmar")
    public ResponseEntity<EntityModel<MensajeRespuesta>> confirmarCompra(@RequestParam(required = false) String codigoDescuento) {
        if (carritoTemporal.isEmpty()) {
            MensajeRespuesta mensaje = new MensajeRespuesta("El carrito está vacío.");
            EntityModel<MensajeRespuesta> respuesta = EntityModel.of(mensaje,
            linkTo(methodOn(CarritoController.class).agregarUnidadesCarrito(1L, 1)).withRel("agregar-unidades-carrito")
            );
            return ResponseEntity.badRequest().body(respuesta);
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

            MensajeRespuesta mensaje = new MensajeRespuesta("La compra ha sido confirmada.");
            EntityModel<MensajeRespuesta> respuesta = EntityModel.of(mensaje,
            linkTo(methodOn(CarritoController.class).mostrarItemsCarrito()).withRel("ver-carrito"),
            linkTo(methodOn(CarritoController.class).agregarUnidadesCarrito(1L, 1)).withRel("agregar-unidades-carrito")
            );
            return ResponseEntity.ok(respuesta);
        }
    }

    @Operation(summary = "Vaciar carrito", description = "Vacia el carrito temporal de compras")
    @DeleteMapping("vaciar")
    public ResponseEntity<EntityModel<MensajeRespuesta>> vaciarCarrito() {
        carritoTemporal.clear();
        MensajeRespuesta mensaje = new MensajeRespuesta("El carrito ha sido vaciado.");
        EntityModel<MensajeRespuesta> respuesta = EntityModel.of(mensaje,
        linkTo(methodOn(CarritoController.class).mostrarItemsCarrito()).withRel("ver-carrito"),
        linkTo(methodOn(CarritoController.class).agregarUnidadesCarrito(1L, 1)).withRel("agregar-unidades-carrito")
        );
        return ResponseEntity.ok(respuesta);
    }

}
