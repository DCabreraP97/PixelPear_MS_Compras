package com.pixelpear.perfulandia.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.pixelpear.perfulandia.model.Descuento;
import com.pixelpear.perfulandia.model.ItemPedido;
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

    public Pedido confirmarPedido(String alias, String codigo_descuento, double totalVenta) {
        LocalDateTime fechaPedido = LocalDateTime.now();
        Descuento descuento = descuentoService.buscarDescuentoPorCodigo(codigo_descuento);

        Pedido pedido = new Pedido();
        pedido.setAlias(alias);
        pedido.setPrecioSinDescuento(totalVenta);
        pedido.setFecha(fechaPedido);

        if (descuento != null) {
            if(descuento.getCodigoDescuento().equalsIgnoreCase(codigo_descuento) &&
               !LocalDate.now().isBefore(descuento.getFechaInicio()) &&
               !LocalDate.now().isAfter(descuento.getFechaFin()))
            {
                double porcentaje = descuento.getPorcentajeDescuento();
                double totalConDescuento = totalVenta * (1 - (porcentaje / 100.0));
                pedido.setPrecioFinal(totalConDescuento);
                pedido.setCodigoDescuento(descuento.getCodigoDescuento());
            }
            else {
                pedido.setPrecioFinal(totalVenta);
                pedido.setCodigoDescuento("NO APLICA");
            }
        } else {
            pedido.setPrecioFinal(totalVenta);
            pedido.setCodigoDescuento("NO APLICA");
    }

    pedidoRepository.save(pedido);
    return pedido;
}

    public List<Pedido> mostrarPedidosPorAlias(String alias) {
        List<Pedido> listaPedidos = pedidoRepository.findByAlias(alias);
        return listaPedidos;
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
