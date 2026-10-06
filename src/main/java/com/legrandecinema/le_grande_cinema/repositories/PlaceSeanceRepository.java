package com.legrandecinema.le_grande_cinema.repositories;

import com.legrandecinema.le_grande_cinema.model.PlaceSeance;
import com.legrandecinema.le_grande_cinema.model.type.StatutReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface PlaceSeanceRepository extends JpaRepository<PlaceSeance, Integer> {

    //compte la Disponibilité de place pour une séance
    long countBySeanceIdSeanceAndReservationIsNull(Integer idSeance);



    //Compte la Fréquentation
    //getTauxDeRemplissage et getTauxDeRemplissageParPeriode
    //de FrequentationService
    long countBySeanceIdSeance(Integer idSeance);

    long countBySeanceIdSeanceAndReservationStatut(
            Integer idSeance,
            StatutReservation statut
    );

    long countBySeanceDateHeureBetween(LocalDateTime dateDebut,
                                       LocalDateTime dateFin
    );

    long countBySeanceDateHeureBetweenAndReservationStatut(LocalDateTime dateDebut,
                                                           LocalDateTime dateFin,
                                                           StatutReservation statut
    );
}
