package system.ui.components;

import java.awt.Color;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import system.model.Patient;
import system.service.EncryptionUtil;
import system.service.PatientService;
import system.ui.panels.PatientRecordPanel;

public class UpdatePatientForm extends javax.swing.JFrame {

    private Patient patientToUpdate;
    private final PatientService patientService;
    private final PatientRecordPanel parentPanel;

    public UpdatePatientForm(String name, String id, int age, String gender, String lastVisit, String history, String contact, PatientRecordPanel parent) {
        initComponents();

        patientToUpdate = new Patient();
        this.parentPanel = parent;

        patientToUpdate.setAge(age);
        patientToUpdate.setPatientId(id);
        patientToUpdate.setMedicalHistory(history);
        patientToUpdate.setContactNumberEncrypted(contact);

        this.patientService = new PatientService();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        roundedTextField1.setText(name);
        roundedTextField1.setEnabled(false);
        roundedTextField2.setText(String.valueOf(age));
        roundedTextField3.setText(contact);
        jTextArea1.setText(history);

        this.setTitle("Edit Patient Info");
        Image icon = Toolkit.getDefaultToolkit().getImage("src/main/resources/img/icon.png");
        setIconImage(icon);

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        roundedTextField2 = new system.ui.components.RoundedTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        roundedTextField1 = new system.ui.components.RoundedTextField();
        roundedTextField3 = new system.ui.components.RoundedTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        jPanel1.setBackground(new java.awt.Color(247, 247, 247));
        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 10, 20, 10));

        jLabel3.setBackground(new java.awt.Color(0, 0, 0));
        jLabel3.setFont(new java.awt.Font("Inter 18pt SemiBold", 0, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Edit Patient");

        jTextArea1.setBackground(new java.awt.Color(249, 249, 249));
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jTextArea1.setForeground(new java.awt.Color(51, 51, 51));
        jTextArea1.setRows(1);
        jTextArea1.setTabSize(1);
        jTextArea1.setText("Enter Medical History");
        jTextArea1.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        jTextArea1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTextArea1FocusGained(evt);
            }
        });
        jScrollPane1.setViewportView(jTextArea1);

        roundedTextField2.setBackground(new java.awt.Color(249, 249, 249));
        roundedTextField2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1));
        roundedTextField2.setForeground(new java.awt.Color(51, 51, 51));
        roundedTextField2.setText("Enter Age");
        roundedTextField2.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        roundedTextField2.setMargin(new java.awt.Insets(2, 20, 2, 20));
        roundedTextField2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                roundedTextField2FocusGained(evt);
            }
        });

        jLabel6.setBackground(new java.awt.Color(51, 51, 51));
        jLabel6.setFont(new java.awt.Font("Inter 18pt SemiBold", 0, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(51, 51, 51));
        jLabel6.setText("Age");

        jLabel9.setBackground(new java.awt.Color(51, 51, 51));
        jLabel9.setFont(new java.awt.Font("Inter 18pt SemiBold", 0, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(51, 51, 51));
        jLabel9.setText("Medical History");

        roundedTextField1.setBackground(new java.awt.Color(249, 249, 249));
        roundedTextField1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1));
        roundedTextField1.setForeground(new java.awt.Color(51, 51, 51));
        roundedTextField1.setText("Enter Patient name");
        roundedTextField1.setToolTipText("");
        roundedTextField1.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        roundedTextField1.setName(""); // NOI18N
        roundedTextField1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                roundedTextField1FocusGained(evt);
            }
        });

        roundedTextField3.setBackground(new java.awt.Color(249, 249, 249));
        roundedTextField3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1));
        roundedTextField3.setForeground(new java.awt.Color(51, 51, 51));
        roundedTextField3.setText("Enter Contact Number");
        roundedTextField3.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        roundedTextField3.setMargin(new java.awt.Insets(2, 20, 2, 20));
        roundedTextField3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                roundedTextField3FocusGained(evt);
            }
        });

        jLabel4.setBackground(new java.awt.Color(153, 153, 153));
        jLabel4.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(153, 153, 153));
        jLabel4.setText("Enter patient details");

        jLabel5.setBackground(new java.awt.Color(51, 51, 51));
        jLabel5.setFont(new java.awt.Font("Inter 18pt SemiBold", 0, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(51, 51, 51));
        jLabel5.setText("Patinet Name");

        jButton3.setBackground(new java.awt.Color(0, 153, 255));
        jButton3.setFont(new java.awt.Font("Inter 18pt SemiBold", 0, 14)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Save Patient");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel8.setBackground(new java.awt.Color(51, 51, 51));
        jLabel8.setFont(new java.awt.Font("Inter 18pt SemiBold", 0, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(51, 51, 51));
        jLabel8.setText("Contact no");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 363, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(roundedTextField2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(roundedTextField1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING))
                            .addGap(0, 0, Short.MAX_VALUE))
                        .addComponent(roundedTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE))
                    .addContainerGap()))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 622, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jLabel3)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jLabel4)
                    .addGap(18, 18, 18)
                    .addComponent(jLabel5)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(roundedTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jLabel6)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(roundedTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jLabel8)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(roundedTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                    .addComponent(jLabel9)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
                    .addGap(18, 18, 18)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );

        getContentPane().add(jPanel1);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void roundedTextField1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_roundedTextField1FocusGained
        if (roundedTextField1.getText().equals("Enter Patient name")) {
            roundedTextField1.setText("");
            roundedTextField1.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_roundedTextField1FocusGained

    private void roundedTextField2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_roundedTextField2FocusGained
        if (roundedTextField2.getText().equals("Enter Age")) {
            roundedTextField2.setText("");
            roundedTextField2.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_roundedTextField2FocusGained

    private void roundedTextField3FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_roundedTextField3FocusGained
        if (roundedTextField3.getText().equals("Enter Contact Number")) {
            roundedTextField3.setText("");
            roundedTextField3.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_roundedTextField3FocusGained

    private void jTextArea1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextArea1FocusGained

        if (jTextArea1.getText().equals("Enter Medical History")) {
            jTextArea1.setText("");
            jTextArea1.setForeground(Color.BLACK);
        }
    }//GEN-LAST:event_jTextArea1FocusGained

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed

        String ageStr = roundedTextField2.getText();
        String contactNo = roundedTextField3.getText();
        String history = jTextArea1.getText();

        // --- 2. VALIDATE the input ---
        if (ageStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Age cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Age must be a valid number.",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

// Extra validation: Age must be between 0 and 120
        if (age < 0 || age > 120) {
            JOptionPane.showMessageDialog(this, "Age must be between 0 and 120.",
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- 4. CALL the service to save changes to the database ---
        boolean success = patientService.updatePatientDetails(patientToUpdate.getPatientId(), Integer.parseInt(ageStr), contactNo, history);

        // --- 5. PROVIDE FEEDBACK, refresh parent, and close this form ---
        if (success) {
            JOptionPane.showMessageDialog(this, "Patient details updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            parentPanel.fetchAndDisplayAllPatients();
            this.dispose(); // Close the update window
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update patient details.", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_jButton3ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private system.ui.components.RoundedTextField roundedTextField1;
    private system.ui.components.RoundedTextField roundedTextField2;
    private system.ui.components.RoundedTextField roundedTextField3;
    // End of variables declaration//GEN-END:variables
}
