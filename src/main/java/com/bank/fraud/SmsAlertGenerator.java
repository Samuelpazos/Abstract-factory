package com.bank.fraud;

import java.io.IOException;

public class SmsAlertGenerator implements AlertGenerator {
    private static final int HIGH_RISK_THRESHOLD = 70;
    private final GroqClient groqClient;

    public SmsAlertGenerator(GroqClient groqClient) {
        this.groqClient = groqClient;
    }

    @Override
    public void sendAlert(Transaction transaction, int riskScore) {
        if (riskScore < HIGH_RISK_THRESHOLD) {
            System.out.println("SMS not sent: risk is below the alert threshold.");
            return;
        }
        send(groqClient, transaction, "SMS");
    }

    static void send(GroqClient groqClient, Transaction transaction, String channel) {
        String prompt = String.format("Write a short fraud alert message for a transaction of $%.2f at %s.", transaction.amount(), transaction.merchant());
        try {
            System.out.println(channel + " alert: " + groqClient.ask(prompt));
        } catch (IOException | InterruptedException | IllegalArgumentException | IllegalStateException exception) {
            System.err.println(channel + " alert failed: " + exception.getMessage());
        }
    }
}
