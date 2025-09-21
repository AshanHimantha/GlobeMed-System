package system.ui.components;

import system.model.Patient;
import system.service.PatientService;
import system.ui.panels.PatientRecordPanel;
import system.model.User;
import system.service.AuthenticationService;
import system.service.AuditService;
import system.model.AuditLog;

import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.time.LocalDateTime;


public class PatientCard extends javax.swing.JPanel {
    
    // --- 1. DECLARE MEMBER VARIABLES ---
    private Patient patient; // To store the full object
    private final PatientRecordPanel parentPanel; // To store the parent reference

    /**
     * YOUR ORIGINAL CONSTRUCTOR - UNCHANGED.
     * It sets the display text.
     */
    public PatientCard(String name, String id, int age, String gender, String lastVisit, String contact, PatientRecordPanel parent) {
        initComponents();
        this.parentPanel = parent; // Store the parent reference

        // Initialize the patient object
        this.patient = new Patient();

        // Set display values from parameters
        jLabel1.setText(name);
        jLabel7.setText(id);
        jLabel8.setText(String.valueOf(age));
        jLabel9.setText(lastVisit);
        jLabel11.setText(contact);
        
        // Set patient object properties
        patient.setAge(age);
        patient.setName(name);
        patient.setGender(gender);

        // Handle lastVisit date parsing safely
        try {
            if (lastVisit != null && !lastVisit.trim().isEmpty() && !"N/A".equalsIgnoreCase(lastVisit.trim())) {
                patient.setLastVisitDate(java.time.LocalDate.parse(lastVisit));
            } else {
                patient.setLastVisitDate(null); // Set to null for invalid/missing dates
            }
        } catch (java.time.format.DateTimeParseException e) {
            patient.setLastVisitDate(null); // Set to null if parsing fails
        }

        patient.setContactNumberEncrypted(contact);
        patient.setPatientId(id);
        

        // Set gender button
        if ("Male".equalsIgnoreCase(gender)) {
            jButton1.setText("Male");
            jButton1.setBackground(new Color(217, 255, 255));
            jButton1.setForeground(new Color(0, 153, 255));
            jButton1.setIcon(new ImageIcon(getClass().getResource("/img/male.png")));
        } else {
            jButton1.setText("Female");
            jButton1.setBackground(new Color(255, 217, 255));
            jButton1.setForeground(new Color(255, 0, 127));
            jButton1.setIcon(new ImageIcon(getClass().getResource("/img/female.png")));
        } 
    }

    // --- 2. ADD A NEW PUBLIC SETTER METHOD ---
    /**
     * Sets the full Patient object for this card. This is required for the
     * Edit and View actions to work correctly.
     * @param patient The full Patient data object.
     */
    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        roundedPanel1 = new system.ui.components.RoundedPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 10, 5));
        setMaximumSize(new java.awt.Dimension(32767, 140));
        setMinimumSize(new java.awt.Dimension(0, 140));

        roundedPanel1.setBackground(new java.awt.Color(247, 247, 247));
        roundedPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        roundedPanel1.setForeground(new java.awt.Color(224, 224, 224));
        roundedPanel1.setMaximumSize(new java.awt.Dimension(32767, 130));
        roundedPanel1.setMinimumSize(new java.awt.Dimension(0, 130));

        jLabel1.setFont(new java.awt.Font("Inter 18pt SemiBold", 0, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 153, 255));
        jLabel1.setText("Ashan Himantha");

        jLabel2.setFont(new java.awt.Font("Inter 18pt", 0, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(153, 153, 153));
        jLabel2.setText("ID :");

        jLabel3.setFont(new java.awt.Font("Inter 18pt", 0, 12)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(153, 153, 153));
        jLabel3.setText("Age :");

        jLabel5.setFont(new java.awt.Font("Inter 18pt", 0, 12)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(153, 153, 153));
        jLabel5.setText("Last Visit :");

        jLabel7.setFont(new java.awt.Font("Inter 18pt", 0, 12)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(102, 102, 102));
        jLabel7.setText("P001");

        jLabel8.setFont(new java.awt.Font("Inter 18pt", 0, 12)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(102, 102, 102));
        jLabel8.setText("45");

        jLabel9.setFont(new java.awt.Font("Inter 18pt", 0, 12)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(102, 102, 102));
        jLabel9.setText("2024-01-24");

        jLabel10.setFont(new java.awt.Font("Inter 18pt SemiBold", 0, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(102, 102, 102));

        jLabel13.setFont(new java.awt.Font("Inter 18pt SemiBold", 0, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(102, 102, 102));
        jLabel13.setText("Edit");
        jLabel13.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel13MouseClicked(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Inter 18pt SemiBold", 0, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 51, 51));
        jLabel14.setText("Delete");
        jLabel14.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel14MouseClicked(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(217, 255, 255));
        jButton1.setFont(new java.awt.Font("Inter 18pt SemiBold", 0, 12)); // NOI18N
        jButton1.setForeground(new java.awt.Color(0, 153, 255));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/male.png"))); // NOI18N
        jButton1.setText("Male");
        jButton1.setBorderPainted(false);
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton1.setFocusPainted(false);
        jButton1.setFocusable(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Inter 18pt", 0, 12)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(102, 102, 102));
        jLabel11.setText("0701705553");

        jLabel12.setFont(new java.awt.Font("Inter 18pt", 0, 12)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(153, 153, 153));
        jLabel12.setText("Contact No :");

        jLabel15.setFont(new java.awt.Font("Inter 18pt SemiBold", 0, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(102, 102, 102));
        jLabel15.setText("View");
        jLabel15.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel15MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout roundedPanel1Layout = new javax.swing.GroupLayout(roundedPanel1);
        roundedPanel1.setLayout(roundedPanel1Layout);
        roundedPanel1Layout.setHorizontalGroup(
            roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundedPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel9))
                    .addGroup(roundedPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11))
                    .addGroup(roundedPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1))
                    .addGroup(roundedPanel1Layout.createSequentialGroup()
                        .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2))
                        .addGap(48, 48, 48)
                        .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(jLabel7))))
                .addGap(0, 69, Short.MAX_VALUE)
                .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundedPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                        .addComponent(jLabel15)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel13)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel14)
                        .addGap(27, 27, 27))))
        );
        roundedPanel1Layout.setVerticalGroup(
            roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel1Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jLabel13)
                    .addComponent(jLabel15))
                .addGap(52, 52, 52))
            .addGroup(roundedPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel7))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(roundedPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(roundedPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jLabel14MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel14MouseClicked
        if (patient == null) return; // Safety check
        int response = JOptionPane.showConfirmDialog(
            parentPanel,
            "Are you sure you want to delete patient " + patient.getName() + "?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (response == JOptionPane.YES_OPTION) {
            PatientService service = new PatientService();
            boolean success = service.softDeletePatient(patient.getPatientId());

            if (success) {
                JOptionPane.showMessageDialog(parentPanel, "Patient record has been deleted.");
                parentPanel.fetchAndDisplayAllPatients();
            } else {
                JOptionPane.showMessageDialog(parentPanel, "Error: Could not delete patient record.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jLabel14MouseClicked

    private void jLabel13MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel13MouseClicked
        if (patient == null) return; // Safety check
        // Use the original approach that was working in the code
        UpdatePatientForm updateForm = new UpdatePatientForm(
            jLabel1.getText(),
            jLabel7.getText(),
            Integer.parseInt(jLabel8.getText()),
            jButton1.getText(),
            jLabel9.getText(),
            jLabel11.getText(),
            parentPanel
        );
        updateForm.setVisible(true);
    }//GEN-LAST:event_jLabel13MouseClicked

    private void jLabel15MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel15MouseClicked
        if (patient == null) return; // Safety check
        // --- Audit Logging ---
        User currentUser = AuthenticationService.getInstance().getLoggedInUser();
        if (currentUser != null) {
            AuditService auditService = new AuditService();
            AuditLog logEntry = new AuditLog(
                LocalDateTime.now(),
                currentUser.getUsername(),
                currentUser.getRole().toString(),
                "VIEW_PATIENT_RECORD",
                patient.getPatientId(),
                "SUCCESS"
            );
            auditService.log(logEntry);
        }
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        // Use the original approach that was working in the code
        try {
            Class<?> dialogClass = Class.forName("system.ui.components.PatientDetailsDialog");
            java.lang.reflect.Constructor<?> constructor = dialogClass.getConstructor(
                java.awt.Frame.class, boolean.class, system.model.Patient.class
            );
            Object dialog = constructor.newInstance(parentFrame, true, this.patient);
            java.lang.reflect.Method setVisibleMethod = dialogClass.getMethod("setVisible", boolean.class);
            setVisibleMethod.invoke(dialog, true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error opening patient details: " + e.getMessage());
        }
    }//GEN-LAST:event_jLabel15MouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private system.ui.components.RoundedPanel roundedPanel1;
    // End of variables declaration//GEN-END:variables
}
