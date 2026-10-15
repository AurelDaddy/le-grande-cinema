package com.legrandecinema.le_grande_cinema.mapper;

import com.legrandecinema.le_grande_cinema.dto.UtilisateurDto;
import com.legrandecinema.le_grande_cinema.model.Utilisateur;

public class UtilisateurMapper {
    private UtilisateurMapper() {
    }

    public static UtilisateurDto toDto(Utilisateur utilisateur) {
        return new UtilisateurDto(
                utilisateur.getIdUtilisateur(),
                utilisateur.getPrenom(),
                utilisateur.getNom(),
                utilisateur.getEmail(),
                utilisateur.getTelephone(),
                utilisateur.getTypeUtilisateur()
        );
    }
}
