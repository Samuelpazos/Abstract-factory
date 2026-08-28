package com.bank.fraud;

public interface AlertGenerator {
    void sendAlert(Transaction transaction, int riskScore);
}
