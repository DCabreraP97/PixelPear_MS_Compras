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

    public ResponseEntity<String> confirmarPedido(String alias, String codigoDescuento,Long totalVenta) {
        boolean descuentoValido = false;
        LocalDate hoy = LocalDate.now();
        LocalDateTime fechaPedido = LocalDateTime.now();

        if (codigoDescuento != null) {
            Descuento descuento = descuentoService.buscarDescuentoPorCodigo(codigoDescuento);
            if (descuento != null) {
                descuentoValido = !hoy.isBefore(descuento.getFechaInicio()) && !hoy.isAfter(descuento.getFechaFin());
                if (descuentoValido) {
                    //Aplica el descuento
                    double porcentaje = descuento.getPorcentajeDescuento(); 
                    double totalConDescuentoDouble = totalVenta * (1 - (porcentaje / 100.0));
                    Long totalConDescuento = (long) totalConDescuentoDouble;
                    Pedido pedido = new Pedido();
                    pedido.setAlias(alias);
                    pedido.setCodigoDescuento(codigoDescuento);
                    pedido.setPrecioSinDescuento(totalVenta);
                    pedido.setPrecioFinal(totalConDescuento);
                    pedido.setFecha(fechaPedido);
                    pedidoRepository.save(pedido);
                    return ResponseEntity.ok("El pedido ha sido confirmado. Descuento aplicado");
                } 
                else 
                {
                    // El descuento no es válido, está fuera de la fecha
                    Pedido pedido = new Pedido();
                    pedido.setAlias(alias);
                    pedido.setCodigoDescuento(codigoDescuento);
                    pedido.setPrecioSinDescuento(totalVenta);
                    pedido.setPrecioFinal(totalVenta);
                    pedido.setFecha(fechaPedido);
                    pedidoRepository.save(pedido);
                    return ResponseEntity.ok("El pedido ha sido confirmado. Descuento no aplicado: fuera de la fecha de validez");
                }
            }
            else
            {
                //El descuento no es valido, no existe
                Pedido pedido = new Pedido();
                pedido.setAlias(alias);
                pedido.setCodigoDescuento(codigoDescuento);
                pedido.setPrecioSinDescuento(totalVenta);
                pedido.setPrecioFinal(totalVenta);
                pedido.setFecha(fechaPedido);
                pedidoRepository.save(pedido);
                return ResponseEntity.ok("El pedido ha sido confirmado. Descuento no aplicado: no se encontro uno con ese codigo");
            }
        }
        else
        {
            //Descuento no aplica, no hay uno ingresado
            Pedido pedido = new Pedido();
            pedido.setAlias(alias);
            pedido.setCodigoDescuento(codigoDescuento);
            pedido.setPrecioSinDescuento(totalVenta);
            pedido.setPrecioFinal(totalVenta);
            pedido.setFecha(fechaPedido);
            pedidoRepository.save(pedido);
            return ResponseEntity.ok("El pedido ha sido confirmado. Descuento no aplicado: no se ingreso uno");
        }
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
