package com.pixelpear.perfulandia.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pixelpear.perfulandia.model.Descuento;
import com.pixelpear.perfulandia.repository.DescuentoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DescuentoService {
    private final DescuentoRepository descuentoRepository;
    public Descuento buscarDescuentoPorCodigo(String codigoDescuento) {
        return descuentoRepository.findByCodigoDescuentoIgnoreCase(codigoDescuento);
    }

}
