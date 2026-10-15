package com.legrandecinema.le_grande_cinema.dto;

import com.legrandecinema.le_grande_cinema.model.type.TypeUtilisateur;

public record UtilisateurDto(
        Integer idUtilisateur,
        String prenom,
        String nom,
        String email,
        String telephone,
        TypeUtilisateur typeUtilisateur
) {
}
