package com.legrandecinema.le_grande_cinema.controller;

import com.legrandecinema.le_grande_cinema.dto.CreatePaiementRequest;
import com.legrandecinema.le_grande_cinema.dto.PaiementDto;
import com.legrandecinema.le_grande_cinema.mapper.PaiementMapper;
import com.legrandecinema.le_grande_cinema.model.Paiement;
import com.legrandecinema.le_grande_cinema.service.PaiementService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/paiements")
public class PaiementController {

    private final PaiementService paiementService;

    public PaiementController(PaiementService paiementService) {
        this.paiementService = paiementService;
    }

    @PostMapping
    public ResponseEntity<PaiementDto> effectuerPaiement(
            @RequestBody CreatePaiementRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        Paiement paiement = paiementService.effectuerPaiement(
                email,
                request.idReservation()
        );

        PaiementDto paiementDto = PaiementMapper.toDto(paiement);

        return ResponseEntity.ok(paiementDto);
    }

    //avec authenticate afin d'éviter qu'un utilisateur accède au paiement d'un autre
    @GetMapping("/{idPaiement}")
    public ResponseEntity<PaiementDto> getPaiementById(@PathVariable int idPaiement,
                                                       Authentication authentication) {

        String email = authentication.getName();

        Paiement paiement = paiementService.getPaiementById(email, idPaiement);

        PaiementDto paiementDto = PaiementMapper.toDto(paiement);

        return ResponseEntity.ok(paiementDto);
    }
}
