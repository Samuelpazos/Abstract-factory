package com.bank.fraud;

/** Abstract Factory for a compatible family of fraud-analysis products. */
public interface FraudAnalysisFactory {
    RiskCalculator createRiskCalculator();

    LocationClassifier createLocationClassifier();

    AlertGenerator createAlertGenerator();
}
