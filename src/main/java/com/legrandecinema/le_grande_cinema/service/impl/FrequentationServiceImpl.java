package com.legrandecinema.le_grande_cinema.service.impl;

import com.legrandecinema.le_grande_cinema.model.type.StatutReservation;
import com.legrandecinema.le_grande_cinema.repositories.PlaceSeanceRepository;
import com.legrandecinema.le_grande_cinema.service.FrequentationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FrequentationServiceImpl implements FrequentationService {

    private final PlaceSeanceRepository placeSeanceRepository;

    public FrequentationServiceImpl(PlaceSeanceRepository placeSeanceRepository) {
        this.placeSeanceRepository = placeSeanceRepository;
    }

    @Override
    public double getTauxDeRemplissage(Integer idSeance) {
       long nombrePlacesTotal = placeSeanceRepository.countBySeanceIdSeance(idSeance);

       //au cas où il n'y a aucune placeSeance en base
        if (nombrePlacesTotal == 0) {
            return 0.0;
        }
        long nombrePlacesConfirmees =
                placeSeanceRepository.countBySeanceIdSeanceAndReservationStatut(
                        idSeance,
                        StatutReservation.CONFIRMEE
                );

        return (double) nombrePlacesConfirmees*100/nombrePlacesTotal;
    }

    @Override
    public double getTauxDeRemplissageParPeriode(LocalDateTime dateDebut, LocalDateTime dateFin) {
        long nombrePlacesTotalPeriode =
                placeSeanceRepository.countBySeanceDateHeureBetween(
                        dateDebut,
                        dateFin
                );

        //au cas où il n'y a aucune placeSeance en base
        if (nombrePlacesTotalPeriode == 0) {
            return 0.0;
        }
        long nombrePlacesConfirmeesPeriode =
                placeSeanceRepository.countBySeanceDateHeureBetweenAndReservationStatut(
                        dateDebut,
                        dateFin,
                        StatutReservation.CONFIRMEE
                );

        return (double) nombrePlacesConfirmeesPeriode*100/nombrePlacesTotalPeriode;
    }
}
