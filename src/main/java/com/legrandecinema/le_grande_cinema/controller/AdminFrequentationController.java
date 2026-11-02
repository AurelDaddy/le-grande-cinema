package com.legrandecinema.le_grande_cinema.controller;

import com.legrandecinema.le_grande_cinema.service.FrequentationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin/frequentation")
@Tag(
        name = "Administration : Fréquentation",
        description = "Consultation des statistiques de fréquentation par un administrateur"
)
@SecurityRequirement(name = "basicAuth")
public class AdminFrequentationController {

    private final FrequentationService frequentationService;

    public AdminFrequentationController(
            FrequentationService frequentationService
    ) {
        this.frequentationService = frequentationService;
    }

    @GetMapping("/{id}/seance")
    @Operation(
            summary = "Récupérer le taux de remplissage d'une séance",
            description = "Retourne le taux de remplissage d'une séance à partir de son identifiant."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Taux de remplissage récupéré avec succès"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Utilisateur non authentifié"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Accès réservé aux administrateurs"
            )
    })
    public ResponseEntity<Double> getTauxDeRemplissage(

            @Parameter(
                    description = "Identifiant de la séance",
                    example = "1"
            )
            @PathVariable Integer id
    ) {

        double taux =
                frequentationService.getTauxDeRemplissage(id);

        return ResponseEntity.ok(taux);
    }

    @GetMapping
    @Operation(
            summary = "Récupérer le taux de remplissage sur une période",
            description = "Retourne le taux de remplissage moyen des séances comprises entre deux dates."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Taux de remplissage récupéré avec succès"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Utilisateur non authentifié"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Accès réservé aux administrateurs"
            )
    })
    public ResponseEntity<Double> getTauxDeRemplissageParPeriode(

            @Parameter(
                    description = "Date et heure de début de la période",
                    example = "2026-11-01T00:00:00"
            )
            @RequestParam LocalDateTime dateDebut,

            @Parameter(
                    description = "Date et heure de fin de la période",
                    example = "2026-11-30T23:59:59"
            )
            @RequestParam LocalDateTime dateFin
    ) {

        double taux =
                frequentationService
                        .getTauxDeRemplissageParPeriode(
                                dateDebut,
                                dateFin
                        );

        return ResponseEntity.ok(taux);
    }
}