package com.legrandecinema.le_grande_cinema.controller;

import com.legrandecinema.le_grande_cinema.dto.FilmDto;
import com.legrandecinema.le_grande_cinema.mapper.FilmMapper;
import com.legrandecinema.le_grande_cinema.service.FilmService;
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
@RequestMapping("/api/films")
@Tag(
        name = "Films",
        description = "Consultation des films à l'affiche du cinéma"
)
public class FilmController {

    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    @Operation(
            summary = "Récupérer les films à l'affiche",
            description = "Retourne la liste des films ayant des séances programmées."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Liste des films récupérés avec succès"
    )
    public List<FilmDto> getFilmsAlAffiche() {
        return filmService.getFilmsALAffiche()
                .stream()
                .map(FilmMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Récupérer un film par son identifiant",
            description = "Retourne les informations détaillées d'un film à partir de son identifiant"
    )
    @ApiResponses(
            {@ApiResponse(
                    responseCode = "200",
                    description = "Film trouvé"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Film non trouvé"
            )
            })

    public ResponseEntity<FilmDto> getFilmById(
            @Parameter(
                    description = "Identifiant du film",
                    example = "1"
            )
            @PathVariable Integer id
    ) {

        return filmService.getFilmById(id)
                .map(FilmMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
