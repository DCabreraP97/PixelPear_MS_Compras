package com.pixelpear.perfulandia.controller;

import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pixelpear.perfulandia.model.Perfume;
import com.pixelpear.perfulandia.service.PerfumeService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(BodegaController.class)
public class BodegaControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean 
    private PerfumeService perfumeService;

    @Test
    public void mostrarPerfumes_DeberiaRetornar200yLista() throws Exception {
        List<Perfume> perfumes = List.of(
            new Perfume(1L, "Perfume uno", 3890.0, 100),
            new Perfume(2L, "Perfume dos", 4570.0, 150)
        );

        when(perfumeService.mostrarPerfumes()).thenReturn(perfumes);

        mockMvc.perform(get("/bodega/perfumes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].nombre").value("Perfume uno"))
            .andExpect(jsonPath("$[1].nombre").value("Perfume dos"));
    }

    @Test
    public void mostrarPerfumePorId_DeberiaRetornar200yPerfume() throws Exception {
        List<Perfume> perfumes = List.of(
            new Perfume(1L, "Perfume uno", 3890.0, 100),
            new Perfume(2L, "Perfume dos", 4570.0, 150)
        );

        when(perfumeService.buscarPerfumePorId(1L)).thenReturn(perfumes.get(0));

        mockMvc.perform(get("/bodega/perfume/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nombre").value("Perfume uno"));
    }

    @Test
    public void agregarPerfume_DeberiaRetornar201yPerfume() throws Exception {
        Perfume perfume = new Perfume(1L, "Perfume uno", 3890.0, 100);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(perfume);

        mockMvc.perform(post("/bodega/perfume/agregar")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(json))
                  .andExpect(status().isCreated())
                  .andExpect(jsonPath("$.nombre").value("Perfume uno"));
    }

    @Test
    public void eliminarPerfume_DeberiaRetornar204() throws Exception {
        when(perfumeService.existePerfume(1L)).thenReturn(true);

        mockMvc.perform(delete("/bodega/eliminarPerfume/1"))
            .andExpect(status().isNoContent());

        verify(perfumeService, times(1)).eliminarPerfume(1L);
    }

    @Test
    public void actualizarPerfume_DeberiaRetornar200yPerfumeActualizado() throws Exception {
        Perfume perfume = new Perfume(1L, "Perfume uno", 3890.0, 100);

        when(perfumeService.existePerfume(1L)).thenReturn(true);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(perfume);

        mockMvc.perform(put("/bodega/actualizarPerfume/1")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(json))
                  .andExpect(status().isOk())
                  .andExpect(jsonPath("$.nombre").value("Perfume uno"));
    }

}
