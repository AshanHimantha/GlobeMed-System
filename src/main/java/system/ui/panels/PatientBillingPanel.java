/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package system.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
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
    private final DefaultListModel<String> listModel;
    private final Map<String, Claim> claimMap;

    // --- UI Components ---
    private JList<String> claimList;
    private JTextArea detailsTextArea;
    private JButton processPaymentButton;
    private JButton refreshButton;

    public PatientBillingPanel() {
        this.claimService = new ClaimService();
        this.listModel = new DefaultListModel<>();
        this.claimMap = new HashMap<>();

        initComponentsManual(); // Build the UI

        // Add event listeners
        claimList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onClaimSelected();
        });
        processPaymentButton.addActionListener(e -> onProcessPayment());
        refreshButton.addActionListener(e -> loadPendingClaims());

        loadPendingClaims();
    }
    
    private void loadPendingClaims() {
        listModel.clear();
        claimMap.clear();
        detailsTextArea.setText("Please select a claim from the list to process final payment.");
        processPaymentButton.setEnabled(false);

        // Fetch claims that are waiting for this final step
        List<Claim> pendingClaims = claimService.findClaimsByStatus(ClaimStatus.PENDING_PATIENT_BILLING);
        
        if (pendingClaims.isEmpty()) {
            listModel.addElement("No claims are currently pending patient payment.");
            claimList.setEnabled(false);
        } else {
            claimList.setEnabled(true);
            for (Claim claim : pendingClaims) {
                String displayText = String.format("Claim #%d - %s (Due: $%.2f)",
                    claim.getId(),
                    claim.getAppointment().getPatient().getName(),
                    claim.getPatientDueAmount());
                listModel.addElement(displayText);
                claimMap.put(displayText, claim);
            }
        }
    }

    private void onClaimSelected() {
        String selectedValue = claimList.getSelectedValue();
        if (selectedValue == null || !claimMap.containsKey(selectedValue)) {
            processPaymentButton.setEnabled(false);
            return;
        }

        Claim claim = claimMap.get(selectedValue);
        detailsTextArea.setText("");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

        detailsTextArea.append("FINAL BILLING DETAILS\n");
        detailsTextArea.append("---------------------------------\n");
        detailsTextArea.append(String.format("%-20s %s\n", "Claim ID:", claim.getId()));
        detailsTextArea.append(String.format("%-20s %s\n", "Patient:", claim.getAppointment().getPatient().getName()));
        detailsTextArea.append(String.format("%-20s %s\n", "Total Amount:", currencyFormat.format(claim.getTotalAmount())));
        detailsTextArea.append(String.format("%-20s %s\n", "Paid by Insurance:", currencyFormat.format(claim.getPaidByInsurance())));
        detailsTextArea.append("---------------------------------\n");
        detailsTextArea.append(String.format("%-20s %s\n", "AMOUNT DUE:", currencyFormat.format(claim.getPatientDueAmount())));

        processPaymentButton.setEnabled(true);
    }
    
    private void onProcessPayment() {
        String selectedValue = claimList.getSelectedValue();
        if (selectedValue == null) return;
        
        Claim claimToProcess = claimMap.get(selectedValue);

       ClaimHandler billingHandler = new PatientBillingHandler();
        ClaimHandler cardHandler = new CardPaymentHandler(); // The specialized card processor

        // 2. Build the chain. The card handler comes AFTER the billing handler.
        billingHandler.setNextHandler(cardHandler);

        // 3. Start the process by giving the claim to the FIRST handler in this sub-chain.
        billingHandler.processClaim(claimToProcess);
        
        // 4. Refresh the list. The claim should now be gone from this queue.
        loadPendingClaims();
    }
    
    private void initComponentsManual() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel("Patient Final Billing");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 24));
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Main Split Pane
        JSplitPane mainSplitPane = new JSplitPane();
        mainSplitPane.setDividerLocation(350);
        add(mainSplitPane, BorderLayout.CENTER);

        // Left Side: Claims List
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        claimList = new JList<>(listModel);
        JScrollPane listScrollPane = new JScrollPane(claimList);
        listScrollPane.setBorder(BorderFactory.createTitledBorder("Claims Pending Patient Payment"));
        refreshButton = new JButton("Refresh List");
        leftPanel.add(listScrollPane, BorderLayout.CENTER);
        leftPanel.add(refreshButton, BorderLayout.SOUTH);
        mainSplitPane.setLeftComponent(leftPanel);

        // Right Side: Details and Actions
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        detailsTextArea = new JTextArea("Please select a claim from the list.");
        detailsTextArea.setEditable(false);
        detailsTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        rightPanel.add(new JScrollPane(detailsTextArea), BorderLayout.CENTER);

        processPaymentButton = new JButton("Process Final Patient Payment");
        processPaymentButton.setFont(new Font("Inter", Font.BOLD, 14));
        processPaymentButton.setBackground(new Color(40, 167, 69)); // Green
        processPaymentButton.setForeground(Color.WHITE);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(processPaymentButton);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainSplitPane.setRightComponent(rightPanel);
        
        setButtonsEnabled(false);
    }
    
    private void setButtonsEnabled(boolean enabled){
        processPaymentButton.setEnabled(enabled);
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
