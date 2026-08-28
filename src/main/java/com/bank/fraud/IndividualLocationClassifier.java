package com.bank.fraud;

import java.io.IOException;

public class IndividualLocationClassifier implements LocationClassifier {
    private final GroqClient groqClient;

    public IndividualLocationClassifier(GroqClient groqClient) {
        this.groqClient = groqClient;
    }

    @Override
    public String classifyLocation(Transaction transaction) {
        return classify(groqClient, transaction);
    }

    static String classify(GroqClient groqClient, Transaction transaction) {
        String prompt = String.format("Given the location %s, return only one word: LOCAL, NATIONAL, or INTERNATIONAL.", transaction.location());
        try {
            String result = groqClient.ask(prompt).trim().toUpperCase();
            if (!result.equals("LOCAL") && !result.equals("NATIONAL") && !result.equals("INTERNATIONAL")) {
                throw new IllegalArgumentException("Unexpected location classification.");
            }
            return result;
        } catch (IOException | InterruptedException | IllegalArgumentException | IllegalStateException exception) {
            System.err.println("Location analysis failed: " + exception.getMessage());
            return "NATIONAL";
        }
    }
}
