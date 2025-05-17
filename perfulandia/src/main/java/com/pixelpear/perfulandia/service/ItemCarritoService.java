package com.pixelpear.perfulandia.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pixelpear.perfulandia.model.ItemCarrito;
import com.pixelpear.perfulandia.repository.ItemCarritoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemCarritoService {
    private final ItemCarritoRepository itemCarritoRepository;

    public void agregarItemCarrito(ItemCarrito itemCarrito) {
        itemCarritoRepository.save(itemCarrito);
    }

    public List<ItemCarrito> obtenerItemsCarritoPorAlias(String alias) {
        return itemCarritoRepository.findByAlias(alias);
    }

    public void eliminarItemCarrito(Long idProducto, String alias) {
        itemCarritoRepository.deleteByIdProductoAndAlias(idProducto, alias);
    }

    public void vaciarCarrito(String alias) {
        itemCarritoRepository.deleteByAlias(alias);
    }

    public boolean actualizarStock(String alias, Long idProducto, Integer cantidad, String operacion) {
        if(operacion.equals("sumar")) {
            ItemCarrito itemCarrito = itemCarritoRepository.findByIdProductoAndAlias(idProducto, alias).orElse(null);
            if (itemCarrito != null) {
                itemCarrito.setCantidad(itemCarrito.getCantidad() + cantidad);
                itemCarritoRepository.save(itemCarrito);
                return true;
            }
            else return false;
        } else if (operacion.equals("restar")) {
            ItemCarrito itemCarrito = itemCarritoRepository.findByIdProductoAndAlias(idProducto, alias).orElse(null);
            if (itemCarrito != null && itemCarrito.getCantidad() >= cantidad) {
                itemCarrito.setCantidad(itemCarrito.getCantidad() - cantidad);
                itemCarritoRepository.save(itemCarrito);
                return true;
            }
            else return false;
        } else return false;
    }

}
