package com.legrandecinema.le_grande_cinema.mapper;

import com.legrandecinema.le_grande_cinema.dto.PaiementDto;
import com.legrandecinema.le_grande_cinema.model.Paiement;

public class PaiementMapper {

    private PaiementMapper() {}

    public static PaiementDto toDto(Paiement paiement) {
        return new PaiementDto(
                paiement.getIdPaiement(),
                paiement.getMontant(),
                paiement.getDatePaiement(),
                paiement.getStatut(),
                paiement.getReferencePaiement(),
                paiement.getReservation().getIdReservation()
        );

    }

}


