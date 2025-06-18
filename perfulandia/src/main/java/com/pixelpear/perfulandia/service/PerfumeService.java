package com.pixelpear.perfulandia.service;

import org.springframework.stereotype.Service;

import com.pixelpear.perfulandia.model.Perfume;
import com.pixelpear.perfulandia.repository.PerfumeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class PerfumeService {
    private final PerfumeRepository perfumeRepository;
    public boolean existePerfume(Long idPerfume) {
        return perfumeRepository.existsById(idPerfume);
    }
    public Perfume buscarPerfumePorId(Long idPerfume) {
        return perfumeRepository.findByIdPerfume(idPerfume);
    }
    public void guardarPerfume(Perfume perfumeBD) {
        perfumeRepository.save(perfumeBD);
    }
}