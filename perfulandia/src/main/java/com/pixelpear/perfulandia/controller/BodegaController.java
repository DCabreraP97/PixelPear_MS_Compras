package com.pixelpear.perfulandia.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pixelpear.perfulandia.model.Perfume;
import com.pixelpear.perfulandia.service.PerfumeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@Tag(name = "Bodega", description = "Controlador para gestionar perfumes en la bodega")
@RestController
@RequiredArgsConstructor
@RequestMapping("bodega")

// BodegaController es un CRUD de los perfumes en la BD
public class BodegaController {
    
    private final PerfumeService perfumeService;

    @Operation(summary = "Mostrar todos los perfumes", description = "Devuelve una lista de todos los perfumes disponibles en la bodega")
    @GetMapping("/perfumes")
    public ResponseEntity<List<Perfume>> mostrarPerfumes() {
        return ResponseEntity.ok(perfumeService.mostrarPerfumes());
    }
    
    @Operation(summary = "Mostrar un perfume por ID", description = "Devuelve el perfume correspondiente al ID proporcionado")
    @GetMapping("/perfume/{idPerfume}")
    public ResponseEntity<Perfume> mostrarPerfume(@PathVariable Long idPerfume) {
        Perfume perfume = perfumeService.buscarPerfumePorId(idPerfume);
        if (perfume != null) {
            return ResponseEntity.ok(perfume);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @Operation(summary = "Agregar un nuevo perfume", description = "Añade un nuevo perfume a la bodega")
    @PostMapping("/agregarPerfume")
    public ResponseEntity<Perfume> agregarPerfume(@RequestBody Perfume perfume) {
        perfumeService.guardarPerfume(perfume);
        return ResponseEntity.status(201).body(perfume);
    }

    @Operation(summary = "Eliminar un perfume", description = "Elimina un perfume de la bodega")
    @DeleteMapping("/eliminarPerfume/{idPerfume}")
    public ResponseEntity<String> eliminarPerfume(@PathVariable Long idPerfume) {
        if (perfumeService.existePerfume(idPerfume)) {
            perfumeService.eliminarPerfume(idPerfume);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Actualizar un perfume", description = "Actualiza los detalles de un perfume existente")
    @PutMapping("/actualizarPerfume/{idPerfume}")
    public ResponseEntity<Perfume> actualizarPerfume(@PathVariable Long idPerfume, @RequestBody Perfume perfume) {
        if (perfumeService.existePerfume(idPerfume)) {
            perfume.setIdPerfume(idPerfume);
            perfumeService.guardarPerfume(perfume);
            return ResponseEntity.ok(perfume);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
