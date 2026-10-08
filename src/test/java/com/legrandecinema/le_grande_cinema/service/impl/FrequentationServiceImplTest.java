package com.legrandecinema.le_grande_cinema.service.impl;

import com.legrandecinema.le_grande_cinema.model.type.StatutReservation;
import com.legrandecinema.le_grande_cinema.repositories.PlaceSeanceRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FrequentationServiceImplTest {

    @Test
    void shouldReturnZeroWhenNoPlaceSeanceExistsForSeance(){

        PlaceSeanceRepository placeSeanceRepository = Mockito.mock(PlaceSeanceRepository.class);

        FrequentationServiceImpl frequentationService = new FrequentationServiceImpl(placeSeanceRepository);

        Mockito.when(
                placeSeanceRepository.countBySeanceIdSeance(1))
        .thenReturn(0L);

        double tauxDeRemplissage = frequentationService.getTauxDeRemplissage(1);

        assertEquals(0.0, tauxDeRemplissage);
    }

    @Test
    void shouldCalculateTauxRemplissageDuneSeance(){

        PlaceSeanceRepository placeSeanceRepository = Mockito.mock(PlaceSeanceRepository.class);

        FrequentationServiceImpl frequentationService = new FrequentationServiceImpl(placeSeanceRepository);

        long nombrePlaceTotal = 100L;
        long nombrePlacesConfirmees = 75L;

        Mockito.when(placeSeanceRepository.countBySeanceIdSeance(1))
                .thenReturn(nombrePlaceTotal);

        Mockito.when(placeSeanceRepository.countBySeanceIdSeanceAndReservationStatut(1, StatutReservation.CONFIRMEE))
                .thenReturn(nombrePlacesConfirmees);

        double tauxRemplissageDuneSeance = frequentationService.getTauxDeRemplissage(1);

        assertEquals(75.0, tauxRemplissageDuneSeance);
    }

    @Test
    void shouldCalculateTauxRemplissageDunePeriode(){

        PlaceSeanceRepository placeSeanceRepository = Mockito.mock(PlaceSeanceRepository.class);

        FrequentationServiceImpl frequentationService = new FrequentationServiceImpl(placeSeanceRepository);

        LocalDateTime dateDebut = LocalDateTime.of(2026, 10, 1, 0, 0);
        LocalDateTime dateFin = LocalDateTime.of(2026, 10, 31, 0, 0);

        long nombrePlacesTotalPeriode = 200L;
        long nombrePlacesConfirmees = 150L;

        Mockito.when(
                placeSeanceRepository.countBySeanceDateHeureBetween(dateDebut, dateFin))
                        .thenReturn(nombrePlacesTotalPeriode);

        Mockito.when(
                placeSeanceRepository.countBySeanceDateHeureBetweenAndReservationStatut(
                        dateDebut,
                        dateFin,
                        StatutReservation.CONFIRMEE))
                        .thenReturn(nombrePlacesConfirmees);

        double tauxRemplissagePeriode =
                frequentationService.getTauxDeRemplissageParPeriode(
                        dateDebut,
                        dateFin
                );

        assertEquals(75.0, tauxRemplissagePeriode);
    }

    @Test
    void shouldReturnZeroWhenNoPlaceSeanceExistsForPeriode(){
        PlaceSeanceRepository placeSeanceRepository = Mockito.mock(PlaceSeanceRepository.class);

        FrequentationServiceImpl frequentationService = new FrequentationServiceImpl(placeSeanceRepository);

        LocalDateTime dateDebut = LocalDateTime.of(2026, 10, 1, 0, 0);
        LocalDateTime dateFin = LocalDateTime.of(2026, 10, 31, 0, 0);

        Mockito.when(
                placeSeanceRepository.countBySeanceDateHeureBetween(dateDebut, dateFin))
                .thenReturn(0L);

        double tauxRemplissagePeriode = frequentationService.getTauxDeRemplissageParPeriode(dateDebut, dateFin);

        assertEquals(0.0, tauxRemplissagePeriode);

    }
}
