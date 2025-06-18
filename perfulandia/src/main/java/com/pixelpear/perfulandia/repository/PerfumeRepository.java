package com.pixelpear.perfulandia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pixelpear.perfulandia.model.Perfume;

@Repository
public interface PerfumeRepository extends JpaRepository<Perfume, Long> {
    Perfume findByIdPerfume(Long idPerfume);
}
