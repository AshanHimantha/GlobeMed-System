/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package system.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
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
import system.patterns.chain.ClaimHandler;
import system.patterns.chain.InsuranceApprovalHandler;
import system.service.ClaimService;

/**
 *
 * @author User
 */
public class InsuranceApprovalPanel extends javax.swing.JPanel {

    private final ClaimService claimService;
    private final DefaultListModel<String> listModel;
    private final Map<String, Claim> claimMap;

    // --- UI Components ---
    private JList<String> claimList;
    private JTextArea detailsTextArea;
    private JButton approveButton;
    private JButton partialButton;
    private JButton denyButton;
    private JButton refreshButton;

    public InsuranceApprovalPanel() {
        this.claimService = new ClaimService();
        this.listModel = new DefaultListModel<>();
        this.claimMap = new HashMap<>();

        initComponentsManual(); // Build the UI

        // Add event listeners
        claimList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onClaimSelected();
        });
        refreshButton.addActionListener(e -> loadPendingClaims());
        
        // The buttons will trigger the SAME handler, but we can pass a hint
        // or let the handler itself show the options. Let's let the handler do it.
        ActionListener processAction = e -> onProcessClaim();
        approveButton.addActionListener(processAction);
        partialButton.addActionListener(processAction);
        denyButton.addActionListener(processAction);

        loadPendingClaims();
    }
    
    private void loadPendingClaims() {
        listModel.clear();
        claimMap.clear();
        detailsTextArea.setText("Please select a claim from the list to review.");
        setButtonsEnabled(false);

        // Fetch claims that are waiting for this specific step in the chain
        List<Claim> pendingClaims = claimService.findClaimsByStatus(ClaimStatus.PENDING_INSURANCE_APPROVAL);
        
        if (pendingClaims.isEmpty()) {
            listModel.addElement("No claims are currently pending insurance approval.");
            claimList.setEnabled(false);
        } else {
            claimList.setEnabled(true);
            for (Claim claim : pendingClaims) {
                String displayText = String.format("Claim #%d - %s (Total: $%.2f)",
                    claim.getId(),
                    claim.getAppointment().getPatient().getName(),
                    claim.getTotalAmount());
                listModel.addElement(displayText);
                claimMap.put(displayText, claim);
            }
        }
    }

    private void onClaimSelected() {
        String selectedValue = claimList.getSelectedValue();
        if (selectedValue == null || !claimMap.containsKey(selectedValue)) {
            setButtonsEnabled(false);
            return;
        }

        Claim claim = claimMap.get(selectedValue);
        detailsTextArea.setText("");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

        detailsTextArea.append("CLAIM DETAILS FOR REVIEW\n");
        detailsTextArea.append("---------------------------------\n");
        detailsTextArea.append(String.format("%-25s %s\n", "Claim ID:", claim.getId()));
        detailsTextArea.append(String.format("%-25s Appt #%d\n", "Appointment:", claim.getAppointment().getId()));
        
        // --- THIS IS THE FIX ---
        // Display the Insurance Authorization ID that was saved in the previous step.
        detailsTextArea.append(String.format("%-25s %s\n", "Insurance Auth ID:", claim.getInsuranceAuthorizationId()));
        // --- END OF FIX ---
        
        detailsTextArea.append(String.format("%-25s %s\n", "Patient:", claim.getAppointment().getPatient().getName()));
        detailsTextArea.append(String.format("%-25s %s\n", "Service:", claim.getAppointment().getServiceName()));
        detailsTextArea.append("---------------------------------\n");
        detailsTextArea.append(String.format("TOTAL AMOUNT TO REVIEW: %s\n", currencyFormat.format(claim.getTotalAmount())));

        setButtonsEnabled(true);
    }
    
    private void onProcessClaim() {
        String selectedValue = claimList.getSelectedValue();
        if (selectedValue == null) return;
        
        Claim claimToProcess = claimMap.get(selectedValue);

        // --- CHAIN OF RESPONSIBILITY IN ACTION (Step 2) ---
        // 1. Create the handler for this specific step.
        ClaimHandler insuranceHandler = new InsuranceApprovalHandler();
        
        // 2. Process the claim. The handler will show its own interactive dialog.
        insuranceHandler.processClaim(claimToProcess);
        
        // 3. Refresh the list. The claim should now be gone from this queue.
        loadPendingClaims();
    }
    
    private void setButtonsEnabled(boolean enabled) {
        approveButton.setEnabled(enabled);
        partialButton.setEnabled(enabled);
        denyButton.setEnabled(enabled);
    }

    private void initComponentsManual() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel("Insurance Approval Portal");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 24));
        JLabel subtitleLabel = new JLabel("Review and process claims pending insurance approval.");
        subtitleLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Main Split Pane
        JSplitPane mainSplitPane = new JSplitPane();
        mainSplitPane.setDividerLocation(350);
        add(mainSplitPane, BorderLayout.CENTER);

        // Left Side: Claims List
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        claimList = new JList<>(listModel);
        JScrollPane listScrollPane = new JScrollPane(claimList);
        listScrollPane.setBorder(BorderFactory.createTitledBorder("Pending Claims"));
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

        // Action Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        approveButton = new JButton("Approve Full Amount");
        approveButton.setBackground(new Color(40, 167, 69));
        approveButton.setForeground(Color.WHITE);
        partialButton = new JButton("Approve Partial Amount");
        denyButton = new JButton("Deny Claim");
        denyButton.setBackground(new Color(220, 53, 69));
        denyButton.setForeground(Color.WHITE);
        
        buttonPanel.add(denyButton);
        buttonPanel.add(partialButton);
        buttonPanel.add(approveButton);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainSplitPane.setRightComponent(rightPanel);

        setButtonsEnabled(false);
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
