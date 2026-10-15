package com.legrandecinema.le_grande_cinema.payment;

import com.legrandecinema.le_grande_cinema.model.type.StatutPaiement;

import java.math.BigDecimal;

//C'est le contrat avec le paiement externe
public interface PaymentGateway  {

    StatutPaiement payer(BigDecimal montant);
}
