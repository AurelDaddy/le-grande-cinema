package com.legrandecinema.le_grande_cinema.controller;

import com.legrandecinema.le_grande_cinema.dto.CreateSeanceRequest;
import com.legrandecinema.le_grande_cinema.dto.SeanceDto;
import com.legrandecinema.le_grande_cinema.exception.RessourceIntrouvableException;
import com.legrandecinema.le_grande_cinema.mapper.SeanceMapper;
import com.legrandecinema.le_grande_cinema.model.Film;
import com.legrandecinema.le_grande_cinema.model.Salle;
import com.legrandecinema.le_grande_cinema.model.Seance;
import com.legrandecinema.le_grande_cinema.service.FilmService;
import com.legrandecinema.le_grande_cinema.service.SalleService;
import com.legrandecinema.le_grande_cinema.service.SeanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RestController
@RequestMapping("/api/admin/seances")
public class AdminSeanceController {

    private final SeanceService seanceService;
    private final SalleService salleService;
    private final FilmService filmService;


    public AdminSeanceController(SeanceService seanceService, SalleService salleService, FilmService filmService) {
        this.seanceService = seanceService;
        this.salleService = salleService;
        this.filmService = filmService;
    }

    @PostMapping
    public ResponseEntity<SeanceDto> createSeance(
            @RequestBody CreateSeanceRequest request) {

        Salle salle = salleService
                .getSalleById(request.idSalle())
                .orElseThrow(() ->
                        new RessourceIntrouvableException(
                                "Salle introuvable"
                        ));

        Film film = filmService
                .getFilmById(request.idFilm())
                .orElseThrow(() ->
                        new RessourceIntrouvableException(
                                "Film introuvable"
                        )
                );

        //on crée la séance
        Seance seance = new Seance();
        seance.setSalle(salle);
        seance.setFilm(film);
        seance.setDateHeure(request.dateHeure());
        seance.setPrix(request.prix());
        seance.setVersion(request.version());

        // on l'envoie au service pour qu'elle soit enregistrée en base arpès un passage par le repo
        Seance seanceEnregistree = seanceService.createSeance(seance);

        //puis on mappe en seanceDto pour ne pas exposer directement l'entité JPA
        SeanceDto seanceDto = SeanceMapper.toDto(seanceEnregistree);

        return ResponseEntity.ok(seanceDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSeance(@PathVariable Integer id){

        seanceService.deleteSeance(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<SeanceDto>> getAllSeances(){

        List<SeanceDto> seances = seanceService
                .getAllSeances()
                .stream()
                .map(SeanceMapper::toDto)
                .toList();

        return ResponseEntity.ok(seances);
    }

}