package com.legrandecinema.le_grande_cinema.controller;

import com.legrandecinema.le_grande_cinema.dto.CreatePaiementRequest;
import com.legrandecinema.le_grande_cinema.dto.PaiementDto;
import com.legrandecinema.le_grande_cinema.mapper.PaiementMapper;
import com.legrandecinema.le_grande_cinema.model.Paiement;
import com.legrandecinema.le_grande_cinema.service.PaiementService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/paiements")
@Tag(
        name = "Paiements",
        description = "Gestion des paiements d'un utilisateur authentifié"
)
@SecurityRequirement(name = "basicAuth")
public class PaiementController {

    private final PaiementService paiementService;

    public PaiementController(PaiementService paiementService) {
        this.paiementService = paiementService;
    }

    @PostMapping
    @Operation(
            summary = "Effectuer un paiement",
            description = "Effectue le paiement d'une réservation appartenant à l'utilisateur authentifié."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Paiement traité avec succès"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Demande de paiement invalide"
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
                    description = "Paiement impossible : réservation déjà payée, annulée ou appartenant à un autre utilisateur"
            )
    })
    public ResponseEntity<PaiementDto> effectuerPaiement(

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Identifiant de la réservation à payer",
                    required = true
            )
            @RequestBody CreatePaiementRequest request,

            Authentication authentication
    ) {

        String email = authentication.getName();

        Paiement paiement =
                paiementService.effectuerPaiement(
                        email,
                        request.idReservation()
                );

        PaiementDto paiementDto =
                PaiementMapper.toDto(paiement);

        return ResponseEntity.ok(paiementDto);
    }

    @GetMapping("/{idPaiement}")
    @Operation(
            summary = "Récupérer un paiement",
            description = "Retourne un paiement appartenant à l'utilisateur authentifié."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Paiement trouvé"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Utilisateur non authentifié"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Paiement introuvable"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Le paiement n'appartient pas à l'utilisateur connecté"
            )
    })
    public ResponseEntity<PaiementDto> getPaiementById(

            @Parameter(
                    description = "Identifiant du paiement",
                    example = "1"
            )
            @PathVariable int idPaiement,

            Authentication authentication
    ) {

        String email = authentication.getName();

        Paiement paiement =
                paiementService.getPaiementById(
                        email,
                        idPaiement
                );

        PaiementDto paiementDto =
                PaiementMapper.toDto(paiement);

        return ResponseEntity.ok(paiementDto);
    }
}