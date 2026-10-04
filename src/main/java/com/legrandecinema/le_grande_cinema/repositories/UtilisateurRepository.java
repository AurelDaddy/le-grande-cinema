package com.legrandecinema.le_grande_cinema.repositories;

import com.legrandecinema.le_grande_cinema.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {
}
