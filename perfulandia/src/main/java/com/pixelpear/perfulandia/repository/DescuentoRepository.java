package com.pixelpear.perfulandia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pixelpear.perfulandia.model.Descuento;

@Repository
public interface DescuentoRepository extends JpaRepository<Descuento, Long> {
    Descuento findByCodigoDescuentoIgnoreCase(String codigoDescuento);
}
