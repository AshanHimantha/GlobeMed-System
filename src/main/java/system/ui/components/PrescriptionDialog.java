/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package system.ui.components;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import system.model.Appointment;
import system.model.Prescription;
import system.model.PrescriptionItem;
import system.service.AppointmentService;
import system.service.PrescriptionService;

/**
 *
 * @author User
 */
public class PrescriptionDialog extends javax.swing.JDialog {

     private final Appointment appointment;
    private final PrescriptionService prescriptionService;
    private final AppointmentService appointmentService;
    private final Runnable refreshCallback;
    private final DefaultTableModel tableModel;
    private final List<PrescriptionItem> tempPrescriptionItems = new ArrayList<>();
    
    
   public PrescriptionDialog(java.awt.Frame parent, boolean modal, Appointment appointment, Runnable refreshCallback) {
        super(parent, modal);
        initComponents();

        this.appointment = appointment;
        this.refreshCallback = refreshCallback;
        this.prescriptionService = new PrescriptionService();
        this.appointmentService = new AppointmentService();

        // --- Configure the Dialog and Table ---
        setTitle("Create Prescription for Appointment #" + appointment.getId());
        setLocationRelativeTo(parent);

        // Setup a clean JTable model
        tableModel = new DefaultTableModel(new Object[]{"Drug Name", "Dosage", "Quantity"}, 0);
        jTable1.setModel(tableModel);
        
        // Configure the JSpinner to only accept numbers, with a minimum of 1
        jSpinner1.setModel(new SpinnerNumberModel(1, 1, 100, 1));

        // --- Populate Initial Info ---
        jLabel1.setText("Create Prescription"); // Title
        jLabel2.setText("For Appointment ID: " + appointment.getId());
        jLabel4.setText(appointment.getPatient().getName() + " (ID: " + appointment.getPatient().getPatientId() + ")");
    }
   
   private void addDrugButtonActionPerformed(java.awt.event.ActionEvent evt) {
        String drugName = roundedTextField1.getText().trim();
        String dosage = roundedTextField2.getText().trim();
        int quantity = (Integer) jSpinner1.getValue();

        if (drugName.isEmpty() || dosage.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Drug Name and Dosage are required.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        PrescriptionItem newItem = new PrescriptionItem(drugName, dosage, quantity);
        tempPrescriptionItems.add(newItem);
        tableModel.addRow(new Object[]{drugName, dosage, quantity});

        // Clear fields for next entry
        roundedTextField1.setText("");
        roundedTextField2.setText("");
        jSpinner1.setValue(1);
        roundedTextField1.requestFocusInWindow();
    }

    private void savePrescriptionButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (tempPrescriptionItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add at least one medication to the prescription.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Prescription newPrescription = new Prescription(appointment);
        for (PrescriptionItem item : tempPrescriptionItems) {
            newPrescription.addItem(item);
        }

        Prescription savedPrescription = prescriptionService.createPrescription(newPrescription);

        if (savedPrescription != null) {
            appointmentService.updateAppointmentStatus(appointment.getId(), "COMPLETED");
            JOptionPane.showMessageDialog(this, "Prescription saved and appointment marked as COMPLETED.", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            if (refreshCallback != null) {
                refreshCallback.run();
            }
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save the prescription.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        this.dispose();
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        roundedTextField1 = new system.ui.components.RoundedTextField();
        roundedTextField2 = new system.ui.components.RoundedTextField();
        jButton1 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jSpinner1 = new javax.swing.JSpinner();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setText("Create Prescription");
        jLabel1.setFont(new java.awt.Font("Inter 18pt", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(5, 5, 5));

        jLabel2.setText("Manage patient appintments and doctor schedules");
        jLabel2.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N

        jLabel3.setText("Patient :");

        jLabel4.setText("jLabel4");

        jButton1.setText("Add to Prescription");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel5.setText("Drug Name");

        jLabel6.setText("Dosage ");

        jLabel7.setText("Quantity");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Drug Name", "Dosage", "Quantity"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jButton2.setText("Cancel");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(0, 153, 255));
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Save");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton3)
                        .addGap(18, 18, 18)
                        .addComponent(jButton2))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel4))
                            .addComponent(jLabel2)
                            .addComponent(jLabel1)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(roundedTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addComponent(roundedTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jButton1))
                                    .addComponent(jLabel7))))))
                .addContainerGap(35, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addGap(45, 45, 45)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4))
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(roundedTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(roundedTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1)
                    .addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton3))
                .addGap(17, 17, 17))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        cancelButtonActionPerformed(evt);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        addDrugButtonActionPerformed(evt);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        savePrescriptionButtonActionPerformed(evt);
    }//GEN-LAST:event_jButton3ActionPerformed

   

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner jSpinner1;
    private javax.swing.JTable jTable1;
    private system.ui.components.RoundedTextField roundedTextField1;
    private system.ui.components.RoundedTextField roundedTextField2;
    // End of variables declaration//GEN-END:variables
}
