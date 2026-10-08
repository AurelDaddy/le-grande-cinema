package com.legrandecinema.le_grande_cinema.payment.impl;

import com.legrandecinema.le_grande_cinema.model.type.StatutPaiement;
import com.legrandecinema.le_grande_cinema.payment.PaymentGateway;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

//@Component dit à Spring de créer automatiquement une instance de MockPaymentGateway
// afin qu’on puisse ensuite l’injecter dans PaiementServiceImpl.
@Component
public class MockPaymentGateway implements PaymentGateway {

    @Override
    public StatutPaiement payer(BigDecimal montant) {
        if(montant == null || montant.compareTo(BigDecimal.ZERO) <= 0){
            return StatutPaiement.REFUSE;
        }
        return StatutPaiement.ACCEPTE;
    }
}
