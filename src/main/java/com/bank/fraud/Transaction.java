package com.bank.fraud;

public record Transaction(double amount, String merchant, String location, String customerType) {
}
