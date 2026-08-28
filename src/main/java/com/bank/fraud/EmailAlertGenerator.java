package com.bank.fraud;

public class EmailAlertGenerator implements AlertGenerator {
    private static final int HIGH_RISK_THRESHOLD = 70;
    private final GroqClient groqClient;

    public EmailAlertGenerator(GroqClient groqClient) {
        this.groqClient = groqClient;
    }

    @Override
    public void sendAlert(Transaction transaction, int riskScore) {
        if (riskScore < HIGH_RISK_THRESHOLD) {
            System.out.println("Email not sent: risk is below the alert threshold.");
            return;
        }
        SmsAlertGenerator.send(groqClient, transaction, "Email");
    }
}
