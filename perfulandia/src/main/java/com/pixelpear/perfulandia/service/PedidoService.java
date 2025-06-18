package com.pixelpear.perfulandia.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


import org.springframework.stereotype.Service;

import com.pixelpear.perfulandia.dto.ItemCarritoDTO;
import com.pixelpear.perfulandia.model.Descuento;

import com.pixelpear.perfulandia.model.Pedido;
import com.pixelpear.perfulandia.repository.PedidoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PedidoService {
    private final PedidoRepository pedidoRepository;
    private final DescuentoService descuentoService;

    public double calcularTotalVenta(List<ItemCarritoDTO> carritoTemporal) {
        double total = 0.0;
        for (ItemCarritoDTO perfume : carritoTemporal) {
            total += perfume.getPrecio() * perfume.getCantidad();
        }
        return total;
    }

    public Pedido confirmarPedido(String codigoDescuento, double totalVenta) {

        LocalDateTime fechaPedido = LocalDateTime.now();

        Descuento descuento = descuentoService.buscarDescuentoPorCodigo(codigoDescuento);

        Pedido pedido = new Pedido();
        pedido.setPrecioSinDescuento(totalVenta);
        pedido.setFecha(fechaPedido);

        if (descuento != null)
        {
            if(descuento.getCodigoDescuento().equalsIgnoreCase(codigoDescuento) &&
               !LocalDate.now().isBefore(descuento.getFechaInicio()) &&
               !LocalDate.now().isAfter(descuento.getFechaFin()))
            {
                double porcentaje = descuento.getPorcentajeDescuento();
                double totalConDescuento = totalVenta * (1 - (porcentaje / 100.0));
                pedido.setPrecioFinal(totalConDescuento);
                pedido.setCodigoDescuento(descuento.getCodigoDescuento());
                pedidoRepository.save(pedido);
                return pedido;
            }
            else {
                //CÓDIGO DE DESCUENTO NO VÁLIDO O FUERA DE FECHA
                pedido.setPrecioFinal(totalVenta);
                pedido.setCodigoDescuento("NO APLICA");
                pedidoRepository.save(pedido);
                return pedido;
            }
        } 
        else 
        {
            //NO EXISTE EL CÓDIGO DE DESCUENTO
            pedido.setPrecioFinal(totalVenta);
            pedido.setCodigoDescuento("NO APLICA");
            pedidoRepository.save(pedido);
            return pedido;
        }
        
    }

    public List<Pedido> mostrarPedidos() {
        List<Pedido> listaPedidos = pedidoRepository.findAll();
        return listaPedidos;
    }

    public Pedido mostrarPedidoPorId(Long idPedido) {
        Pedido pedido = pedidoRepository.findById(idPedido).orElse(null);
        return pedido;
    }

}
