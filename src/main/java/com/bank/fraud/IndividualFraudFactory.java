package com.bank.fraud;

public class IndividualFraudFactory implements FraudAnalysisFactory {
    private final GroqClient groqClient;

    public IndividualFraudFactory(GroqClient groqClient) {
        this.groqClient = groqClient;
    }

    @Override
    public RiskCalculator createRiskCalculator() {
        return new IndividualRiskCalculator(groqClient);
    }

    @Override
    public LocationClassifier createLocationClassifier() {
        return new IndividualLocationClassifier(groqClient);
    }

    @Override
    public AlertGenerator createAlertGenerator() {
        return new SmsAlertGenerator(groqClient);
    }
}
