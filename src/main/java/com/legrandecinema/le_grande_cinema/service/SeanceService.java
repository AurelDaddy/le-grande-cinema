package com.legrandecinema.le_grande_cinema.service;

import com.legrandecinema.le_grande_cinema.model.Seance;

import java.util.List;
import java.util.Optional;

public interface SeanceService {

    //Attention, on ne veut ici pas récupérer les séances passées
    List<Seance> getSeancesByFilm(Integer idFilm);

    Optional<Seance> getSeanceById(Integer id);

    Seance createSeance(Seance seance);

    void deleteSeance(Integer id);

}
