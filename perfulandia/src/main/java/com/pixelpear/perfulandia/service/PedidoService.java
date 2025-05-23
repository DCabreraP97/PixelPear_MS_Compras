package com.pixelpear.perfulandia.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(PedidoService.class);

    public Pedido confirmarPedido(String alias, String codigoDescuento, double totalVenta) {
        LocalDateTime fechaPedido = LocalDateTime.now();
        logger.info("CODIGO DEL DESCUENTO : {}", codigoDescuento);
        Descuento descuento = descuentoService.buscarDescuentoPorCodigo(codigoDescuento.trim());
        logger.info("Descuento encontrado: {}", descuento);

        Pedido pedido = new Pedido();
        pedido.setAlias(alias);
        pedido.setPrecioSinDescuento(totalVenta);
        pedido.setFecha(fechaPedido);

        if (descuento != null) {
            if(descuento.getCodigoDescuento().equalsIgnoreCase(codigoDescuento) &&
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
