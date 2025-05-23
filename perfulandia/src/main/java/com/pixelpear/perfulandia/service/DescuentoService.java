package com.pixelpear.perfulandia.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(DescuentoService.class);
    public Descuento buscarDescuentoPorCodigo(String codigoDescuento) {
        logger.info("Buscando descuento por código: {}", codigoDescuento);
        Descuento descuento = descuentoRepository.findByCodigoDescuentoIgnoreCase(codigoDescuento);
        logger.info("Descuento encontrado: {}", descuento);
        return descuento;
    }

}
