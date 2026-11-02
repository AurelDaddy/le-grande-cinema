package com.legrandecinema.le_grande_cinema.controller;

import com.legrandecinema.le_grande_cinema.dto.SeanceDto;
import com.legrandecinema.le_grande_cinema.mapper.SeanceMapper;
import com.legrandecinema.le_grande_cinema.service.PlaceSeanceService;
import com.legrandecinema.le_grande_cinema.service.SeanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@Tag(
        name = "Seances",
        description = "Consultation des séances d'un film"
)
public class SeanceController {

    private final SeanceService seanceService;
    private final PlaceSeanceService placeSeanceService;

    public SeanceController(SeanceService seanceService, PlaceSeanceService placeSeanceService) {
        this.seanceService = seanceService;
        this.placeSeanceService = placeSeanceService;
    }



    @GetMapping("/films/{idFilm}/seances")
    @Operation(
            summary = "Récupérer les séances d'un film",
            description = "Retourne la liste des séances à venir d'un film à l'affche"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Séances trouvées"
    )
    public List<SeanceDto> getSeancesByFilm(
            @Parameter(
                    description = "Identifiant du film",
                    example = "1"
            )
            @PathVariable Integer idFilm
    ){
        return seanceService.getSeancesByFilm(idFilm)
                .stream()
                .map(SeanceMapper::toDto)
                .toList();
    }

    @GetMapping("/seances/{idSeance}")
    @Operation(
            summary = "Récupérer le détail de la séance",
            description = "Retourne les informations détaillées d'une séance à partir de son identifiant"
    )
    @ApiResponses(
            {@ApiResponse(
                    responseCode = "200",
                    description = "Séance trouvée"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Séance non trouvée"
            )}
    )
    public ResponseEntity<SeanceDto> getSeanceById(
            @Parameter(
                    description = "Identifiant de la séance",
                    example = "1"
            )
            @PathVariable Integer idSeance){
        return seanceService.getSeanceById(idSeance)
                .map(SeanceMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());


    }

    @GetMapping("/seances/{idSeance}/places-disponibles")
    @Operation(
            summary = "Récupère le nombre de places disponibles d'une séance",
            description = "Retourne le nombre de places disponibles à partir de l'identifaint d'une séance"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Le nombre de places restantes a été retourné avec succès"
    )
    public long getNombrePlacesDisponibles(
            @Parameter(
                    description = "Identifiant de la séance",
                    example = "1"
            )
            @PathVariable Integer idSeance){
        return placeSeanceService.getNombrePlacesDisponibles(idSeance);
    }
}
