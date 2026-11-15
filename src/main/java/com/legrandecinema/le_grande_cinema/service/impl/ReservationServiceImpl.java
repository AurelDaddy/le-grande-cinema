package com.legrandecinema.le_grande_cinema.service.impl;

import com.legrandecinema.le_grande_cinema.exception.RessourceIntrouvableException;
import com.legrandecinema.le_grande_cinema.model.PlaceSeance;
import com.legrandecinema.le_grande_cinema.model.Reservation;
import com.legrandecinema.le_grande_cinema.model.Seance;
import com.legrandecinema.le_grande_cinema.model.Utilisateur;
import com.legrandecinema.le_grande_cinema.model.type.StatutReservation;
import com.legrandecinema.le_grande_cinema.repositories.PlaceSeanceRepository;
import com.legrandecinema.le_grande_cinema.repositories.ReservationRepository;
import com.legrandecinema.le_grande_cinema.repositories.SeanceRepository;
import com.legrandecinema.le_grande_cinema.repositories.UtilisateurRepository;
import com.legrandecinema.le_grande_cinema.service.ReservationExpirationService;
import com.legrandecinema.le_grande_cinema.service.ReservationService;
import com.legrandecinema.le_grande_cinema.service.policy.ReservationExpirationPolicy;
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
    private final ReservationExpirationService reservationExpirationService;

    public ReservationServiceImpl(
            ReservationRepository reservationRepository,
            SeanceRepository seanceRepository,
            UtilisateurRepository utilisateurRepository,
            PlaceSeanceRepository placeSeanceRepository,
            ReservationExpirationService reservationExpirationService
    ) {
        this.reservationRepository = reservationRepository;
        this.seanceRepository = seanceRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.placeSeanceRepository = placeSeanceRepository;
        this.reservationExpirationService = reservationExpirationService;
    }

    @Transactional
    @Override
    public Reservation createReservation(
            String email,
            Integer idSeance,
            int nombrePlaces) {

        //petite vérification que le nombre de places demandé est bien supérieur à 0
        if (nombrePlaces <= 0) {
            throw new IllegalArgumentException("Le nombre de places demandé doit être supérieur à 0");
        }


        // Verrouillage de la séance avant de rechercher les places disponibles.
        // Les réservations concurrentes pour cette séance devront attendre
        // la fin de la transaction en cours.
        Seance seance = seanceRepository
                .findByIdForUpdate(idSeance)
                .orElseThrow(() ->
                        new RessourceIntrouvableException(
                                "Séance introuvable"
                        )
                );

        // Récupération de l'utilisateur après acquisition du verrou.
        Utilisateur utilisateur = utilisateurRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RessourceIntrouvableException(
                                "Utilisateurs introuvable"
                        )
                );


        //verifier que la résservation ne se fasse pas sur une séance passée
        if (seance.getDateHeure().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("La séance est passée. Elle n'est donc plus réservable");
        }

        //libèration des places des réservations expirées avant
        //de rechercher les places encore disponibles
        reservationExpirationService.expirerReservationsDeLaSeance(idSeance);

        //vérifie que qu'il reste des placesSeances avec Reservation Null donc des places disponibles pour la Séance
        List<PlaceSeance> placesAReserver = placeSeanceRepository.findBySeanceIdSeanceAndReservationIsNull(
                idSeance,
                PageRequest.of(0, nombrePlaces));

        //vérifie qu'il y ait assez de places disponibles
        if (placesAReserver.size() < nombrePlaces) {
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

    @Override
    public List<Reservation> getReservationsBySeance(Integer idSeance) {
        return reservationRepository.findBySeanceIdSeance(idSeance);
    }

}
