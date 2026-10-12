package com.legrandecinema.le_grande_cinema.controller;

import com.legrandecinema.le_grande_cinema.dto.CreatePaiementRequest;
import com.legrandecinema.le_grande_cinema.dto.PaiementDto;
import com.legrandecinema.le_grande_cinema.mapper.PaiementMapper;
import com.legrandecinema.le_grande_cinema.model.Paiement;
import com.legrandecinema.le_grande_cinema.service.PaiementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/paiements")
public class PaiementController {

    private final PaiementService paiementService;

    public PaiementController(PaiementService paiementService) {
        this.paiementService = paiementService;
    }

    @PostMapping
    public ResponseEntity<PaiementDto> effectuerPaiement(@RequestBody CreatePaiementRequest request){

        Paiement paiement = paiementService.effectuerPaiement(
                request.idReservation(),
                request.montant()
        );

        PaiementDto paiementDto = PaiementMapper.toDto(paiement);

        return ResponseEntity.ok(paiementDto);
    }
}
