package com.pixelpear.perfulandia.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

        LocalDateTime fechaPedido = LocalDateTime.now().withNano(0);

        Descuento descuento = descuentoService.buscarDescuentoPorCodigo(codigoDescuento); //Busca el codigo, retorna null si no lo encuentra

        Pedido pedido = new Pedido(); //Se crea el pedido y se setean la fecha y precio sin el descuento

        // Se redondea el totalVenta a 2 decimales
        BigDecimal bd = new BigDecimal(totalVenta).setScale(2, RoundingMode.HALF_UP);
        double totalVentaRedondeado = bd.doubleValue();
        pedido.setPrecioSinDescuento(totalVentaRedondeado);
        pedido.setFecha(fechaPedido);

        if (descuento != null)
        {
            if(descuento.getCodigoDescuento().equalsIgnoreCase(codigoDescuento) &&
               !LocalDate.now().isBefore(descuento.getFechaInicio()) &&
               !LocalDate.now().isAfter(descuento.getFechaFin())) // Estas condiciones se encargan de que cumpla con requisitos de fechas
            {
                //Operaciones de descuento de porcentaje. Se guarda el nuevo precio y el codigo valido. Se redondea a 2 decimales
                double porcentaje = descuento.getPorcentajeDescuento();
                double totalConDescuento = totalVentaRedondeado * (1 - (porcentaje / 100.0));
                BigDecimal bdDescuento = new BigDecimal(totalConDescuento).setScale(2, RoundingMode.HALF_UP);
                double totalConDescuentoRedondeado = bdDescuento.doubleValue();
                pedido.setPrecioFinal(totalConDescuentoRedondeado);
                pedido.setCodigoDescuento(descuento.getCodigoDescuento());
                pedidoRepository.save(pedido);
                return pedido;
            }
            else {
                //CÓDIGO DE DESCUENTO NO VÁLIDO O FUERA DE FECHA.
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
