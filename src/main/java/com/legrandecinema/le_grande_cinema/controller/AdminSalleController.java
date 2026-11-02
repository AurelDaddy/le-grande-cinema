package com.legrandecinema.le_grande_cinema.controller;

import com.legrandecinema.le_grande_cinema.dto.SalleDto;
import com.legrandecinema.le_grande_cinema.mapper.SalleMapper;
import com.legrandecinema.le_grande_cinema.service.SalleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/admin/salles")
@Tag(
        name = "Administration : Salles",
        description = "Consultation des salles par un administrateur"
)
@SecurityRequirement(name = "basicAuth")
public class AdminSalleController {

    private final SalleService salleService;

    public AdminSalleController(SalleService salleService) {
        this.salleService = salleService;
    }

    @GetMapping
    @Operation(
            summary = "Récupérer toutes les salles",
            description = "Retourne la liste complète des salles du cinéma."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste des salles récupérée avec succès"
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
    public ResponseEntity<List<SalleDto>> getAllSalles(){

        List<SalleDto> salles = salleService
                .getAllSalles()
                .stream()
                .map(SalleMapper::toDto)
                .toList();

        return ResponseEntity.ok(salles);
    }
}
