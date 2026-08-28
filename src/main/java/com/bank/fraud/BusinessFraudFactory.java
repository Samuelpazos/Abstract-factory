package com.bank.fraud;

public class BusinessFraudFactory implements FraudAnalysisFactory {
    private final GroqClient groqClient;

    public BusinessFraudFactory(GroqClient groqClient) {
        this.groqClient = groqClient;
    }

    @Override
    public RiskCalculator createRiskCalculator() {
        return new BusinessRiskCalculator(groqClient);
    }

    @Override
    public LocationClassifier createLocationClassifier() {
        return new BusinessLocationClassifier(groqClient);
    }

    @Override
    public AlertGenerator createAlertGenerator() {
        return new EmailAlertGenerator(groqClient);
    }
}
