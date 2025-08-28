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
import java.awt.Frame;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import system.model.Appointment;
import system.model.BillableItem;
import system.model.Claim;
import system.patterns.chain.ClaimHandler;
import system.patterns.chain.ValidationHandler;
import system.service.BillingService;
import system.service.ClaimService;
import system.ui.components.RoundedPanel;


/**
 *
 * @author User
 */
public class ClaimProcessingPanel extends javax.swing.JPanel {

private final ClaimService claimService;
    private final BillingService billingService;
    private final DefaultListModel<String> listModel;
    private final Map<String, Appointment> appointmentMap;

    // --- UI Component Declarations ---
    private JList<String> appointmentList;
    private JTextArea detailsTextArea;
    private JButton processClaimButton;
    private JButton refreshButton;
    private JButton openInsurancePanelButton;
    private JTextArea logTextArea; 
     private JButton openPatientBillingButton;

    public ClaimProcessingPanel() {
       this.claimService = new ClaimService();
        this.billingService = new BillingService();
        this.listModel = new DefaultListModel<>();
        this.appointmentMap = new HashMap<>();

        initComponentsManual();
        
        // Add event listeners for all interactive components
        appointmentList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onAppointmentSelected();
            }
        });
        processClaimButton.addActionListener(e -> onProcessClaim());
        refreshButton.addActionListener(e -> loadAppointmentsReadyForBilling());
        openInsurancePanelButton.addActionListener(e -> openInsurancePanel());
        openPatientBillingButton.addActionListener(e -> openPatientBillingPanel());

        // Load the initial data into the list
        loadAppointmentsReadyForBilling();
    }
    
    private void openPatientBillingPanel() {
        JDialog billingDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Patient Final Billing Portal", true);
        
        // Create an instance of the new panel
        PatientBillingPanel billingPanel = new PatientBillingPanel(); 
        
        billingDialog.setContentPane(billingPanel);
        billingDialog.pack(); // Automatically sizes the dialog to fit the panel
        billingDialog.setMinimumSize(new Dimension(800, 600)); // Set a reasonable minimum size
        billingDialog.setLocationRelativeTo(this);
        billingDialog.setVisible(true);
        
        // After the billing clerk is done, refresh this main list.
        loadAppointmentsReadyForBilling();
    }
    
    private void loadAppointmentsReadyForBilling() {
        listModel.clear();
        appointmentMap.clear();
        detailsTextArea.setText("Please select a completed appointment from the list to begin processing.");
        processClaimButton.setEnabled(false);

        List<Appointment> appointments = claimService.getAppointmentsReadyForBilling();
        if (appointments.isEmpty()) {
            listModel.addElement("No appointments are pending insurance claims.");
            appointmentList.setEnabled(false);
        } else {
            appointmentList.setEnabled(true);
            for (Appointment appt : appointments) {
                String displayText = String.format("Appt #%d - %s (%s)",
                    appt.getId(), appt.getPatient().getName(), appt.getServiceName());
                listModel.addElement(displayText);
                appointmentMap.put(displayText, appt);
            }
        }
    }

      private void onAppointmentSelected() {
        String selectedValue = appointmentList.getSelectedValue();
        if (selectedValue == null || !appointmentMap.containsKey(selectedValue)) { /* ... */ return; }
        
        Appointment appt = appointmentMap.get(selectedValue);
        detailsTextArea.setText("");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

        detailsTextArea.append("APPOINTMENT DETAILS FOR CLAIM\n");
        // ... (display appointment details as before)

        // --- THIS IS THE FIX ---
        // 1. Find the Claim associated with this Appointment.
        Claim associatedClaim = claimService.findClaimByAppointmentId(appt.getId());
        double total = appt.getPrice(); // Default to appointment price

        // 2. If a claim exists (like for a surgery), get the items FROM THE CLAIM.
        if (associatedClaim != null) {
            List<BillableItem> items = associatedClaim.getItems();
            if (!items.isEmpty()) {
                detailsTextArea.append("\nBILLABLE ITEMS (from Claim #" + associatedClaim.getId() + ")\n");
                detailsTextArea.append("---------------------------------\n");
                total = 0; // Recalculate total from items
                for (BillableItem item : items) {
                    detailsTextArea.append(String.format("- %-20s %s\n", item.getDescription(), currencyFormat.format(item.getCost())));
                    total += item.getCost();
                }
            }
        }
        // --- END OF FIX ---
        
        detailsTextArea.append("---------------------------------\n");
        detailsTextArea.append(String.format("TOTAL TO BE CLAIMED: %s\n", currencyFormat.format(total)));
        
        processClaimButton.setEnabled(true);
        logTextArea.setText("Ready to generate and process claim for Appointment #" + appt.getId());
    }

    private void onProcessClaim() {
        String selectedValue = appointmentList.getSelectedValue();
        if (selectedValue == null) return;
        
        Appointment appointmentToBill = appointmentMap.get(selectedValue);
        
        // 1. Create the initial Claim object and save it.
        Claim newClaim = claimService.createClaimFromAppointment(appointmentToBill);
        if (newClaim == null) {
            JOptionPane.showMessageDialog(this, "Could not create the initial claim record in the database.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JOptionPane.showMessageDialog(this, "Successfully created new Claim #" + newClaim.getId() + ".\nStarting validation process.", "Claim Created", JOptionPane.INFORMATION_MESSAGE);

        // 2. Create and execute the first handler in the chain.
        ClaimHandler validationHandler = new ValidationHandler();
        validationHandler.processClaim(newClaim);
        
        // 3. Refresh the list.
        loadAppointmentsReadyForBilling();
    }
    
    private void openInsurancePanel() {
        JDialog insuranceDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Insurance Agent Portal", true);
        
        // This will show an error until InsuranceApprovalPanel is created.
        InsuranceApprovalPanel insurancePanel = new InsuranceApprovalPanel(); 
        
        insuranceDialog.setContentPane(insurancePanel);
        insuranceDialog.pack();
        insuranceDialog.setLocationRelativeTo(this);
        insuranceDialog.setVisible(true);
        
        // After the insurance agent is done, this main list might have changed, so we refresh it.
        loadAppointmentsReadyForBilling();
    }

    private void initComponentsManual() {
        setBackground(new java.awt.Color(247, 247, 247));
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        JLabel titleLabel = new JLabel("Admin: Start Insurance Claim");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 24));
        JLabel subtitleLabel = new JLabel("Select a completed appointment to generate a claim and send it for validation.");
        subtitleLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(subtitleLabel);
        add(headerPanel, BorderLayout.NORTH);
        logTextArea = new JTextArea(); 

        // Main Content Split Pane
        JSplitPane mainSplitPane = new JSplitPane();
        mainSplitPane.setDividerLocation(350);
        mainSplitPane.setDividerSize(8);
        add(mainSplitPane, BorderLayout.CENTER);

        // Left Side: Appointments List
        RoundedPanel leftPanel = new RoundedPanel();
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setLayout(new BorderLayout(10, 10));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        JLabel leftTitle = new JLabel("Appointments Ready for Billing");
        leftTitle.setFont(new Font("Inter", Font.BOLD, 16));
        refreshButton = new JButton("Refresh");
        JPanel leftHeader = new JPanel(new BorderLayout());
        leftHeader.setOpaque(false);
        leftHeader.add(leftTitle, BorderLayout.WEST);
        leftHeader.add(refreshButton, BorderLayout.EAST);
        leftPanel.add(leftHeader, BorderLayout.NORTH);
        appointmentList = new JList<>(listModel);
        leftPanel.add(new JScrollPane(appointmentList), BorderLayout.CENTER);
        mainSplitPane.setLeftComponent(leftPanel);
        
        // Right Side: Details and Actions
        RoundedPanel rightPanel = new RoundedPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(new BorderLayout(10, 10));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        detailsTextArea = new JTextArea("Please select an appointment from the list.");
        detailsTextArea.setEditable(false);
        detailsTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        detailsTextArea.setWrapStyleWord(true);
        detailsTextArea.setLineWrap(true);
        JScrollPane detailsScrollPane = new JScrollPane(detailsTextArea);
        detailsScrollPane.setBorder(BorderFactory.createTitledBorder("Selected Appointment Details"));
        rightPanel.add(detailsScrollPane, BorderLayout.CENTER);

        // Button Panel at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        
        // Create the new button
        openPatientBillingButton = new JButton("Open Patient Billing UI"); // <<<--- CREATE THE BUTTON
        openPatientBillingButton.setFont(new Font("Inter", Font.PLAIN, 12));
        
        openInsurancePanelButton = new JButton("Open Insurance Agent UI");
        openInsurancePanelButton.setFont(new Font("Inter", Font.PLAIN, 12));

        processClaimButton = new JButton("Generate & Submit Claim");
        
        openInsurancePanelButton = new JButton("Open Insurance Agent UI");
        openInsurancePanelButton.setFont(new Font("Inter", Font.PLAIN, 12));

        processClaimButton = new JButton("Generate & Submit Claim");
        processClaimButton.setFont(new Font("Inter", Font.BOLD, 14));
        processClaimButton.setBackground(new Color(0, 153, 255));
        processClaimButton.setForeground(Color.WHITE);
        processClaimButton.setEnabled(false);
        
         buttonPanel.add(openPatientBillingButton); // <<<--- ADD THE BUTTON
        buttonPanel.add(openInsurancePanelButton);
        buttonPanel.add(processClaimButton);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainSplitPane.setRightComponent(rightPanel);
    }
   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel4 = new javax.swing.JPanel();
        roundedPanel1 = new system.ui.components.RoundedPanel();
        jPanel5 = new javax.swing.JPanel();
        roundedPanel2 = new system.ui.components.RoundedPanel();

        setLayout(new java.awt.CardLayout());

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new java.awt.BorderLayout(10, 0));

        jPanel3.setBackground(new java.awt.Color(247, 247, 247));
        jPanel3.setMaximumSize(new java.awt.Dimension(32767, 90));
        jPanel3.setMinimumSize(new java.awt.Dimension(0, 90));
        jPanel3.setPreferredSize(new java.awt.Dimension(987, 90));

        jLabel1.setText("Insurance Claim Processing");
        jLabel1.setFont(new java.awt.Font("Inter 18pt", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(5, 5, 5));

        jLabel2.setText("Manage Insurance Claim Processing");
        jLabel2.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addContainerGap(752, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        jPanel1.add(jPanel3, java.awt.BorderLayout.PAGE_START);

        jPanel2.setBackground(new java.awt.Color(247, 247, 247));
        jPanel2.setMinimumSize(new java.awt.Dimension(900, 700));
        jPanel2.setPreferredSize(new java.awt.Dimension(903, 700));
        jPanel2.setLayout(new java.awt.CardLayout(10, 10));

        jSplitPane1.setDividerLocation(360);
        jSplitPane1.setDividerSize(0);
        jSplitPane1.setMaximumSize(new java.awt.Dimension(2147483647, 500));
        jSplitPane1.setMinimumSize(new java.awt.Dimension(800, 300));
        jSplitPane1.setPreferredSize(new java.awt.Dimension(1250, 510));

        jPanel4.setBackground(new java.awt.Color(247, 247, 247));
        jPanel4.setMaximumSize(new java.awt.Dimension(300, 32767));
        jPanel4.setMinimumSize(new java.awt.Dimension(300, 100));
        jPanel4.setPreferredSize(new java.awt.Dimension(300, 510));
        jPanel4.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 30, 0));

        roundedPanel1.setBackground(new java.awt.Color(255, 255, 255));
        roundedPanel1.setForeground(new java.awt.Color(234, 234, 234));
        roundedPanel1.setMinimumSize(new java.awt.Dimension(330, 600));

        javax.swing.GroupLayout roundedPanel1Layout = new javax.swing.GroupLayout(roundedPanel1);
        roundedPanel1.setLayout(roundedPanel1Layout);
        roundedPanel1Layout.setHorizontalGroup(
            roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 339, Short.MAX_VALUE)
        );
        roundedPanel1Layout.setVerticalGroup(
            roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 600, Short.MAX_VALUE)
        );

        jPanel4.add(roundedPanel1);

        jSplitPane1.setLeftComponent(jPanel4);

        jPanel5.setBackground(new java.awt.Color(247, 247, 247));
        jPanel5.setMaximumSize(new java.awt.Dimension(2147483647, 600));
        jPanel5.setPreferredSize(new java.awt.Dimension(831, 600));
        jPanel5.setLayout(new java.awt.BorderLayout(30, 0));

        roundedPanel2.setBackground(new java.awt.Color(255, 255, 255));
        roundedPanel2.setForeground(new java.awt.Color(234, 234, 234));
        roundedPanel2.setMaximumSize(new java.awt.Dimension(32767, 600));
        roundedPanel2.setMinimumSize(new java.awt.Dimension(0, 600));
        roundedPanel2.setPreferredSize(new java.awt.Dimension(811, 600));

        javax.swing.GroupLayout roundedPanel2Layout = new javax.swing.GroupLayout(roundedPanel2);
        roundedPanel2.setLayout(roundedPanel2Layout);
        roundedPanel2Layout.setHorizontalGroup(
            roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 717, Short.MAX_VALUE)
        );
        roundedPanel2Layout.setVerticalGroup(
            roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 680, Short.MAX_VALUE)
        );

        jPanel5.add(roundedPanel2, java.awt.BorderLayout.CENTER);

        jSplitPane1.setRightComponent(jPanel5);

        jPanel2.add(jSplitPane1, "card2");

        jPanel1.add(jPanel2, java.awt.BorderLayout.CENTER);

        add(jPanel1, "card2");
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JSplitPane jSplitPane1;
    private system.ui.components.RoundedPanel roundedPanel1;
    private system.ui.components.RoundedPanel roundedPanel2;
    // End of variables declaration//GEN-END:variables
}
