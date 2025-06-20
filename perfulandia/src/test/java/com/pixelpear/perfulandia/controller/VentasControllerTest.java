package com.pixelpear.perfulandia.controller;

import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.pixelpear.perfulandia.model.Factura;
import com.pixelpear.perfulandia.model.Pedido;
import com.pixelpear.perfulandia.service.FacturaService;
import com.pixelpear.perfulandia.service.PedidoService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(VentasController.class)
public class VentasControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PedidoService pedidoService;
    @MockitoBean
    private FacturaService facturaService;

    @Test
    public void mostrarPedidos_DeberiaRetornar200yLista() throws Exception {
        List<Pedido> pedidos = List.of(
            new Pedido(1L, "NO APLICA", 12000.0, 12000.0, LocalDateTime.now()),
            new Pedido(2L, "OFERTONJUNIO", 20000.0, 18200.0, LocalDateTime.now())
        );

        when(pedidoService.mostrarPedidos()).thenReturn(pedidos);

        mockMvc.perform(get("/ventas/pedidos"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"));
    }

    @Test
    public void mostrarPedidoPorId_DeberiaRetornar200yPedido() throws Exception {
        Pedido pedido = new Pedido(1L, "NO APLICA", 12000.0, 12000.0, LocalDateTime.now());

        when(pedidoService.mostrarPedidoPorId(1L)).thenReturn(pedido);

        mockMvc.perform(get("/ventas/pedido/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"));
    }

    @Test
    public void mostrarFacturas_DeberiaRetornar200yLista() throws Exception {
        List<Factura> facturas = List.of(
            new Factura(1L, LocalDate.now(), 12000.0),
            new Factura(2L, LocalDate.now(), 20000.0)
        );

        when(facturaService.mostrarFacturas()).thenReturn(facturas);

        mockMvc.perform(get("/ventas/facturas"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"));
    }

    @Test
    public void mostrarFacturaPorId_DeberiaRetornar200yFactura() throws Exception {
        Factura factura = new Factura(1L, LocalDate.now(), 12000.0);

        when(facturaService.mostrarFacturaPorId(1L)).thenReturn(factura);

        mockMvc.perform(get("/ventas/factura/1"))
            .andExpect(status().isOk())
            .andExpect(content().contentType("application/json"));
    }

}