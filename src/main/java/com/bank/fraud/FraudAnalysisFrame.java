package com.bank.fraud;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class FraudAnalysisFrame extends JFrame {
    private static final Color NAVY = new Color(17, 31, 52);
    private static final Color BLUE = new Color(41, 107, 192);
    private static final Color PALE_BLUE = new Color(235, 244, 255);
    private static final Color BORDER = new Color(213, 221, 232);
    private static final Color TEXT = new Color(35, 47, 62);
    private static final int HIGH_RISK_THRESHOLD = 70;

    private final GroqClient groqClient;
    private final JComboBox<String> clientType = new JComboBox<>(new String[]{"Individual client", "Business client"});
    private final JTextField amountField = new JTextField();
    private final JTextField merchantField = new JTextField();
    private final JTextField locationField = new JTextField();
    private final JButton analyzeButton = new JButton("Analyze transaction");
    private final JProgressBar progressBar = new JProgressBar();
    private final JLabel statusLabel = new JLabel("Ready for a new transaction");
    private final JLabel riskValue = new JLabel("--");
    private final JLabel riskCaption = new JLabel("Waiting for analysis");
    private final JLabel locationValue = new JLabel("--");
    private final JLabel alertValue = new JLabel("--");
    private final JTextArea detailsArea = new JTextArea();

    public FraudAnalysisFrame(GroqClient groqClient) {
        this.groqClient = groqClient;
        setTitle("FraudGuard | Banking fraud analysis");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(800, 620));
        setSize(900, 680);
        setLocationRelativeTo(null);
        buildUi();
    }

    private void buildUi() {
        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(new Color(247, 249, 252));
        root.setBorder(BorderFactory.createEmptyBorder(26, 32, 26, 32));
        root.add(createHeader(), BorderLayout.NORTH);

        JPanel content = new JPanel(new GridLayout(1, 2, 20, 0));
        content.setOpaque(false);
        content.add(createTransactionPanel());
        content.add(createResultsPanel());
        root.add(content, BorderLayout.CENTER);

        JPanel footer = new JPanel(new BorderLayout(12, 0));
        footer.setOpaque(false);
        statusLabel.setForeground(new Color(90, 105, 123));
        footer.add(statusLabel, BorderLayout.WEST);
        progressBar.setVisible(false);
        progressBar.setIndeterminate(true);
        footer.add(progressBar, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);
        setContentPane(root);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("FraudGuard");
        title.setFont(new Font("SansSerif", Font.BOLD, 30));
        title.setForeground(NAVY);
        JLabel subtitle = new JLabel("AI-powered transaction intelligence");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(new Color(90, 105, 123));
        JPanel copy = new JPanel(new GridLayout(2, 1, 0, 3));
        copy.setOpaque(false);
        copy.add(title);
        copy.add(subtitle);
        header.add(copy, BorderLayout.WEST);
        JLabel badge = new JLabel("GROQ AI  /  LIVE");
        badge.setOpaque(true);
        badge.setBackground(new Color(220, 242, 232));
        badge.setForeground(new Color(25, 110, 63));
        badge.setBorder(BorderFactory.createEmptyBorder(7, 12, 7, 12));
        header.add(badge, BorderLayout.EAST);
        return header;
    }

    private JPanel createTransactionPanel() {
        JPanel panel = cardPanel();
        panel.setLayout(new BorderLayout(0, 20));
        panel.add(sectionTitle("Transaction details", "Select the client family and enter the purchase data."), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(0, 0, 16, 0);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        constraints.gridx = 0;
        constraints.gridy = 0;
        form.add(fieldLabel("Client family"), constraints);
        constraints.gridy++;
        form.add(clientType, constraints);
        constraints.gridy++;
        form.add(fieldLabel("Amount (USD)"), constraints);
        constraints.gridy++;
        form.add(amountField, constraints);
        constraints.gridy++;
        form.add(fieldLabel("Merchant"), constraints);
        constraints.gridy++;
        form.add(merchantField, constraints);
        constraints.gridy++;
        form.add(fieldLabel("Location / city"), constraints);
        constraints.gridy++;
        form.add(locationField, constraints);
        styleInput(clientType);
        styleInput(amountField);
        styleInput(merchantField);
        styleInput(locationField);
        panel.add(form, BorderLayout.CENTER);

        analyzeButton.setForeground(Color.WHITE);
        analyzeButton.setBackground(BLUE);
        analyzeButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        analyzeButton.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        analyzeButton.setFocusPainted(false);
        analyzeButton.addActionListener(event -> analyzeTransaction());
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        buttonRow.setOpaque(false);
        buttonRow.add(analyzeButton);
        panel.add(buttonRow, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createResultsPanel() {
        JPanel panel = cardPanel();
        panel.setLayout(new BorderLayout(0, 16));
        panel.add(sectionTitle("Analysis result", "The selected product family is used for every analysis."), BorderLayout.NORTH);

        JPanel metrics = new JPanel(new GridLayout(1, 3, 10, 0));
        metrics.setOpaque(false);
        metrics.add(metricCard("RISK SCORE", riskValue, riskCaption));
        metrics.add(metricCard("LOCATION", locationValue, new JLabel("AI classification")));
        metrics.add(metricCard("ALERT", alertValue, new JLabel("Delivery channel")));
        panel.add(metrics, BorderLayout.CENTER);

        detailsArea.setEditable(false);
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        detailsArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        detailsArea.setForeground(TEXT);
        detailsArea.setBackground(PALE_BLUE);
        detailsArea.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        panel.add(new JScrollPane(detailsArea), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel metricCard(String title, JLabel value, JLabel caption) {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(PALE_BLUE);
        card.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        titleLabel.setForeground(new Color(82, 105, 132));
        value.setFont(new Font("SansSerif", Font.BOLD, 22));
        value.setForeground(NAVY);
        caption.setFont(new Font("SansSerif", Font.PLAIN, 11));
        caption.setForeground(new Color(90, 105, 123));
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        card.add(caption, BorderLayout.SOUTH);
        return card;
    }

    private JPanel cardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(22, 22, 22, 22)));
        return panel;
    }

    private JPanel sectionTitle(String title, String subtitle) {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 4));
        panel.setOpaque(false);
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(NAVY);
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(90, 105, 123));
        panel.add(titleLabel);
        panel.add(subtitleLabel);
        return panel;
    }

    private JLabel fieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 12));
        label.setForeground(TEXT);
        return label;
    }

    private void styleInput(javax.swing.JComponent component) {
        component.setFont(new Font("SansSerif", Font.PLAIN, 14));
        component.setBackground(Color.WHITE);
        component.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
    }

    private void analyzeTransaction() {
        double amount;
        try {
            amount = Double.parseDouble(amountField.getText().trim());
            if (amount < 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException exception) {
            showValidationError("Enter a valid non-negative amount.");
            return;
        }
        String merchant = merchantField.getText().trim();
        String location = locationField.getText().trim();
        if (merchant.isBlank() || location.isBlank()) {
            showValidationError("Merchant and location are required.");
            return;
        }

        String customerType = clientType.getSelectedIndex() == 0 ? "individual" : "business";
        Transaction transaction = new Transaction(amount, merchant, location, customerType);
        setBusy(true);
        new SwingWorker<AnalysisResult, Void>() {
            @Override
            protected AnalysisResult doInBackground() {
                // The selected Abstract Factory creates the complete product family.
                FraudAnalysisFactory factory = customerType.equals("individual")
                        ? new IndividualFraudFactory(groqClient)
                        : new BusinessFraudFactory(groqClient);
                int risk = factory.createRiskCalculator().calculateRisk(transaction);
                String classification = factory.createLocationClassifier().classifyLocation(transaction);
                factory.createAlertGenerator().sendAlert(transaction, risk);
                return new AnalysisResult(risk, classification, customerType);
            }

            @Override
            protected void done() {
                try {
                    showResult(get());
                } catch (InterruptedException | ExecutionException exception) {
                    detailsArea.setText("The analysis could not be completed: " + exception.getMessage());
                    statusLabel.setText("Analysis failed");
                } finally {
                    setBusy(false);
                }
            }
        }.execute();
    }

    private void showResult(AnalysisResult result) {
        riskValue.setText(result.risk() + "/100");
        riskCaption.setText(result.risk() >= HIGH_RISK_THRESHOLD ? "High risk detected" : "Below alert threshold");
        riskValue.setForeground(result.risk() >= HIGH_RISK_THRESHOLD ? new Color(190, 65, 58) : new Color(25, 110, 63));
        locationValue.setText(result.location());
        String channel = result.customerType().equals("individual") ? "SMS" : "EMAIL";
        alertValue.setText(result.risk() >= HIGH_RISK_THRESHOLD ? channel + " sent" : "Not required");
        detailsArea.setText(String.format(Locale.US,
                "Analysis complete for a %s client. Groq generated the risk score and location classification. "
                        + "The %s alert product was %s because the risk threshold is %d/100.",
                result.customerType(), channel, result.risk() >= HIGH_RISK_THRESHOLD ? "activated" : "not activated", HIGH_RISK_THRESHOLD));
        statusLabel.setText("Analysis complete");
    }

    private void setBusy(boolean busy) {
        analyzeButton.setEnabled(!busy);
        clientType.setEnabled(!busy);
        amountField.setEnabled(!busy);
        merchantField.setEnabled(!busy);
        locationField.setEnabled(!busy);
        progressBar.setVisible(busy);
        statusLabel.setText(busy ? "Contacting Groq AI..." : statusLabel.getText());
    }

    private void showValidationError(String message) {
        JOptionPane.showMessageDialog(this, message, "Check transaction", JOptionPane.WARNING_MESSAGE);
    }

    private record AnalysisResult(int risk, String location, String customerType) {
    }
}
