package com.legrandecinema.le_grande_cinema.service.impl;

import com.legrandecinema.le_grande_cinema.model.Seance;
import com.legrandecinema.le_grande_cinema.repositories.SeanceRepository;
import com.legrandecinema.le_grande_cinema.service.SeanceService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SeanceServiceImpl implements SeanceService {

    // Référence vers l'instance de SeanceRepository injectée par Spring
    private final SeanceRepository seanceRepository;

    //Injection de SeanceRepository par le constructeur
    public SeanceServiceImpl(SeanceRepository seanceRepository) {
        this.seanceRepository = seanceRepository;
    }

    @Override
    public List<Seance> getSeancesByFilm(Integer idFilm) {
        return seanceRepository.findByFilmIdFilmAndDateHeureAfter(idFilm, LocalDateTime.now());
    }
    @Override
    public Optional<Seance> getSeanceById(Integer id) {
        return seanceRepository.findById(id);
    }

    @Override
    public Seance createSeance(Seance seance) {
        return seanceRepository.save(seance);
    }

    @Override
    public void deleteSeance(Integer id) {
        seanceRepository.deleteById(id);

    }
}
