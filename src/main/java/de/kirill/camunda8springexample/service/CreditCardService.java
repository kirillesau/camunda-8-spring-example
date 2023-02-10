package de.kirill.camunda8springexample.service;

import org.springframework.stereotype.Service;

@Service
public class CreditCardService {

    public void chargeCreditCard() {
        System.out.println("Charging Credit Card");
    }

}
