package com.pixelpear.perfulandia.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.pixelpear.perfulandia.model.ItemCarrito;
import com.pixelpear.perfulandia.repository.ItemCarritoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
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

}
