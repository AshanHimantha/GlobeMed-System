package system.ui.panels;

import java.awt.Font;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import system.model.Appointment;
import system.model.Patient;
import system.model.Prescription;
import system.model.PrescriptionItem;
import system.service.AppointmentService;
import system.service.PatientService;
import system.service.PrescriptionService;

/**
 *
 * @author User
 */
public class PharmacyPanel extends javax.swing.JPanel {

    private final PatientService patientService;
    private final PrescriptionService prescriptionService;
    private final AppointmentService appointmentService;

    // Data maps for linking UI strings to objects
    private final Map<String, Patient> patientMap = new HashMap<>();

    public PharmacyPanel() {
        this.patientService = new PatientService();
        this.prescriptionService = new PrescriptionService();
        this.appointmentService = new AppointmentService();

        initComponents(); // NetBeans-generated components

        // Configure UI components after they have been created
        configureComponents();

        // Add listeners
        addListeners();
    }

    private void configureComponents() {
        // Update header labels for pharmacy (already set in initComponents)

        // Set up the JTable with the correct columns
        DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Date", "Drug Name", "Dosage", "Quantity"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        jTable1.setModel(model);
        jTable1.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));

        // Center-align the text in the table columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < jTable1.getColumnCount(); i++) {
            jTable1.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Set placeholder text for appointment ID search
        roundedTextField1.setText("");
    }

    private void addListeners() {
        // Listener for the search text field
        roundedTextField1.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { searchByAppointmentId(); }
            public void removeUpdate(DocumentEvent e) { searchByAppointmentId(); }
            public void changedUpdate(DocumentEvent e) { /* Not needed */ }
        });

        // The search button to search by appointment ID
        jButton1.addActionListener(e -> searchByAppointmentId());
    }

    private void searchByAppointmentId() {
        String appointmentIdText = roundedTextField1.getText().trim();
        DefaultTableModel tableModel = (DefaultTableModel) jTable1.getModel();
        tableModel.setRowCount(0); // Clear the table

        // Reset patient info labels (using the correct label references)
        jLabel7.setText("Patient ID :");
        jLabel8.setText("Patient Name :");

        if (appointmentIdText.isEmpty()) {
            return; // Don't search if field is empty
        }

        try {
            Long appointmentId = Long.parseLong(appointmentIdText);
            Appointment appointment = appointmentService.getAppointmentById(appointmentId);

            if (appointment != null) {
                Patient patient = appointment.getPatient();
                onPatientSelectedFromAppointment(patient, appointmentId);
            } else {
                tableModel.addRow(new Object[]{"Appointment ID not found.", "", "", ""});
            }
        } catch (NumberFormatException e) {
            tableModel.addRow(new Object[]{"Please enter a valid appointment ID (numbers only).", "", "", ""});
        }
    }

    private void onPatientSelectedFromAppointment(Patient selectedPatient, Long appointmentId) {
        DefaultTableModel tableModel = (DefaultTableModel) jTable1.getModel();
        tableModel.setRowCount(0); // Clear the table

        if (selectedPatient == null) return;

        // Update the info labels with actual data
        jLabel7.setText("Patient ID : " + selectedPatient.getPatientId());
        jLabel8.setText("Patient Name : " + selectedPatient.getName());

        List<Prescription> prescriptions = prescriptionService.getPrescriptionsForPatient(selectedPatient);
        
        if (prescriptions.isEmpty()) {
            tableModel.addRow(new Object[]{"No prescriptions found for this patient.", "", "", ""});
        } else {
            for (Prescription p : prescriptions) {
                for (PrescriptionItem item : p.getItems()) {
                    // Populate table with prescription data
                    tableModel.addRow(new Object[]{
                        p.getPrescriptionDate(),
                        item.getDrugName(),
                        item.getDosage(),
                        item.getQuantity()
                    });
                }
            }
        }
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        roundedPanel1 = new system.ui.components.RoundedPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        roundedTextField1 = new system.ui.components.RoundedTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        roundedPanel2 = new system.ui.components.RoundedPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new java.awt.BorderLayout(10, 0));

        jPanel3.setBackground(new java.awt.Color(247, 247, 247));
        jPanel3.setMaximumSize(new java.awt.Dimension(32767, 90));
        jPanel3.setMinimumSize(new java.awt.Dimension(0, 90));
        jPanel3.setPreferredSize(new java.awt.Dimension(987, 90));

        jLabel1.setText("Pharmacy Portal - Patient Prescription History");
        jLabel1.setFont(new java.awt.Font("Inter 18pt", Font.BOLD, 24));
        jLabel1.setForeground(new java.awt.Color(5, 5, 5));

        jLabel2.setText("Enter appointment ID to view patient's prescription records.");
        jLabel2.setFont(new java.awt.Font("Inter", Font.PLAIN, 12));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addContainerGap(967, Short.MAX_VALUE))
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

        jPanel1.setBackground(new java.awt.Color(247, 247, 247));
        jPanel1.setMinimumSize(new java.awt.Dimension(900, 700));
        jPanel1.setPreferredSize(new java.awt.Dimension(903, 700));
        jPanel1.setLayout(new java.awt.CardLayout(10, 10));

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

        jLabel3.setText("Search Patient");
        jLabel3.setBackground(new java.awt.Color(0, 0, 0));
        jLabel3.setFont(new java.awt.Font("Inter 18pt SemiBold", Font.PLAIN, 24));
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));

        jLabel4.setText("Enter appointment ID to search");
        jLabel4.setBackground(new java.awt.Color(153, 153, 153));
        jLabel4.setFont(new java.awt.Font("Inter", Font.PLAIN, 12));
        jLabel4.setForeground(new java.awt.Color(153, 153, 153));

        jLabel5.setText("Appointment ID");
        jLabel5.setBackground(new java.awt.Color(51, 51, 51));
        jLabel5.setFont(new java.awt.Font("Inter 18pt SemiBold", Font.PLAIN, 14));
        jLabel5.setForeground(new java.awt.Color(51, 51, 51));

        jLabel6.setText("Patient Information");
        jLabel6.setBackground(new java.awt.Color(51, 51, 51));
        jLabel6.setFont(new java.awt.Font("Inter 18pt SemiBold", Font.PLAIN, 14));
        jLabel6.setForeground(new java.awt.Color(51, 51, 51));

        jButton1.setText("Search");
        jButton1.setBackground(new java.awt.Color(0, 153, 255));
        jButton1.setFont(new java.awt.Font("Inter 18pt SemiBold", Font.PLAIN, 14));
        jButton1.setForeground(new java.awt.Color(255, 255, 255));

        roundedTextField1.setBackground(new java.awt.Color(255, 255, 255));
        roundedTextField1.setFont(new java.awt.Font("Inter", Font.PLAIN, 12));
        roundedTextField1.setForeground(new java.awt.Color(102, 102, 102));

        jLabel7.setText("Patient ID :");
        jLabel7.setBackground(new java.awt.Color(51, 51, 51));
        jLabel7.setFont(new java.awt.Font("Inter 18pt SemiBold", Font.PLAIN, 14));
        jLabel7.setForeground(new java.awt.Color(51, 51, 51));

        jLabel8.setText("Patient Name :");
        jLabel8.setBackground(new java.awt.Color(51, 51, 51));
        jLabel8.setFont(new java.awt.Font("Inter 18pt SemiBold", Font.PLAIN, 14));
        jLabel8.setForeground(new java.awt.Color(51, 51, 51));

        javax.swing.GroupLayout roundedPanel1Layout = new javax.swing.GroupLayout(roundedPanel1);
        roundedPanel1.setLayout(roundedPanel1Layout);
        roundedPanel1Layout.setHorizontalGroup(
            roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel1Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel5)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3)
                    .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jButton1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(roundedTextField1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)))
                .addGap(14, 14, 14))
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
                .addGap(18, 18, 18)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(roundedPanel1);

        jSplitPane1.setLeftComponent(jPanel2);

        jPanel4.setBackground(new java.awt.Color(247, 247, 247));
        jPanel4.setMaximumSize(new java.awt.Dimension(2147483647, 600));
        jPanel4.setPreferredSize(new java.awt.Dimension(831, 600));
        jPanel4.setLayout(new java.awt.BorderLayout(30, 0));

        roundedPanel2.setBackground(new java.awt.Color(255, 255, 255));
        roundedPanel2.setForeground(new java.awt.Color(234, 234, 234));
        roundedPanel2.setMaximumSize(new java.awt.Dimension(32767, 600));
        roundedPanel2.setMinimumSize(new java.awt.Dimension(0, 600));
        roundedPanel2.setPreferredSize(new java.awt.Dimension(811, 600));

        jLabel9.setText("Prescription History");
        jLabel9.setBackground(new java.awt.Color(0, 0, 0));
        jLabel9.setFont(new java.awt.Font("Inter 18pt SemiBold", Font.PLAIN, 24));
        jLabel9.setForeground(new java.awt.Color(0, 0, 0));

        jLabel10.setText("View patient's complete prescription records");
        jLabel10.setBackground(new java.awt.Color(153, 153, 153));
        jLabel10.setFont(new java.awt.Font("Inter", Font.PLAIN, 12));
        jLabel10.setForeground(new java.awt.Color(153, 153, 153));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Date", "Drug Name", "Dosage", "Quantity"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable1);

        javax.swing.GroupLayout roundedPanel2Layout = new javax.swing.GroupLayout(roundedPanel2);
        roundedPanel2.setLayout(roundedPanel2Layout);
        roundedPanel2Layout.setHorizontalGroup(
            roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel2Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 856, Short.MAX_VALUE)
                    .addGroup(roundedPanel2Layout.createSequentialGroup()
                        .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(20, 20, 20))
        );
        roundedPanel2Layout.setVerticalGroup(
            roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel2Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel9)
                .addGap(12, 12, 12)
                .addComponent(jLabel10)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel4.add(roundedPanel2, java.awt.BorderLayout.CENTER);

        jSplitPane1.setRightComponent(jPanel4);

        jPanel1.add(jSplitPane1, "card2");

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable jTable1;
    private system.ui.components.RoundedPanel roundedPanel1;
    private system.ui.components.RoundedPanel roundedPanel2;
    private system.ui.components.RoundedTextField roundedTextField1;
    // End of variables declaration//GEN-END:variables
}
