package com.legrandecinema.le_grande_cinema.controller;

import com.legrandecinema.le_grande_cinema.service.FrequentationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin/frequentation")
public class AdminFrequentationController {

    private final FrequentationService frequentationService;

    public AdminFrequentationController(FrequentationService frequentationService) {
        this.frequentationService = frequentationService;
    }

    @GetMapping("/{id}/seance")
    public ResponseEntity<Double> getTauxDeRemplissage(@PathVariable Integer id) {

        double taux = frequentationService.getTauxDeRemplissage(id);

        return ResponseEntity.ok(taux);
    }

    @GetMapping
    public ResponseEntity<Double> getTauxDeRemplissageParPeriode(
            @RequestParam LocalDateTime dateDebut,
            @RequestParam LocalDateTime dateFin
    ){
        double taux = frequentationService.getTauxDeRemplissageParPeriode(dateDebut, dateFin);

        return ResponseEntity.ok(taux);
    }


}
