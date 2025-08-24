
package system.ui.panels;

import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;
import system.model.Patient;
import system.service.PatientService;
import system.ui.components.PatientCard;
import java.util.List; 
import system.service.EncryptionUtil;

public class PatientRecordPanel extends javax.swing.JPanel {
    
private final PatientService patientService;
    
 public PatientRecordPanel() {
     initComponents();
     this.patientService = new PatientService();
       fetchAndDisplayAllPatients();
     
    }
 
  public void fetchAndDisplayAllPatients(){
       jPanel5.removeAll();
      java.util.List<Patient> pList = patientService.getAllPatients();
      addPatientCardsToPanel(pList);

        // Refresh the panel to show the new components
        jPanel5.revalidate();
        jPanel5.repaint();
      
  }
  
  private void addPatientCardsToPanel(List<Patient> patientList) {
        if (patientList.isEmpty()) {
            // Optional: Show a message if no patients are found
            jPanel5.add(new javax.swing.JLabel("No patient records found."));
        } else {
             EncryptionUtil decypt = new EncryptionUtil();
            for (Patient patient : patientList) {
                // Use the safe date formatter
                String lastVisit = formatLastVisitDate(patient.getLastVisitDate());
                jPanel5.add(new PatientCard(patient.getName(), patient.getPatientId(), patient.getAge(), patient.getGender(), lastVisit, patient.getMedicalHistory(),  decypt.decrypt(patient.getContactNumberEncrypted()),this ));
            }
        }
    }
  
   private String formatLastVisitDate(LocalDate date) {
        if (date == null) {
            return "N/A"; // Return "Not Available" if the date is null
        }
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
 
 

 
   

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        roundedPanel1 = new system.ui.components.RoundedPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        roundedTextField1 = new system.ui.components.RoundedTextField();
        jLabel6 = new javax.swing.JLabel();
        roundedTextField2 = new system.ui.components.RoundedTextField();
        jLabel7 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel8 = new javax.swing.JLabel();
        roundedTextField3 = new system.ui.components.RoundedTextField();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton3 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        roundedPanel2 = new system.ui.components.RoundedPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        roundedTextField4 = new system.ui.components.RoundedTextField();
        jButton4 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel5 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(247, 247, 247));
        jPanel1.setMinimumSize(new java.awt.Dimension(900, 700));
        jPanel1.setPreferredSize(new java.awt.Dimension(903, 700));
        jPanel1.setLayout(new java.awt.CardLayout());

        jSplitPane1.setDividerLocation(360);
        jSplitPane1.setDividerSize(0);
        jSplitPane1.setMaximumSize(new java.awt.Dimension(2147483647, 500));
        jSplitPane1.setMinimumSize(new java.awt.Dimension(800, 300));
        jSplitPane1.setPreferredSize(new java.awt.Dimension(1250, 510));

        jPanel2.setBackground(new java.awt.Color(247, 247, 247));
        jPanel2.setMaximumSize(new java.awt.Dimension(300, 32767));
        jPanel2.setMinimumSize(new java.awt.Dimension(300, 100));
        jPanel2.setPreferredSize(new java.awt.Dimension(300, 510));
        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 30, 0));

        roundedPanel1.setBackground(new java.awt.Color(255, 255, 255));
        roundedPanel1.setForeground(new java.awt.Color(234, 234, 234));
        roundedPanel1.setMinimumSize(new java.awt.Dimension(330, 600));
        roundedPanel1.setPreferredSize(new java.awt.Dimension(330, 600));

        jLabel3.setBackground(new java.awt.Color(0, 0, 0));
        jLabel3.setFont(new java.awt.Font("Inter 18pt SemiBold", 0, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Add New Patient");

        jLabel4.setBackground(new java.awt.Color(153, 153, 153));
        jLabel4.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(153, 153, 153));
        jLabel4.setText("Enter patient details");

        jLabel5.setBackground(new java.awt.Color(51, 51, 51));
        jLabel5.setFont(new java.awt.Font("Inter 18pt SemiBold", 0, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(51, 51, 51));
        jLabel5.setText("Patinet Name");

        roundedTextField1.setBackground(new java.awt.Color(249, 249, 249));
        roundedTextField1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1));
        roundedTextField1.setForeground(new java.awt.Color(153, 153, 153));
        roundedTextField1.setText("Enter Patient name");
        roundedTextField1.setToolTipText("");
        roundedTextField1.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        roundedTextField1.setName(""); // NOI18N
        roundedTextField1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                roundedTextField1FocusGained(evt);
            }
        });

        jLabel6.setBackground(new java.awt.Color(51, 51, 51));
        jLabel6.setFont(new java.awt.Font("Inter 18pt SemiBold", 0, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(51, 51, 51));
        jLabel6.setText("Age");

        roundedTextField2.setBackground(new java.awt.Color(249, 249, 249));
        roundedTextField2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1));
        roundedTextField2.setForeground(new java.awt.Color(153, 153, 153));
        roundedTextField2.setText("Enter Age");
        roundedTextField2.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        roundedTextField2.setMargin(new java.awt.Insets(2, 20, 2, 20));
        roundedTextField2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                roundedTextField2FocusGained(evt);
            }
        });

        jLabel7.setBackground(new java.awt.Color(51, 51, 51));
        jLabel7.setFont(new java.awt.Font("Inter 18pt SemiBold", 0, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(51, 51, 51));
        jLabel7.setText("Gender");

        jComboBox1.setBackground(new java.awt.Color(247, 247, 247));
        jComboBox1.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Male", "Female" }));

        jLabel8.setBackground(new java.awt.Color(51, 51, 51));
        jLabel8.setFont(new java.awt.Font("Inter 18pt SemiBold", 0, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(51, 51, 51));
        jLabel8.setText("Contact no");

        roundedTextField3.setBackground(new java.awt.Color(249, 249, 249));
        roundedTextField3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1));
        roundedTextField3.setForeground(new java.awt.Color(153, 153, 153));
        roundedTextField3.setText("Enter Contact Number");
        roundedTextField3.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        roundedTextField3.setMargin(new java.awt.Insets(2, 20, 2, 20));
        roundedTextField3.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                roundedTextField3FocusGained(evt);
            }
        });

        jLabel9.setBackground(new java.awt.Color(51, 51, 51));
        jLabel9.setFont(new java.awt.Font("Inter 18pt SemiBold", 0, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(51, 51, 51));
        jLabel9.setText("Medical History");

        jTextArea1.setBackground(new java.awt.Color(249, 249, 249));
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jTextArea1.setForeground(new java.awt.Color(153, 153, 153));
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

        jButton3.setBackground(new java.awt.Color(0, 153, 255));
        jButton3.setFont(new java.awt.Font("Inter 18pt SemiBold", 0, 14)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Save Patient");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout roundedPanel1Layout = new javax.swing.GroupLayout(roundedPanel1);
        roundedPanel1.setLayout(roundedPanel1Layout);
        roundedPanel1Layout.setHorizontalGroup(
            roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(roundedTextField2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE)
                    .addComponent(roundedTextField1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, roundedPanel1Layout.createSequentialGroup()
                        .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(roundedTextField3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(19, 19, 19))
        );
        roundedPanel1Layout.setVerticalGroup(
            roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
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
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(roundedTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29))
        );

        roundedTextField2.getAccessibleContext().setAccessibleDescription("");

        jPanel2.add(roundedPanel1);

        jSplitPane1.setLeftComponent(jPanel2);

        jPanel4.setBackground(new java.awt.Color(247, 247, 247));
        jPanel4.setMaximumSize(new java.awt.Dimension(2147483647, 600));
        jPanel4.setMinimumSize(new java.awt.Dimension(0, 600));
        jPanel4.setPreferredSize(new java.awt.Dimension(831, 600));
        jPanel4.setLayout(new java.awt.BorderLayout(10, 0));

        roundedPanel2.setBackground(new java.awt.Color(255, 255, 255));
        roundedPanel2.setForeground(new java.awt.Color(234, 234, 234));
        roundedPanel2.setMaximumSize(new java.awt.Dimension(32767, 600));
        roundedPanel2.setMinimumSize(new java.awt.Dimension(0, 600));
        roundedPanel2.setPreferredSize(new java.awt.Dimension(811, 600));

        jLabel10.setBackground(new java.awt.Color(0, 0, 0));
        jLabel10.setFont(new java.awt.Font("Inter 18pt SemiBold", 0, 24)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 0, 0));
        jLabel10.setText("Add New Patient");

        jLabel11.setBackground(new java.awt.Color(153, 153, 153));
        jLabel11.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(153, 153, 153));
        jLabel11.setText("Search and manage patient records");

        roundedTextField4.setBackground(new java.awt.Color(247, 247, 247));
        roundedTextField4.setForeground(new java.awt.Color(204, 204, 204));
        roundedTextField4.setText("Search by Name or ID");
        roundedTextField4.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                roundedTextField4FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                roundedTextField4FocusLost(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(0, 153, 255));
        jButton4.setFont(new java.awt.Font("Inter 18pt SemiBold", 0, 14)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("Search");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jScrollPane2.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 30, 1));
        jScrollPane2.setForeground(new java.awt.Color(255, 255, 255));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setForeground(new java.awt.Color(255, 255, 255));
        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.Y_AXIS));
        jScrollPane2.setViewportView(jPanel5);

        javax.swing.GroupLayout roundedPanel2Layout = new javax.swing.GroupLayout(roundedPanel2);
        roundedPanel2.setLayout(roundedPanel2Layout);
        roundedPanel2Layout.setHorizontalGroup(
            roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel2Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2)
                    .addGroup(roundedPanel2Layout.createSequentialGroup()
                        .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addGroup(roundedPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 311, Short.MAX_VALUE)
                                .addComponent(roundedTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(20, 20, 20))
        );
        roundedPanel2Layout.setVerticalGroup(
            roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel2Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(roundedTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 698, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel4.add(roundedPanel2, java.awt.BorderLayout.CENTER);

        jSplitPane1.setRightComponent(jPanel4);

        jPanel1.add(jSplitPane1, "card2");

        add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanel3.setBackground(new java.awt.Color(247, 247, 247));
        jPanel3.setMaximumSize(new java.awt.Dimension(32767, 90));
        jPanel3.setMinimumSize(new java.awt.Dimension(0, 90));
        jPanel3.setPreferredSize(new java.awt.Dimension(987, 90));

        jLabel1.setFont(new java.awt.Font("Inter 18pt", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(5, 5, 5));
        jLabel1.setText("Patient Records");

        jLabel2.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel2.setText("Manage patient information and medical records");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addContainerGap(1006, Short.MAX_VALUE))
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

        add(jPanel3, java.awt.BorderLayout.PAGE_START);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
       // --- A. GATHER DATA from UI fields ---
        String name = roundedTextField1.getText();
        String ageStr = roundedTextField2.getText();
        String gender = (String) jComboBox1.getSelectedItem();
        String contactNo = roundedTextField3.getText();
        String history = jTextArea1.getText();

        // --- B. VALIDATE the input ---
        if (name.isEmpty() || name.equals("Enter Patient name") || ageStr.isEmpty() || ageStr.equals("Enter Age")) {
            JOptionPane.showMessageDialog(this, "Patient Name and Age are required fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age < 0 || age > 120) {
                JOptionPane.showMessageDialog(this, "Please enter a valid age.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Age must be a valid number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- C. CALL the service to add the patient ---
        Patient createdPatient = patientService.addPatient(name, age, gender, contactNo, history);

        // --- D. PROVIDE FEEDBACK to the user ---
   if (createdPatient != null) {
            JOptionPane.showMessageDialog(this,
                    "Patient saved successfully!\nNew Patient ID: " + createdPatient.getPatientId(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            fetchAndDisplayAllPatients(); // <<< --- IMPROVEMENT: Refresh the list automatically
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to save patient. A database error occurred.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    
    }//GEN-LAST:event_jButton3ActionPerformed

    private void roundedTextField1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_roundedTextField1FocusGained
       if(roundedTextField1.getText().equals("Enter Patient name")){
            roundedTextField1.setText("");
            roundedTextField1.setForeground(Color.BLACK);
    }
    }//GEN-LAST:event_roundedTextField1FocusGained

    private void roundedTextField2FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_roundedTextField2FocusGained
          if(roundedTextField2.getText().equals("Enter Age")){
               roundedTextField2.setText("");
                roundedTextField2.setForeground(Color.BLACK);
          }
    }//GEN-LAST:event_roundedTextField2FocusGained

    private void roundedTextField3FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_roundedTextField3FocusGained
         if(roundedTextField3.getText().equals("Enter Contact Number")){
               roundedTextField3.setText("");
                roundedTextField3.setForeground(Color.BLACK);
          }
    }//GEN-LAST:event_roundedTextField3FocusGained

    private void jTextArea1FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jTextArea1FocusGained
              
               if(jTextArea1.getText().equals("Enter Medical History")){
               jTextArea1.setText("");
                jTextArea1.setForeground(Color.BLACK);
          }
    }//GEN-LAST:event_jTextArea1FocusGained

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
       
      String searchInput = roundedTextField4.getText();
        
        // Clear the panel before showing search results
        
        jPanel5.removeAll();

        if (searchInput != null && !searchInput.trim().isEmpty() && !searchInput.equals("Search by Name or ID")) {
            List<Patient> searchList = patientService.searchPatients(searchInput);
            
            addPatientCardsToPanel(searchList); // Use the helper method
        } else {
            // If the search bar is empty, show all patients again
            fetchAndDisplayAllPatients();
        }
        
        // Refresh the panel
        jPanel5.revalidate();
        jPanel5.repaint();
        
    }//GEN-LAST:event_jButton4ActionPerformed

    private void roundedTextField4FocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_roundedTextField4FocusLost

    }//GEN-LAST:event_roundedTextField4FocusLost

    private void roundedTextField4FocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_roundedTextField4FocusGained
       roundedTextField4.setText("");
       roundedTextField4.setForeground(Color.BLACK);
    }//GEN-LAST:event_roundedTextField4FocusGained

private void clearFields() {
        roundedTextField1.setText("Enter Patient name");
        roundedTextField1.setForeground(Color.GRAY);
        roundedTextField2.setText("Enter Age");
        roundedTextField2.setForeground(Color.GRAY);
        roundedTextField3.setText("Enter Contact Number");
        roundedTextField3.setForeground(Color.GRAY);
        jTextArea1.setText("Enter Medical History");
        jTextArea1.setForeground(Color.GRAY);
        jComboBox1.setSelectedIndex(0);
    }
    
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTextArea jTextArea1;
    private system.ui.components.RoundedPanel roundedPanel1;
    private system.ui.components.RoundedPanel roundedPanel2;
    private system.ui.components.RoundedTextField roundedTextField1;
    private system.ui.components.RoundedTextField roundedTextField2;
    private system.ui.components.RoundedTextField roundedTextField3;
    private system.ui.components.RoundedTextField roundedTextField4;
    // End of variables declaration//GEN-END:variables
}
