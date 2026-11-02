package com.legrandecinema.le_grande_cinema.controller;

import com.legrandecinema.le_grande_cinema.dto.CreateFilmRequest;
import com.legrandecinema.le_grande_cinema.dto.FilmDto;
import com.legrandecinema.le_grande_cinema.mapper.FilmMapper;
import com.legrandecinema.le_grande_cinema.model.Film;
import com.legrandecinema.le_grande_cinema.service.FilmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/films")
@Tag(
        name = "Administration : Films",
        description = "Gestion des films pour l'administrateur"
)
@SecurityRequirement(name = "basicAuth")
public class AdminFilmController {

    private final FilmService filmService;

    public AdminFilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    @Operation(
            summary = "Créer un film",
            description = "Ajoute un nouveau film dans le catalogue du cinéma"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Film créé avec succès"
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

    public ResponseEntity<FilmDto> createFilm(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Informations du films à ajouter",
                    required = true
            )
            @RequestBody CreateFilmRequest request
    ) {

        Film film = new Film();

        film.setTitre(request.titre());
        film.setDureeMinute(request.dureeMinute());
        film.setAnneeSortie(request.anneeSortie());
        film.setGenre(request.genre());
        film.setRealisateur(request.realisateur());
        film.setSynopsis(request.synopsis());
        film.setUrlAffiche(request.urlAffiche());

        Film filmEnregistre = filmService.createFilm(film);

        FilmDto filmDto = FilmMapper.toDto(filmEnregistre);

        return ResponseEntity.ok(filmDto);
    }

    @GetMapping
    @Operation(
            summary = "Récupérer tous les films",
            description = "Retourne tous les films du catalogue y compris ceux qui ne sont pas à l'affiche"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Liste des films récupérée avec succès"
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
    public ResponseEntity<List<FilmDto>> getAllFilms() {

        List<FilmDto> films = filmService
                .getAllFilms()
                .stream()
                .map(FilmMapper::toDto)
                .toList();

        return ResponseEntity.ok(films);
    }

}