package de.kirill.camunda8springexample.service;

import de.kirill.camunda8springexample.job.CreditCardVariables;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CreditCardService {

    public void chargeCreditCard(CreditCardVariables creditCardVariables) {
        log.trace("Charging Credit Card: " + creditCardVariables.cardNumber());

        log.info("Credit Card Data: ");
        log.info("- reference: " + creditCardVariables.reference());
        log.info("- amount: " + creditCardVariables.amount());
        log.info("- cardExpiry: " + creditCardVariables.cardExpiry());
        log.info("- cardCVC: " + creditCardVariables.cardCVC());
    }

}
