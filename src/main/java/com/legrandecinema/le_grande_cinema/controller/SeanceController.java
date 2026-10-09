package com.legrandecinema.le_grande_cinema.controller;

import com.legrandecinema.le_grande_cinema.dto.SeanceDto;
import com.legrandecinema.le_grande_cinema.mapper.SeanceMapper;
import com.legrandecinema.le_grande_cinema.service.SeanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SeanceController {

    private final SeanceService seanceService;

    public SeanceController(SeanceService seanceService) {
        this.seanceService = seanceService;
    }

    @GetMapping("/films/{idFilm}/seances")
    public List<SeanceDto> getSeancesByFilm(@PathVariable Integer idFilm){
        return seanceService.getSeancesByFilm(idFilm)
                .stream()
                .map(SeanceMapper::toDto)
                .toList();
    }

    @GetMapping("/seances/{id}")
    public ResponseEntity<SeanceDto> getSeanceById(@PathVariable Integer id){
        return seanceService.getSeanceById(id)
                .map(SeanceMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
