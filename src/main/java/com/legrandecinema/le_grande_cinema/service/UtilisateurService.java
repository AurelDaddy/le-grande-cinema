package com.legrandecinema.le_grande_cinema.service;

import com.legrandecinema.le_grande_cinema.model.Utilisateur;

import java.util.Optional;

public interface UtilisateurService {

    Utilisateur inscrireUtilisateur(Utilisateur utilisateur);

    Optional<Utilisateur> getUtilisateurByEmail(String email);
}
