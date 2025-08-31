/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package system.ui.panels;

import com.github.lgooddatepicker.components.TimePicker;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import system.enums.DayOfWeek;
import system.model.DoctorSchedule;
import system.model.Facility;
import system.model.User;
import system.service.FacilityService;
import system.service.ScheduleService;
import system.service.UserService;

/**
 *
 * @author User
 */
public class DoctorSchedulePanel extends javax.swing.JPanel {

    private final UserService userService;
    private final ScheduleService scheduleService;
    private final FacilityService facilityService;
    private User selectedDoctor;
    private Facility selectedFacility;
    private List<DoctorSchedule> currentSchedule;
    private final Map<String, Facility> facilityMap = new HashMap<>();

    private final Map<DayOfWeek, JCheckBox> dayCheckboxes = new HashMap<>();
    private final Map<DayOfWeek, TimePicker> startTimePickers = new HashMap<>();
    private final Map<DayOfWeek, TimePicker> endTimePickers = new HashMap<>();

    public DoctorSchedulePanel() {
        this.userService = new UserService();
        this.scheduleService = new ScheduleService();
        this.facilityService = new FacilityService();

        initComponents();
        configureComponents();
        addListeners();
        createScheduleEditingPanel();
        loadFacilities();
    }

    private void configureComponents() {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Day", "Available", "Start Time", "End Time"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        jTable1.setModel(model);
        jTable1.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        roundedPanel2.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        jButton2.setEnabled(false); // Disable save button initially
    }
    
    private void loadFacilities() {
        List<Facility> facilities = facilityService.getAllFacilities();
        jComboBox1.removeAllItems(); // Clear existing items
        jComboBox1.addItem("Select a Facility..."); // Add placeholder
        for(Facility f : facilities) {
            facilityMap.put(f.getName(), f);
            jComboBox1.addItem(f.getName());
        }
    }

    private void addListeners() {
        jButton1.addActionListener(e -> searchDoctors());
        jButton2.addActionListener(e -> onSaveSchedule());
        jComboBox1.addActionListener(e -> onSelectionChange());
        
        roundedTextField1.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { searchDoctors(); }
            public void removeUpdate(DocumentEvent e) { searchDoctors(); }
            public void changedUpdate(DocumentEvent e) {}
        });
    }

    private void createScheduleEditingPanel() {
        JPanel editPanel = new JPanel(new GridBagLayout());
        editPanel.setOpaque(false);
        editPanel.setBorder(BorderFactory.createTitledBorder("Edit Schedule Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        for (DayOfWeek day : DayOfWeek.values()) {
            gbc.gridy = row++;
            gbc.gridx = 0; editPanel.add(new JLabel(day.toString()), gbc);
            gbc.gridx = 1; JCheckBox cb = new JCheckBox("Available"); dayCheckboxes.put(day, cb); editPanel.add(cb, gbc);
            gbc.gridx = 2; TimePicker start = new TimePicker(); startTimePickers.put(day, start); editPanel.add(start, gbc);
            gbc.gridx = 3; editPanel.add(new JLabel("to"), gbc);
            gbc.gridx = 4; TimePicker end = new TimePicker(); endTimePickers.put(day, end); editPanel.add(end, gbc);
            
            cb.addActionListener(e -> {
                boolean isSelected = cb.isSelected();
                startTimePickers.get(day).setEnabled(isSelected);
                endTimePickers.get(day).setEnabled(isSelected);
            });
        }
        roundedPanel2.add(editPanel, BorderLayout.SOUTH);
    }

    private void searchDoctors() {
        String searchText = roundedTextField1.getText().trim();
        clearSchedulePanel();
        if (searchText.isEmpty()) return;
        List<User> doctors = userService.searchDoctorsByName(searchText, 1);
        if (!doctors.isEmpty()) {
            onDoctorSelected(doctors.get(0));
        }
    }
    
    private void onDoctorSelected(User doctor) {
        this.selectedDoctor = doctor;
        jLabel7.setText("Doctor Username: " + doctor.getUsername());
        jLabel8.setText("Doctor Name: Dr. " + doctor.getFirstName() + " " + doctor.getLastName());
        onSelectionChange(); // Trigger schedule fetch
    }

    private void onSelectionChange() {
        String selectedFacilityStr = (String) jComboBox1.getSelectedItem();
        if (selectedDoctor == null || selectedFacilityStr == null || selectedFacilityStr.equals("Select a Facility...")) {
            clearSchedulePanel();
            return;
        }
        
        this.selectedFacility = facilityMap.get(selectedFacilityStr);
        if (this.selectedFacility == null) return;
        
        this.currentSchedule = scheduleService.getScheduleForDoctorAtFacility(this.selectedDoctor, this.selectedFacility);
        populateScheduleData();
        jButton2.setEnabled(true);
    }
    
    private void populateScheduleData() {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        for (DoctorSchedule schedule : currentSchedule) {
            DayOfWeek day = schedule.getDayOfWeek();
            model.addRow(new Object[]{day, schedule.isAvailable() ? "Yes" : "No", 
                schedule.isAvailable() ? schedule.getStartTime() : "N/A", 
                schedule.isAvailable() ? schedule.getEndTime() : "N/A"});

            dayCheckboxes.get(day).setSelected(schedule.isAvailable());
            startTimePickers.get(day).setTime(schedule.getStartTime());
            endTimePickers.get(day).setTime(schedule.getEndTime());
            startTimePickers.get(day).setEnabled(schedule.isAvailable());
            endTimePickers.get(day).setEnabled(schedule.isAvailable());
        }
    }

    private void clearSchedulePanel() {
        ((DefaultTableModel) jTable1.getModel()).setRowCount(0);
        for (DayOfWeek day : DayOfWeek.values()) {
            dayCheckboxes.get(day).setSelected(false);
            startTimePickers.get(day).setEnabled(false);
            endTimePickers.get(day).setEnabled(false);
        }
        jButton2.setEnabled(false);
    }

    private void onSaveSchedule() {
        if (selectedDoctor == null || currentSchedule == null) return;
        
        for (DoctorSchedule schedule : currentSchedule) {
            DayOfWeek day = schedule.getDayOfWeek();
            schedule.setAvailable(dayCheckboxes.get(day).isSelected());
            schedule.setStartTime(startTimePickers.get(day).getTime());
            schedule.setEndTime(endTimePickers.get(day).getTime());
        }
        
        if (scheduleService.saveFullSchedule(currentSchedule)) {
            JOptionPane.showMessageDialog(this, "Schedule saved successfully!");
            populateScheduleData(); // Refresh table
        } else {
            JOptionPane.showMessageDialog(this, "Failed to save schedule.", "Error", JOptionPane.ERROR_MESSAGE);
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
        jButton2 = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox<>();
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

        jLabel1.setText("Doctor Schedule Management");
        jLabel1.setFont(new java.awt.Font("Inter 18pt", Font.BOLD, 24));
        jLabel1.setForeground(new java.awt.Color(5, 5, 5));

        jLabel2.setText("Search for doctors to view and manage their weekly schedules with real-time editing.");
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

        jLabel3.setText("Search Doctor");
        jLabel3.setBackground(new java.awt.Color(0, 0, 0));
        jLabel3.setFont(new java.awt.Font("Inter 18pt SemiBold", Font.PLAIN, 24));
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));

        jLabel4.setText("Enter doctor name to search");
        jLabel4.setBackground(new java.awt.Color(153, 153, 153));
        jLabel4.setFont(new java.awt.Font("Inter", Font.PLAIN, 12));
        jLabel4.setForeground(new java.awt.Color(153, 153, 153));

        jLabel5.setText("Doctor Name");
        jLabel5.setBackground(new java.awt.Color(51, 51, 51));
        jLabel5.setFont(new java.awt.Font("Inter 18pt SemiBold", Font.PLAIN, 14));
        jLabel5.setForeground(new java.awt.Color(51, 51, 51));

        jLabel6.setText("Doctor Information");
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

        jLabel7.setText("Doctor Username :");
        jLabel7.setBackground(new java.awt.Color(51, 51, 51));
        jLabel7.setFont(new java.awt.Font("Inter 18pt SemiBold", Font.PLAIN, 14));
        jLabel7.setForeground(new java.awt.Color(51, 51, 51));

        jLabel8.setText("Doctor Name :");
        jLabel8.setBackground(new java.awt.Color(51, 51, 51));
        jLabel8.setFont(new java.awt.Font("Inter 18pt SemiBold", Font.PLAIN, 14));
        jLabel8.setForeground(new java.awt.Color(51, 51, 51));

        jButton2.setText("Save Schedule");
        jButton2.setBackground(new java.awt.Color(0, 204, 102));
        jButton2.setFont(new java.awt.Font("Inter 18pt SemiBold", Font.PLAIN, 14));
        jButton2.setForeground(new java.awt.Color(255, 255, 255));

        jComboBox1.setFont(new java.awt.Font("Inter", Font.PLAIN, 12));
        jComboBox1.setBackground(new java.awt.Color(255, 255, 255));

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
                        .addComponent(jButton2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(roundedTextField1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8)
                .addGap(18, 18, 18)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.add(roundedPanel1);

        jSplitPane1.setLeftComponent(jPanel2);

        roundedPanel2.setBackground(new java.awt.Color(255, 255, 255));
        roundedPanel2.setForeground(new java.awt.Color(234, 234, 234));
        roundedPanel2.setMaximumSize(new java.awt.Dimension(32767, 600));
        roundedPanel2.setMinimumSize(new java.awt.Dimension(0, 600));
        roundedPanel2.setPreferredSize(new java.awt.Dimension(811, 600));
        roundedPanel2.setLayout(new java.awt.BorderLayout());

        jLabel9.setText("Weekly Schedule");
        jLabel9.setBackground(new java.awt.Color(0, 0, 0));
        jLabel9.setFont(new java.awt.Font("Inter 18pt SemiBold", Font.PLAIN, 24));
        jLabel9.setForeground(new java.awt.Color(0, 0, 0));

        jLabel10.setText("View current schedule and edit availability times below");
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
                "Day", "Available", "Start Time", "End Time"
            }
        ) {
            final boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable1);

        javax.swing.JPanel topPanel = new javax.swing.JPanel();
        topPanel.setLayout(new javax.swing.BoxLayout(topPanel, javax.swing.BoxLayout.Y_AXIS));
        topPanel.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.JPanel headerPanel = new javax.swing.JPanel();
        headerPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        headerPanel.setBackground(new java.awt.Color(255, 255, 255));
        headerPanel.add(jLabel9);

        javax.swing.JPanel descPanel = new javax.swing.JPanel();
        descPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        descPanel.setBackground(new java.awt.Color(255, 255, 255));
        descPanel.add(jLabel10);

        topPanel.add(headerPanel);
        topPanel.add(descPanel);

        roundedPanel2.add(topPanel, java.awt.BorderLayout.NORTH);
        roundedPanel2.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jSplitPane1.setRightComponent(roundedPanel2);

        jPanel1.add(jSplitPane1, "card2");

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JComboBox<String> jComboBox1;
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
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable jTable1;
    private system.ui.components.RoundedPanel roundedPanel1;
    private system.ui.components.RoundedPanel roundedPanel2;
    private system.ui.components.RoundedTextField roundedTextField1;
    // End of variables declaration//GEN-END:variables
}
