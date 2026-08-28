package com.bank.fraud;

import java.io.IOException;

public class IndividualRiskCalculator implements RiskCalculator {
    private final GroqClient groqClient;

    public IndividualRiskCalculator(GroqClient groqClient) {
        this.groqClient = groqClient;
    }

    @Override
    public int calculateRisk(Transaction transaction) {
        String prompt = String.format("Given a transaction of $%.2f at %s store in %s city, return only a number from 0 to 100 representing fraud risk.",
                transaction.amount(), transaction.merchant(), transaction.location());
        try {
            return parseRisk(groqClient.ask(prompt));
        } catch (IOException | InterruptedException | IllegalArgumentException | IllegalStateException exception) {
            System.err.println("Risk analysis failed: " + exception.getMessage());
            return 50;
        }
    }

    static int parseRisk(String response) {
        int risk = Integer.parseInt(response.trim());
        if (risk < 0 || risk > 100) {
            throw new IllegalArgumentException("Risk must be between 0 and 100.");
        }
        return risk;
    }
}
