package com.pixelpear.perfulandia.controller;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.pixelpear.perfulandia.model.Perfume;
import com.pixelpear.perfulandia.service.PerfumeService;

@WebMvcTest(BodegaController.class)
public class BodegaControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean 
    private PerfumeService perfumeService;

    @Test
    public void mostrarPerfumesDeberiaRetornar200yLista() throws Exception {
        List<Perfume> perfumes = List.of(
            new Perfume(1L, "Perfume A", "Descripción A", 100.0),
            new Perfume(2L, "Perfume B", "Descripción B", 150.0)
        );

        when(perfumeService.mostrarPerfumes()).thenReturn(perfumes);
        
        mockMvc.perform(get("/bodega/perfumes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].nombre").value("Perfume A"))
            .andExpect(jsonPath("$[1].nombre").value("Perfume B"));
    }
}
