package com.pixelpear.perfulandia.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pixelpear.perfulandia.model.Factura;
import com.pixelpear.perfulandia.model.Pedido;
import com.pixelpear.perfulandia.repository.FacturaRepository;

@ExtendWith(MockitoExtension.class)
public class FacturaServiceTest {
    @Mock
    private FacturaRepository facturaRepository;

    @InjectMocks
    private FacturaService facturaService;

    @Test
    void generarFactura_DeberiaGenerarFacturaCorrectamente() {
        Pedido pedidoCreado = new Pedido(1L, "OFERTONJUNIO", 20000.0, 18200.0, LocalDateTime.now());

        facturaService.generarFactura(pedidoCreado);
        
        verify(facturaRepository, times(1)).save(any(Factura.class));

    }

    @Test
    void mostrarFacturas_DeberiaRetornarListaFacturas() {
        List<Factura> facturas = new ArrayList<>();
        when(facturaRepository.findAll()).thenReturn(facturas);

        List<Factura> resultado = facturaService.mostrarFacturas();

        assertEquals(facturas, resultado);
    }

    @Test
    void mostrarFacturaPorId_DeberiaRetornarFactura() {
        Long idBuscado = 1L;
        Factura factura = new Factura(1L, LocalDateTime.now().toLocalDate(), 18200.0);
        when(facturaRepository.findById(idBuscado)).thenReturn(Optional.of(factura));

        Factura resultado = facturaService.mostrarFacturaPorId(idBuscado);

        assertEquals(factura, resultado);
    }
}
