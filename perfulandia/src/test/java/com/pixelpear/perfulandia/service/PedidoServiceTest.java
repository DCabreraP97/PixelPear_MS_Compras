package com.pixelpear.perfulandia.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.pixelpear.perfulandia.dto.ItemCarritoDTO;
import com.pixelpear.perfulandia.model.Descuento;
import com.pixelpear.perfulandia.model.Pedido;
import com.pixelpear.perfulandia.repository.PedidoRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PedidoServiceTest {
    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private DescuentoService descuentoService;

    @InjectMocks
    private PedidoService pedidoService;

    @Test
    void calcularTotalVenta_DeberiaCalcularCorrectamente(){
        List<ItemCarritoDTO> items = Arrays.asList(
            new ItemCarritoDTO(1L, 100.0, 2),
            new ItemCarritoDTO(2L, 200.0, 1)
        );

        double total = pedidoService.calcularTotalVenta(items);

        assertEquals(400.0, total);
    } 

    @Test
    void mostrarPedidos_DeberiaRetornarLista(){
        List<Pedido> pedidos = Arrays.asList(
            new Pedido(1L, "NO APLICA", 12000.0, 12000.0, null),
            new Pedido(2L, "OFERTONJUNIO", 20000.0, 18200.0, null)
        );

        when(pedidoRepository.findAll()).thenReturn(pedidos);

        List<Pedido> resultado = pedidoService.mostrarPedidos();

        assertEquals(pedidos, resultado);
    }

    @Test
    void mostrarPedidoPorId_DeberiaRetornarPedido() {
        Long idBuscado = 1L;
        Pedido pedido = new Pedido(1L, "NO APLICA", 12000.0, 12000.0, null);

        when(pedidoRepository.findById(idBuscado)).thenReturn(Optional.of(pedido));

        Pedido resultado = pedidoService.mostrarPedidoPorId(idBuscado);

        assertEquals(pedido, resultado);
    }
    @Test
    void confirmarPedido_DescuentoValido_DeberiaRetornarPedido() {
        String codigoDescuento = "JUNIO15";
        double totalVenta = 10000.0;

        Descuento descuento = new Descuento();
        descuento.setCodigoDescuento("JUNIO15");
        descuento.setFechaInicio(LocalDate.now().minusDays(1));
        descuento.setFechaFin(LocalDate.now().plusDays(5));
        descuento.setPorcentajeDescuento(15.0); 

        when(descuentoService.buscarDescuentoPorCodigo(codigoDescuento)).thenReturn(descuento);

        Pedido pedido = pedidoService.confirmarPedido(codigoDescuento, totalVenta);


        assertNotNull(pedido);
        assertEquals("JUNIO15", pedido.getCodigoDescuento());
        assertEquals(8500.00, pedido.getPrecioFinal());
        assertEquals(10000.00, pedido.getPrecioSinDescuento());
    }
}
