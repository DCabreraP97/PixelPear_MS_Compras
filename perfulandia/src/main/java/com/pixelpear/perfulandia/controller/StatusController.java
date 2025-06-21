package com.pixelpear.perfulandia.controller;

import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "Estado", description = "Controlador para verificar el estado del microservicio")
@RestController
public class StatusController {
    @GetMapping("/status")
    public ResponseEntity<String> getStatus() {
        return ResponseEntity.ok("MS-Compras está operativo 🥂");
    }
    
}

