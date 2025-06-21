package com.pixelpear.perfulandia.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pixelpear.perfulandia.model.Descuento;
import com.pixelpear.perfulandia.repository.DescuentoRepository;

@ExtendWith(MockitoExtension.class)
public class DescuentoServiceTest {

    @Mock
    private DescuentoRepository descuentoRepository;

    @InjectMocks
    private DescuentoService descuentoService;

    @Test
    void buscarDescuentoPorCodigo_DeberiaRetornarDescuento_existe()
    {
        String codigo = "OFERTONJUNIO";
        
        Descuento descuento = new Descuento(1L,"OFERTONJUNIO","Oferta especial de junio", 15.0, LocalDate.now(), LocalDate.now().plusDays(30));
        when(descuentoRepository.findByCodigoDescuentoIgnoreCase(codigo)).thenReturn(descuento);

        Descuento resultado = descuentoService.buscarDescuentoPorCodigo(codigo);

        assertEquals(descuento.getIdDescuento(), resultado.getIdDescuento());
        assertEquals(descuento.getCodigoDescuento(), resultado.getCodigoDescuento());
        assertEquals(descuento.getPorcentajeDescuento(), resultado.getPorcentajeDescuento());
    }

    @Test
    void buscarDescuentoPorCodigo_DeberiaRetornarNull_noExiste()
    {
        String codigo = "OFERTONDICIEMBRE";
        
        when(descuentoRepository.findByCodigoDescuentoIgnoreCase(codigo)).thenReturn(null);

        Descuento resultado = descuentoService.buscarDescuentoPorCodigo(codigo);

        assertNull(resultado);
    }

}


