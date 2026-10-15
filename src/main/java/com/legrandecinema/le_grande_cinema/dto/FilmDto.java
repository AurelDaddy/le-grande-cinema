package com.legrandecinema.le_grande_cinema.dto;

public record FilmDto(
        Integer idFilm,
        String titre,
        int dureeMinute,
        int anneeSortie,
        String genre,
        String realisateur,
        String synopsis,
        String urlAffiche)
{


}
