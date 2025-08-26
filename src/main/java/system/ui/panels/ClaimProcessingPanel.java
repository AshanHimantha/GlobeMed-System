/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package system.ui.panels;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import system.model.Appointment;
import system.model.Claim;
import system.patterns.chain.ClaimHandler;
import system.patterns.chain.ClaimService;
import system.patterns.chain.InsuranceApprovalHandler;
import system.patterns.chain.PatientBillingHandler;
import system.patterns.chain.ValidationHandler;
import system.service.AppointmentService;

/**
 *
 * @author User
 */
public class ClaimProcessingPanel extends javax.swing.JPanel {

//private final AppointmentService appointmentService;
//    private final ClaimService claimService;
//    
//    // UI Components
//    private final JList<String> appointmentList;
//    private final DefaultListModel<String> listModel;
//    private final JButton processClaimButton;
//    private final JTextArea logTextArea;
//
//    // Data map to link the display string back to the Appointment object
//    private final Map<String, Appointment> appointmentMap;

//    public ClaimProcessingPanel() {
//        this.appointmentService = new AppointmentService();
//        this.claimService = new ClaimService();
//        this.appointmentMap = new HashMap<>();
//
//        // --- Setup Main Layout ---
//        setLayout(new BorderLayout(10, 10));
//        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
//
//        // --- Create UI Components ---
//        JLabel titleLabel = new JLabel("Billing & Claims (Chain of Responsibility)");
//        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
//        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
//        
//        listModel = new DefaultListModel<>();
//        appointmentList = new JList<>(listModel);
//        JScrollPane listScrollPane = new JScrollPane(appointmentList);
//        listScrollPane.setBorder(BorderFactory.createTitledBorder("Completed Appointments Ready for Billing"));
//        
//        processClaimButton = new JButton("Generate & Process Claim for Selected Appointment");
//        
//        logTextArea = new JTextArea(15, 50);
//        logTextArea.setEditable(false);
//        logTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
//        JScrollPane logScrollPane = new JScrollPane(logTextArea);
//        logScrollPane.setBorder(BorderFactory.createTitledBorder("Processing Log"));
//
//        // --- Assemble the Panel ---
//        add(titleLabel, BorderLayout.NORTH);
//        
//        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, listScrollPane, logScrollPane);
//        splitPane.setResizeWeight(0.4); // Give more space to the log
//        add(splitPane, BorderLayout.CENTER);
//
//        add(processClaimButton, BorderLayout.SOUTH);
//
//        // --- Add Listeners ---
////        processClaimButton.addActionListener(e -> onProcessClaim());
//        
//        // --- Initial Data Load ---
//        loadCompletedAppointments();
//    }

//    private void loadCompletedAppointments() {
//        listModel.clear();
//        appointmentMap.clear();
//        
//        List<Appointment> completedAppointments = appointmentService.getCompletedAppointments();
//        for (Appointment appt : completedAppointments) {
//            String displayText = String.format("ID: %d - %s with Dr. %s",
//                appt.getId(),
//                appt.getPatient().getName(),
//                appt.getDoctor().getLastName()
//            );
//            listModel.addElement(displayText);
//            appointmentMap.put(displayText, appt);
//        }
//    }

//    private void onProcessClaim() {
//        String selectedValue = appointmentList.getSelectedValue();
//        if (selectedValue == null) {
//            JOptionPane.showMessageDialog(this, "Please select a completed appointment to process.", "No Selection", JOptionPane.WARNING_MESSAGE);
//            return;
//        }
//        
//        logTextArea.setText(""); // Clear the log
//        
//        // Retrieve the full Appointment object from our map
//        Appointment appointmentToBill = appointmentMap.get(selectedValue);
//
//        // Create a new claim object (for this demo, we'll use a fixed amount)
//        double claimAmount = 150.00; // Example amount for a consultation
//        Claim newClaim = new Claim(appointmentToBill, claimAmount);
//        
//        // Persist the initial claim state
//        claimService.updateClaim(newClaim);
//        logTextArea.append("New claim created (ID: " + newClaim.getId() + ") for Appointment " + appointmentToBill.getId() + "\n");
//        logTextArea.append("--- Starting processing chain ---\n");
//
//        // --- CHAIN OF RESPONSIBILITY IN ACTION ---
//        // 1. Create the handlers, passing them the log area to write to
//        ClaimHandler validationHandler = new ValidationHandler(logTextArea);
//        ClaimHandler insuranceHandler = new InsuranceApprovalHandler(logTextArea);
//        ClaimHandler billingHandler = new PatientBillingHandler(logTextArea);
//
//        // 2. Link the handlers to form the chain
//        validationHandler.setNextHandler(insuranceHandler);
//        insuranceHandler.setNextHandler(billingHandler);
//
//        // 3. Start the process by giving the claim to the first handler in the chain
//        validationHandler.processClaim(newClaim);
//        // --- END OF PATTERN ---
//        
//        // Refresh the list, as the processed appointment is now billed.
//        // A real app might hide it from this list. For now, we just refresh.
//        loadCompletedAppointments(); 
//    }
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
            .addGap(0, 1097, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 645, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
