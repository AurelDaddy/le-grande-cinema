package com.legrandecinema.le_grande_cinema.dto;

public record CreateFilmRequest(
        String titre,
        int dureeMinute,
        int anneeSortie,
        String genre,
        String realisateur,
        String synopsis,
        String urlAffiche
) {
}