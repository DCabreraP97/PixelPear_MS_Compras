package com.pixelpear.perfulandia.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.pixelpear.perfulandia.model.Factura;
import com.pixelpear.perfulandia.model.Pedido;
import com.pixelpear.perfulandia.repository.FacturaRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class FacturaService {
    private final FacturaRepository facturaRepository;

    public void generarFactura(Pedido pedidoCreado) {
        LocalDate fechaFactura = pedidoCreado.getFecha().toLocalDate();
        Factura factura = new Factura();
        factura.setAlias(pedidoCreado.getAlias());
        factura.setFecha(fechaFactura);
        factura.setPrecio(pedidoCreado.getPrecioFinal());
        facturaRepository.save(factura);

    }

}
