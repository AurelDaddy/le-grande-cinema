package com.legrandecinema.le_grande_cinema.service;

import com.legrandecinema.le_grande_cinema.model.Paiement;

import java.util.List;

public interface PaiementService {

    Paiement effectuerPaiement(Paiement paiement);

    List<Paiement> getPaiementsByReservation(Integer idReservation);
}
