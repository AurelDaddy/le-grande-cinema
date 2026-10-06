package com.legrandecinema.le_grande_cinema.service;

import java.time.LocalDateTime;

public interface FrequentationService {

    //Calcul le taux de remplissage d'une séance en comptant
    //toutes les placesSeances de cette séance
    double getTauxDeRemplissage(Integer idSeance);

    double getTauxDeRemplissageParPeriode(LocalDateTime dateDebut,
                                          LocalDateTime dateFin
    );
}
