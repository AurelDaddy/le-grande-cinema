package com.legrandecinema.le_grande_cinema.service;

import com.legrandecinema.le_grande_cinema.model.Salle;

import java.util.List;
import java.util.Optional;

public interface SalleService {

    Optional<Salle> getSalleById(Integer id);

    List<Salle> getAllSalles();

}
