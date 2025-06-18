package com.pixelpear.perfulandia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pixelpear.perfulandia.model.Factura;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {
    
}
