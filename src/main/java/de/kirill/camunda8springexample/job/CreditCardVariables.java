package de.kirill.camunda8springexample.job;

public record CreditCardVariables(String reference, double amount, String cardNumber, String cardExpiry,
                                  String cardCVC) {
}
