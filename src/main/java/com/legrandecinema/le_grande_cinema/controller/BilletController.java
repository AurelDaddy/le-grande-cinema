package com.legrandecinema.le_grande_cinema.controller;

import com.legrandecinema.le_grande_cinema.dto.BilletDto;
import com.legrandecinema.le_grande_cinema.service.BilletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/billets")
@Tag(
        name = "Billets",
        description = "Consultation des billets d'un utilisateur authentifié"
)
@SecurityRequirement(name = "basicAuth")
public class BilletController {

    private final BilletService billetService;

    public BilletController(BilletService billetService) {
        this.billetService = billetService;
    }

    @GetMapping
    @Operation(
            summary = "Récupéreration des billets de l'utilisateur",
            description = "Retourne la liste des billets appartenant à l'utilisateur authentifié."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste des billets récupérée avec succès"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Utilisateur non authentifié"
            )
    })
    public ResponseEntity<List<BilletDto>> getBilletsUtilisateur(
            Authentication authentication
    ) {

        String email = authentication.getName();

        List<BilletDto> billets =
                billetService.getBilletsUtilisateur(email);

        return ResponseEntity.ok(billets);
    }

    @GetMapping("/{idReservation}")
    @Operation(
            summary = "Récupérer un billet à partir d'une réservation",
            description = "Retourne le billet associé à une réservation de l'utilisateur authentifié."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Billet récupéré avec succès"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Utilisateur non authentifié"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Réservation introuvable"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Billet indisponible : réservation non confirmée, appartenant à un autre utilisateur ou sans paiement accepté"
            )
    })
    public ResponseEntity<BilletDto> getBilletByReservation(

            @Parameter(
                    description = "Identifiant de la réservation associée au billet",
                    example = "1"
            )
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