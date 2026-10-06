package com.legrandecinema.le_grande_cinema.service.impl;

import com.legrandecinema.le_grande_cinema.model.PlaceSeance;
import com.legrandecinema.le_grande_cinema.model.Reservation;
import com.legrandecinema.le_grande_cinema.model.Seance;
import com.legrandecinema.le_grande_cinema.model.Utilisateur;
import com.legrandecinema.le_grande_cinema.model.type.StatutReservation;
import com.legrandecinema.le_grande_cinema.repositories.PlaceSeanceRepository;
import com.legrandecinema.le_grande_cinema.repositories.ReservationRepository;
import com.legrandecinema.le_grande_cinema.repositories.SeanceRepository;
import com.legrandecinema.le_grande_cinema.repositories.UtilisateurRepository;
import com.legrandecinema.le_grande_cinema.service.ReservationService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final SeanceRepository seanceRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PlaceSeanceRepository placeSeanceRepository;

    public ReservationServiceImpl(
            ReservationRepository reservationRepository,
            SeanceRepository seanceRepository,
            UtilisateurRepository utilisateurRepository,
            PlaceSeanceRepository placeSeanceRepository) {
        this.reservationRepository = reservationRepository;
        this.seanceRepository = seanceRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.placeSeanceRepository = placeSeanceRepository;
    }

    @Transactional
    @Override
    public Reservation createReservation(
            Integer idUtilisateur,
            Integer idSeance,
            int nombrePlaces) {

        //petite vérifiaction que le nombre de places demandé est bien supérieur à 0
        if (nombrePlaces <= 0){
            throw new IllegalArgumentException("Le nombre de places demandé doit être supérieur à 0");
        }

        //Récupération de l'utilisateur, de la séance et du nombre de place à réserver
        //disponible dans placeSeance
        Utilisateur utilisateur = utilisateurRepository
                .findById(idUtilisateur)
                .orElseThrow();

        Seance seance = seanceRepository
                .findById(idSeance)
                .orElseThrow();

        List<PlaceSeance> placesAReserver = placeSeanceRepository.findBySeanceIdSeanceAndReservationIsNull(
                idSeance,
                PageRequest.of(0, nombrePlaces));

        if (placesAReserver.size() <  nombrePlaces ){
            throw new IllegalStateException("Désolé il n'y pas assez de places disponibles");
        }

        //Création de la réservation
        Reservation reservation = new Reservation();

        reservation.setUtilisateur(utilisateur);
        reservation.setSeance(seance);
        reservation.setDateReservation(LocalDateTime.now());
        reservation.setStatut(StatutReservation.EN_ATTENTE);

        Reservation reservationEnregistree = reservationRepository.save(reservation);

        //Enregistrement de la reservationEnregistree dans le(s) PlaceSeance(s) reservées
        for (PlaceSeance placeSeance : placesAReserver) {
            placeSeance.setReservation(reservationEnregistree);
        }

        placeSeanceRepository.saveAll(placesAReserver);

        return reservationEnregistree;
    }

    @Override
    public Optional<Reservation> getReservationById(Integer id) {
        return reservationRepository.findById(id);
    }

    @Override
    public List<Reservation> getReservationsConfirmees() {
        return reservationRepository.findByStatut(StatutReservation.CONFIRMEE);
    }

}
