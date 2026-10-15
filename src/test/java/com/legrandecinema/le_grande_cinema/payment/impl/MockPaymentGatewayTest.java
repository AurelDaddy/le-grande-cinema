package com.legrandecinema.le_grande_cinema.payment.impl;

import com.legrandecinema.le_grande_cinema.model.type.StatutPaiement;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MockPaymentGatewayTest {

    @Test
    void shouldAcceptPositiveAmount() {
        MockPaymentGateway mockPaymentGateway = new MockPaymentGateway();
        StatutPaiement statutPaiement = mockPaymentGateway.payer(new BigDecimal("10.00"));

        assertEquals(StatutPaiement.ACCEPTE, statutPaiement);
    }

    @Test
    void shouldRefuseNegativeAmount() {
        MockPaymentGateway mockPaymentGateway = new MockPaymentGateway();
        StatutPaiement statutPaiement = mockPaymentGateway.payer(new BigDecimal("-10.00"));

        assertEquals(StatutPaiement.REFUSE, statutPaiement);
    }

    @Test
    void shouldRefuseNullAmount(){
        MockPaymentGateway mockPaymentGateway = new MockPaymentGateway();
        StatutPaiement statutPaiement = mockPaymentGateway.payer(null);

        assertEquals(StatutPaiement.REFUSE, statutPaiement);
    }

    @Test
    void shouldRefuseZeroAmount(){
        MockPaymentGateway mockPaymentGateway = new MockPaymentGateway();
        StatutPaiement statutPaiement = mockPaymentGateway.payer(BigDecimal.ZERO);

        assertEquals(StatutPaiement.REFUSE, statutPaiement);
    }
}
