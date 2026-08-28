package com.bank.fraud;

public class BusinessLocationClassifier implements LocationClassifier {
    private final GroqClient groqClient;

    public BusinessLocationClassifier(GroqClient groqClient) {
        this.groqClient = groqClient;
    }

    @Override
    public String classifyLocation(Transaction transaction) {
        return IndividualLocationClassifier.classify(groqClient, transaction);
    }
}
