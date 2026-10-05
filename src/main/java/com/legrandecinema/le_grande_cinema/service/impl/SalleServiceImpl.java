package com.legrandecinema.le_grande_cinema.service.impl;

import com.legrandecinema.le_grande_cinema.model.Salle;
import com.legrandecinema.le_grande_cinema.repositories.SalleRepository;
import com.legrandecinema.le_grande_cinema.service.SalleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SalleServiceImpl implements SalleService {

    private final SalleRepository salleRepository;

    public SalleServiceImpl(SalleRepository salleRepository) {
        this.salleRepository = salleRepository;
    }

    @Override
    public Optional<Salle> getSalleById(Integer id) {
        return salleRepository.findById(id);
    }

    @Override
    public List<Salle> getAllSalles() {
        return salleRepository.findAll();

    }
}