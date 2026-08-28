package com.bank.fraud;

import java.util.Locale;
import java.util.Scanner;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("--console")) {
            runConsole();
            return;
        }
        SwingUtilities.invokeLater(() -> new FraudAnalysisFrame(new GroqClient()).setVisible(true));
    }

    private static void runConsole() {
        Scanner scanner = new Scanner(System.in);
        GroqClient groqClient = new GroqClient();
        System.out.println("=== Banking Fraud Analysis ===");

        while (true) {
            String customerType = readCustomerType(scanner);
            if (customerType == null) {
                break;
            }

            // Abstract Factory selects a complete, compatible product family.
            FraudAnalysisFactory factory = customerType.equals("individual")
                    ? new IndividualFraudFactory(groqClient)
                    : new BusinessFraudFactory(groqClient);

            Transaction transaction = readTransaction(scanner, customerType);
            RiskCalculator riskCalculator = factory.createRiskCalculator();
            LocationClassifier locationClassifier = factory.createLocationClassifier();
            AlertGenerator alertGenerator = factory.createAlertGenerator();

            int riskScore = riskCalculator.calculateRisk(transaction);
            String locationType = locationClassifier.classifyLocation(transaction);
            alertGenerator.sendAlert(transaction, riskScore);

            System.out.printf(Locale.US, "%nRisk score: %d/100%nLocation: %s%n%n", riskScore, locationType);
            System.out.print("Analyze another transaction? (yes/no): ");
            if (!scanner.nextLine().trim().equalsIgnoreCase("yes")) {
                break;
            }
        }
        System.out.println("Goodbye.");
    }

    private static String readCustomerType(Scanner scanner) {
        while (true) {
            System.out.print("Client type (individual/business, or quit): ");
            String value = scanner.nextLine().trim().toLowerCase(Locale.ROOT);
            if (value.equals("quit") || value.equals("exit")) {
                return null;
            }
            if (value.equals("individual") || value.equals("business")) {
                return value;
            }
            System.out.println("Please enter individual or business.");
        }
    }

    private static Transaction readTransaction(Scanner scanner, String customerType) {
        double amount;
        while (true) {
            System.out.print("Transaction amount: ");
            try {
                amount = Double.parseDouble(scanner.nextLine().trim());
                if (amount >= 0) {
                    break;
                }
            } catch (NumberFormatException ignored) {
                // Ask again for malformed numeric input.
            }
            System.out.println("Enter a non-negative number.");
        }
        System.out.print("Merchant: ");
        String merchant = scanner.nextLine().trim();
        System.out.print("Location/city: ");
        String location = scanner.nextLine().trim();
        return new Transaction(amount, merchant, location, customerType);
    }
}
