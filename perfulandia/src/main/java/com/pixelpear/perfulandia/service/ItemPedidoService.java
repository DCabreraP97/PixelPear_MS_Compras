package com.pixelpear.perfulandia.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pixelpear.perfulandia.model.ItemPedido;
import com.pixelpear.perfulandia.repository.ItemPedidoRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor

public class ItemPedidoService {
    private final ItemPedidoRepository itemPedidoRepository;

    public void guardarItemsPedido(List<ItemPedido> itemsPedidos) {
        itemPedidoRepository.saveAll(itemsPedidos);
    }

    public List<ItemPedido> obtenerItemsPorAlias(String alias) {
        return itemPedidoRepository.findByAlias(alias);
    }

}
