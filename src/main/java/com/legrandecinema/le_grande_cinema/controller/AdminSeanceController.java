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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/admin/seances")
@Tag(
        name = "Administration : Séances",
        description = "Gestion de la programmation des séances par un administrateur"
)
@SecurityRequirement(name = "basicAuth")
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
    @Operation(
            summary = "Créer une séance",
            description = "Crée une nouvelle séance pour un film et une salle"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Séance créée avec succès"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Utilisateur non authentifié"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Accès réservé aux administrateurs"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Film ou salle introuvable"
            )
    })
    public ResponseEntity<SeanceDto> createSeance(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Informations nécessaires à la création d'une séance",
                    required = true
            )
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
    @Operation(
            summary = "Supprimer une séance",
            description = "Supprime une séance si aucune réservation n'y est associée."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Séance supprimée avec succès"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Utilisateur non authentifié"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Accès réservé aux administrateurs"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Suppression impossible car des réservations sont associées à cette séance"
            )
    })
    public ResponseEntity<Void> deleteSeance(
            @Parameter(
                    description = "Identifiant de la séance à supprimer",
                    example = "1"
            )
            @PathVariable Integer id){

        seanceService.deleteSeance(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(
            summary = "Récupérer toutes les séances",
            description = "Retourne la liste complète de toutes les séances enregistrées."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste des séances récupérée avec succès"
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
    public ResponseEntity<List<SeanceDto>> getAllSeances(){

        List<SeanceDto> seances = seanceService
                .getAllSeances()
                .stream()
                .map(SeanceMapper::toDto)
                .toList();

        return ResponseEntity.ok(seances);
    }

}