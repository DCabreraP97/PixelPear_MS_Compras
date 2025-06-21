package com.pixelpear.perfulandia.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.pixelpear.perfulandia.dto.ItemCarritoDTO;
import com.pixelpear.perfulandia.model.Pedido;
import com.pixelpear.perfulandia.model.Perfume;
import com.pixelpear.perfulandia.service.FacturaService;
import com.pixelpear.perfulandia.service.PedidoService;
import com.pixelpear.perfulandia.service.PerfumeService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;

@WebMvcTest(CarritoController.class)
public class CarritoControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CarritoController carritoController;

    @MockitoBean
    private  PerfumeService perfumeService;
    @MockitoBean
    private  PedidoService pedidoService;
    @MockitoBean
    private  FacturaService facturaService; 


    @Test
    public void mostrarItemsCarrito_DeberiaRetornar200yLista() throws Exception {

        carritoController.carritoTemporal.add(new ItemCarritoDTO(1L, 3890.0, 2));
        carritoController.carritoTemporal.add(new ItemCarritoDTO(2L, 4570.0, 1));

        mockMvc.perform(get("/carrito/mostrar"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].idPerfume").value(1L))
            .andExpect(jsonPath("$[1].idPerfume").value(2L));
    }

    @Test
    public void agregarUnidades_DeberiaRetornar200yAgregarItem() throws Exception {
        
        Perfume perfume = new Perfume(1L, "Perfume Uno", 3890.0, 100);
        when(perfumeService.existePerfume(1L)).thenReturn(true);
        when(perfumeService.buscarPerfumePorId(1L)).thenReturn(perfume);

        mockMvc.perform(post("/carrito/agregarUnidades?idPerfume=1&cantidad=2"))
            .andExpect(status().isOk())
            .andExpect(content().string("Unidades agregadas al carrito"));
        
        assert(carritoController.carritoTemporal.size() == 1);
        assert(carritoController.carritoTemporal.get(0).getIdPerfume() == 1L);
        assert(carritoController.carritoTemporal.get(0).getCantidad() == 2);
    }

    @Test
    public void restarUnidadesCarrito_DeberiaReducirCantidadYRetornarStatus200() throws Exception {

        carritoController.carritoTemporal.add(new ItemCarritoDTO(1L, 3890.0, 5));

        when(perfumeService.existePerfume(1L)).thenReturn(true);
        
        mockMvc.perform(post("/carrito/restarUnidades")
            .param("idPerfume", String.valueOf(1L))
            .param("cantidadAReducir", String.valueOf(3)))
            .andExpect(status().isOk())
            .andExpect(content().string("La cantidad del perfume ha sido restada del carrito."));
    }

    @Test
    public void vaciarCarrito_DeberiaRetornar200YVaciarCarrito() throws Exception
    {
        carritoController.carritoTemporal.add(new ItemCarritoDTO(1L, 3890.0, 2));
        carritoController.carritoTemporal.add(new ItemCarritoDTO(2L, 4570.0, 1));

        mockMvc.perform(delete("/carrito/vaciar"))
            .andExpect(status().isOk())
            .andExpect(content().string("El carrito ha sido vaciado"));

        assert(carritoController.carritoTemporal.isEmpty());
    }

    @Test
    public void confirmarCompra_DescuentoValido_DeberiaRetornar200YConfirmarCompra() throws Exception {
        carritoController.carritoTemporal.add(new ItemCarritoDTO(1L, 5000.0, 2));

        Pedido pedido = new Pedido(1L, "DESC10", 10000.0, 9000.0, LocalDateTime.now());
        
        Perfume perfumeBD = new Perfume(1L, "Perfume Uno", 5000.0, 100);
        
        when(pedidoService.calcularTotalVenta(any())).thenReturn(10000.0);
        when(pedidoService.confirmarPedido(eq("DESC10"), eq(10000.0))).thenReturn(pedido);
        when(perfumeService.buscarPerfumePorId(1L)).thenReturn(perfumeBD);

        mockMvc.perform(post("/carrito/confirmar")
            .param("codigoDescuento", "DESC10"))
            .andExpect(status().isOk())
            .andExpect(content().string("La compra ha sido confirmada."));

        assert(carritoController.carritoTemporal.isEmpty());
    }
}

