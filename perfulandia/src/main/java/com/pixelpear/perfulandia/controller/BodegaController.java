package com.pixelpear.perfulandia.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pixelpear.perfulandia.model.Perfume;
import com.pixelpear.perfulandia.service.PerfumeService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("bodega")

// BodegaController es un CRUD de los perfumes en la BD
public class BodegaController {
    
    private final PerfumeService perfumeService;

    @GetMapping("/perfumes")
    public ResponseEntity<List<Perfume>> mostrarPerfumes() {
        return ResponseEntity.ok(perfumeService.mostrarPerfumes());
    }
    
    @GetMapping("/perfume/{idPerfume}")
    public ResponseEntity<Perfume> mostrarPerfume(@PathVariable Long idPerfume) {
        Perfume perfume = perfumeService.buscarPerfumePorId(idPerfume);
        if (perfume != null) {
            return ResponseEntity.ok(perfume);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/agregarPerfume")
    public ResponseEntity<Perfume> agregarPerfume(@RequestBody Perfume perfume) {
        perfumeService.guardarPerfume(perfume);
        return ResponseEntity.ok(perfume);
    }
    
    @DeleteMapping("/eliminarPerfume/{idPerfume}")
    public ResponseEntity<String> eliminarPerfume(@PathVariable Long idPerfume) {
        if (perfumeService.existePerfume(idPerfume)) {
            perfumeService.eliminarPerfume(idPerfume);
            return ResponseEntity.ok("Perfume eliminado con éxito");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

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
