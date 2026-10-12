package com.legrandecinema.le_grande_cinema.service;

import com.legrandecinema.le_grande_cinema.model.Paiement;

import java.math.BigDecimal;
import java.util.List;

public interface PaiementService {

    Paiement effectuerPaiement(Integer idReservation, BigDecimal montant);

    List<Paiement> getPaiementsByReservation(Integer idReservation);
}
