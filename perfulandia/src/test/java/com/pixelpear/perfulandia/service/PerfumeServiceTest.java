package com.pixelpear.perfulandia.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.pixelpear.perfulandia.model.Perfume;
import com.pixelpear.perfulandia.repository.PerfumeRepository;

@ExtendWith(MockitoExtension.class)
public class PerfumeServiceTest {

    @Mock
    private PerfumeRepository perfumeRepository;

    @InjectMocks
    private PerfumeService perfumeService;

    @Test
    void existePerfume_existe_DeberiaRetornarTrue() {
        Long idPerfume = 1L;
        when(perfumeRepository.existsById(idPerfume)).thenReturn(true);

        boolean resultado = perfumeService.existePerfume(idPerfume);

        assertTrue(resultado);
    }

    @Test
    void buscarPerfumePorId_DeberiaRetornarPerfume() {
        Long idPerfume = 1L;
        Perfume perfume = new Perfume(idPerfume, "Perfume Uno", 6700.0, 50);
        when(perfumeRepository.findByIdPerfume(idPerfume)).thenReturn(perfume);

        Perfume resultado = perfumeService.buscarPerfumePorId(idPerfume);

        assertEquals(idPerfume, resultado.getIdPerfume());
    }

    @Test
    void guardarPerfume_DeberiaGuardarCorrectamente() {
        Perfume perfume = new Perfume(1L, "Perfume Uno", 6700.0, 50);
        
        perfumeService.guardarPerfume(perfume);

        verify(perfumeRepository, times(1)).save(perfume);
    }

    @Test
    void mostrarPerfumes_DeberiaRetornarListaDePerfumes() {
        List<Perfume> ListaPerfumes =List.of(
            new Perfume(1L, "Perfume Uno", 6700.0, 50),
            new Perfume(2L, "Perfume Dos", 7500.0, 75)
        );

        when(perfumeRepository.findAll()).thenReturn(ListaPerfumes);

        List<Perfume> resultado = perfumeService.mostrarPerfumes();
        assertEquals(2, resultado.size());
    }

    @Test
    void eliminarPerfume_DeberiaEliminarCorrectamente() {
        Long idPerfume = 1L;

        perfumeService.eliminarPerfume(idPerfume);

        verify(perfumeRepository, times(1)).deleteById(idPerfume);
    }

}
