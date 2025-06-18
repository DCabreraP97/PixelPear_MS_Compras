package com.pixelpear.perfulandia.service;

import java.time.LocalDate;
import java.util.List;

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
        factura.setFecha(fechaFactura);
        factura.setPrecio(pedidoCreado.getPrecioFinal());
        facturaRepository.save(factura);

    }
    public List<Factura> mostrarFacturas() {
        return facturaRepository.findAll();
    }

    public Factura mostrarFacturaPorId(Long id) {
        return facturaRepository.findById(id).orElse(null);
    }

}
