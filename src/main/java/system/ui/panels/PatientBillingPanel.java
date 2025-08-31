/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package system.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import system.enums.ClaimStatus;
import system.model.Claim;
import system.patterns.chain.CardPaymentHandler;
import system.patterns.chain.ClaimHandler;
import system.patterns.chain.PatientBillingHandler;
import system.service.ClaimService;

/**
 *
 * @author User
 */
public class PatientBillingPanel extends javax.swing.JPanel {

    private final ClaimService claimService;
    private final Map<JPanel, Claim> claimCardMap;

    // --- UI Components ---
    private JPanel claimsContainer;
    private JLabel patientNameLabel;
    private JLabel claimIdLabel;
    private JLabel totalAmountLabel;
    private JLabel insurancePaidLabel;
    private JLabel patientDueLabel;
    private JButton processPaymentButton;
    private JButton refreshButton;
    private Claim selectedClaim;

    public PatientBillingPanel() {
        this.claimService = new ClaimService();
        this.claimCardMap = new HashMap<>();

        initComponentsManual();

        processPaymentButton.addActionListener(e -> onProcessPayment());
        refreshButton.addActionListener(e -> loadPendingClaims());

        loadPendingClaims();
    }
    
    private void loadPendingClaims() {
        claimsContainer.removeAll();
        claimCardMap.clear();
        clearDetailsPanel();
        processPaymentButton.setEnabled(false);

        List<Claim> pendingClaims = claimService.findClaimsByStatus(ClaimStatus.PENDING_PATIENT_BILLING);
        
        if (pendingClaims.isEmpty()) {
            JLabel noClaimsLabel = new JLabel("No claims are currently pending patient payment.");
            noClaimsLabel.setHorizontalAlignment(SwingConstants.CENTER);
            noClaimsLabel.setFont(new Font("Inter", Font.ITALIC, 14));
            noClaimsLabel.setForeground(Color.GRAY);
            claimsContainer.add(noClaimsLabel);
        } else {
            claimsContainer.setLayout(new BoxLayout(claimsContainer, BoxLayout.Y_AXIS));
            for (Claim claim : pendingClaims) {
                JPanel claimCard = createClaimCard(claim);
                claimsContainer.add(claimCard);
                claimCardMap.put(claimCard, claim);
            }
        }

        claimsContainer.revalidate();
        claimsContainer.repaint();
    }

    private JPanel createClaimCard(Claim claim) {
        JPanel card = new JPanel();
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(0, 80));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        GridBagConstraints gbc = new GridBagConstraints();

        // Patient Name
        JLabel nameLabel = new JLabel(claim.getAppointment().getPatient().getName());
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD, 14f));
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 3, 30); // Added 30px right margin for gap
        gbc.weightx = 1.0; // Allow left side to expand
        card.add(nameLabel, gbc);

        // Claim ID
        JLabel idLabel = new JLabel("Claim #" + claim.getId());
        idLabel.setFont(idLabel.getFont().deriveFont(11f));
        idLabel.setForeground(Color.GRAY);
        gbc.gridy = 1;
        card.add(idLabel, gbc);

        // Amount Due
        JLabel amountLabel = new JLabel("Rs. " + String.format("%.2f", claim.getPatientDueAmount()));
        amountLabel.setFont(amountLabel.getFont().deriveFont(Font.BOLD, 16f));
        gbc.gridx = 1; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weightx = 0; // Don't expand right side
        card.add(amountLabel, gbc);

        // Status
        JLabel statusLabel = new JLabel("● PENDING");
        statusLabel.setFont(statusLabel.getFont().deriveFont(10f));
        statusLabel.setForeground(new Color(255, 140, 0)); // Orange
        gbc.gridy = 1;
        card.add(statusLabel, gbc);

        // Add click listener
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selectClaimCard(card, claim);
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (selectedClaim != claim) {
                    card.setBackground(new Color(245, 245, 245));
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (selectedClaim != claim) {
                    card.setBackground(Color.WHITE);
                }
            }
        });

        return card;
    }

    private void selectClaimCard(JPanel selectedCard, Claim claim) {
        // Reset all cards
        for (JPanel card : claimCardMap.keySet()) {
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
            ));
        }

        // Highlight selected card
        selectedCard.setBackground(new Color(230, 240, 255));
        selectedCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180)),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        this.selectedClaim = claim;
        updateDetailsPanel(claim);
        processPaymentButton.setEnabled(true);
    }
    
    private void updateDetailsPanel(Claim claim) {
        patientNameLabel.setText(claim.getAppointment().getPatient().getName());
        claimIdLabel.setText("Claim #" + claim.getId());
        totalAmountLabel.setText("Rs. " + String.format("%.2f", claim.getTotalAmount()));
        insurancePaidLabel.setText("Rs. " + String.format("%.2f", claim.getPaidByInsurance()));
        patientDueLabel.setText("Rs. " + String.format("%.2f", claim.getPatientDueAmount()));
    }

    private void clearDetailsPanel() {
        patientNameLabel.setText("Select a claim to view details");
        claimIdLabel.setText("");
        totalAmountLabel.setText("--");
        insurancePaidLabel.setText("--");
        patientDueLabel.setText("--");
    }

    private void onProcessPayment() {
        if (selectedClaim == null) return;

        ClaimHandler billingHandler = new PatientBillingHandler();
        ClaimHandler cardHandler = new CardPaymentHandler();

        billingHandler.setNextHandler(cardHandler);
        billingHandler.processClaim(selectedClaim);

        loadPendingClaims();
    }
    
    private void initComponentsManual() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Simple Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel titleLabel = new JLabel("Patient Billing");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 20f));

        refreshButton = new JButton("Refresh");

        headerPanel.add(titleLabel);
        headerPanel.add(javax.swing.Box.createHorizontalStrut(20));
        headerPanel.add(refreshButton);

        add(headerPanel, BorderLayout.NORTH);

        // Main Content Panel
        JSplitPane mainSplitPane = new JSplitPane();
        mainSplitPane.setDividerLocation(400);
        add(mainSplitPane, BorderLayout.CENTER);

        // Left Side: Claims List
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));

        JLabel claimsTitle = new JLabel("Pending Claims");
        claimsTitle.setFont(claimsTitle.getFont().deriveFont(Font.BOLD));
        leftPanel.add(claimsTitle, BorderLayout.NORTH);

        claimsContainer = new JPanel();
        claimsContainer.setBackground(Color.WHITE);
        JScrollPane claimsScrollPane = new JScrollPane(claimsContainer);
        leftPanel.add(claimsScrollPane, BorderLayout.CENTER);

        mainSplitPane.setLeftComponent(leftPanel);

        // Right Side: Details Panel
        JPanel detailsPanel = createDetailsPanel();
        mainSplitPane.setRightComponent(detailsPanel);
    }

    private JPanel createDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Billing Details"));

        // Details Content
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Patient Name
        addDetailRow(contentPanel, gbc, 0, "Patient:",
            patientNameLabel = new JLabel("Select a claim"));

        // Claim ID
        addDetailRow(contentPanel, gbc, 1, "Claim ID:",
            claimIdLabel = new JLabel(""));

        // Financial Details
        addDetailRow(contentPanel, gbc, 2, "Total Amount:",
            totalAmountLabel = new JLabel("--"));

        addDetailRow(contentPanel, gbc, 3, "Insurance Paid:",
            insurancePaidLabel = new JLabel("--"));

        // Patient Due
        addDetailRow(contentPanel, gbc, 4, "Patient Due:",
            patientDueLabel = new JLabel("--"));

        panel.add(contentPanel, BorderLayout.CENTER);

        // Action Button
        processPaymentButton = new JButton("Process Payment");
        processPaymentButton.setEnabled(false);
        panel.add(processPaymentButton, BorderLayout.SOUTH);

        return panel;
    }

    private void addDetailRow(JPanel parent, GridBagConstraints gbc, int row, String label, JLabel valueLabel) {
        gbc.gridx = 0; gbc.gridy = row;
        gbc.insets = new Insets(5, 5, 5, 20); // Added horizontal gap of 20px between label and value
        JLabel titleLabel = new JLabel(label);
        parent.add(titleLabel, gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(5, 0, 5, 5); // Reset insets for value label
        parent.add(valueLabel, gbc);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 814, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 468, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
