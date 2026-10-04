package com.legrandecinema.le_grande_cinema.repositories;

import com.legrandecinema.le_grande_cinema.model.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaiementRepository extends JpaRepository<Paiement, Integer> {
}
