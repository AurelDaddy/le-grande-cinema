package com.legrandecinema.le_grande_cinema.dto;

import com.legrandecinema.le_grande_cinema.model.type.StatutPaiement;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaiementDto (
        Integer idPaiement,
        BigDecimal montant,
        LocalDateTime datePaiement,
        StatutPaiement statut,
        String referencePaiement,
        Integer idReservation
)

{}
