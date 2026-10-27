package com.legrandecinema.le_grande_cinema.controller;

import com.legrandecinema.le_grande_cinema.dto.BilletDto;
import com.legrandecinema.le_grande_cinema.service.BilletService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/billets")
public class BilletController {

    private final BilletService billetService;

    public BilletController(BilletService billetService) {
        this.billetService = billetService;
    }

    @GetMapping
    public ResponseEntity<List<BilletDto>> getBilletsUtilisateur(
            Authentication authentication
    ) {

        String email = authentication.getName();

        List<BilletDto> billets =
                billetService.getBilletsUtilisateur(email);

        return ResponseEntity.ok(billets);
    }

    @GetMapping("/{idReservation}")
    public ResponseEntity<BilletDto> getBilletByReservation(
            @PathVariable Integer idReservation,
            Authentication authentication
    ) {

        String email = authentication.getName();

        BilletDto billet =
                billetService.getBilletByReservation(
                        email,
                        idReservation
                );

        return ResponseEntity.ok(billet);
    }
}