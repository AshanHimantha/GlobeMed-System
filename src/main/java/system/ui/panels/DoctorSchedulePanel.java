/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package system.ui.panels;

import com.github.lgooddatepicker.components.TimePicker;
import java.awt.Font;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import system.enums.DayOfWeek;
import system.model.DoctorSchedule;
import system.model.User;
import system.service.ScheduleService;
import system.service.UserService;

/**
 *
 * @author User
 */
public class DoctorSchedulePanel extends javax.swing.JPanel {

    private final UserService userService;
    private final ScheduleService scheduleService;

    // Data maps for linking UI strings to objects
    private User selectedDoctor;
    private List<DoctorSchedule> currentSchedule;

    // Schedule editing components
    private final Map<DayOfWeek, JCheckBox> dayCheckboxes = new HashMap<>();
    private final Map<DayOfWeek, TimePicker> startTimePickers = new HashMap<>();
    private final Map<DayOfWeek, TimePicker> endTimePickers = new HashMap<>();

    public DoctorSchedulePanel() {
        this.userService = new UserService();
        this.scheduleService = new ScheduleService();

        initComponents(); // NetBeans-generated components

        // Configure UI components after they have been created
        configureComponents();

        // Add listeners
        addListeners();

        // Create schedule editing controls
        createScheduleEditingPanel();
    }

    private void configureComponents() {
        // Set up the JTable with the correct columns for schedule display
        DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Day", "Available", "Start Time", "End Time"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Keep table non-editable, we'll use separate controls
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

        // Set placeholder text for doctor search
        roundedTextField1.setText("");

        // Initially disable save button
        jButton2.setEnabled(false);

        // Add padding to roundedPanel2 to pull content away from rounded edges
        roundedPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    private void addListeners() {
        // Listener for the search text field
        roundedTextField1.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { searchDoctors(); }
            public void removeUpdate(DocumentEvent e) { searchDoctors(); }
            public void changedUpdate(DocumentEvent e) { /* Not needed */ }
        });

        // The search button to search doctors
        jButton1.addActionListener(e -> searchDoctors());

        // Save button to save schedule changes
        jButton2.addActionListener(e -> onSaveSchedule());
    }

    private void createScheduleEditingPanel() {
        // Add schedule editing controls to the right panel below the table
        javax.swing.JPanel editPanel = new javax.swing.JPanel();
        editPanel.setLayout(new java.awt.GridBagLayout());
        editPanel.setBackground(new java.awt.Color(255, 255, 255));
        editPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
            javax.swing.BorderFactory.createEtchedBorder(),
            "Edit Schedule",
            javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
            javax.swing.border.TitledBorder.DEFAULT_POSITION,
            new java.awt.Font("Inter 18pt SemiBold", Font.PLAIN, 16)
        ));

        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);
        gbc.anchor = java.awt.GridBagConstraints.WEST;

        // Create editing controls for each day
        int row = 0;
        for (DayOfWeek day : DayOfWeek.values()) {
            gbc.gridy = row;

            // Day label
            gbc.gridx = 0;
            gbc.weightx = 0.2;
            javax.swing.JLabel dayLabel = new javax.swing.JLabel(day.toString());
            dayLabel.setFont(new Font("Inter", Font.BOLD, 12));
            editPanel.add(dayLabel, gbc);

            // Available checkbox
            gbc.gridx = 1;
            gbc.weightx = 0.1;
            JCheckBox availableCheckbox = new JCheckBox("Available");
            availableCheckbox.setBackground(new java.awt.Color(255, 255, 255));
            dayCheckboxes.put(day, availableCheckbox);
            editPanel.add(availableCheckbox, gbc);

            // Start time picker
            gbc.gridx = 2;
            gbc.weightx = 0.3;
            TimePicker startTimePicker = new TimePicker();
            startTimePicker.setTime(LocalTime.of(9, 0)); // Default 9 AM
            startTimePickers.put(day, startTimePicker);
            editPanel.add(startTimePicker, gbc);

            // "to" label
            gbc.gridx = 3;
            gbc.weightx = 0.05;
            editPanel.add(new javax.swing.JLabel("to"), gbc);

            // End time picker
            gbc.gridx = 4;
            gbc.weightx = 0.3;
            TimePicker endTimePicker = new TimePicker();
            endTimePicker.setTime(LocalTime.of(17, 0)); // Default 5 PM
            endTimePickers.put(day, endTimePicker);
            editPanel.add(endTimePicker, gbc);

            // Add listener to checkbox to enable/disable time pickers
            availableCheckbox.addActionListener(e -> {
                boolean isAvailable = availableCheckbox.isSelected();
                startTimePicker.setEnabled(isAvailable);
                endTimePicker.setEnabled(isAvailable);
                updateScheduleDisplay(); // Update the table display
            });

            // Initially disable time pickers
            startTimePicker.setEnabled(false);
            endTimePicker.setEnabled(false);

            row++;
        }

        // Add the edit panel to the right side below the table
        roundedPanel2.add(editPanel, java.awt.BorderLayout.SOUTH);
    }

    private void searchDoctors() {
        String searchText = roundedTextField1.getText().trim();
        DefaultTableModel tableModel = (DefaultTableModel) jTable1.getModel();
        tableModel.setRowCount(0); // Clear the table

        // Reset doctor info labels
        jLabel7.setText("Doctor Username :");
        jLabel8.setText("Doctor Name :");

        if (searchText.isEmpty()) {
            clearScheduleEditing();
            return; // Don't search if field is empty
        }

        List<User> doctors = userService.searchDoctorsByName(searchText, 10);

        if (doctors.isEmpty()) {
            tableModel.addRow(new Object[]{"No doctors found matching: " + searchText, "", "", ""});
            clearScheduleEditing();
        } else {
            // Display first doctor's info and schedule
            User firstDoctor = doctors.get(0);
            onDoctorSelected(firstDoctor);
        }
    }

    private void onDoctorSelected(User selectedDoctor) {
        DefaultTableModel tableModel = (DefaultTableModel) jTable1.getModel();
        tableModel.setRowCount(0); // Clear the table

        if (selectedDoctor == null) {
            clearScheduleEditing();
            return;
        }

        this.selectedDoctor = selectedDoctor;

        // Update the info labels with actual data
        jLabel7.setText("Doctor Username : " + selectedDoctor.getUsername());
        jLabel8.setText("Doctor Name : Dr. " + selectedDoctor.getFirstName() + " " + selectedDoctor.getLastName());

        // Fetch and display the schedule for the selected doctor
        this.currentSchedule = scheduleService.getScheduleForDoctor(selectedDoctor);

        if (currentSchedule.isEmpty()) {
            // Create default schedule for all days
            currentSchedule = createDefaultSchedule(selectedDoctor);
        }

        // Populate both the table and editing controls
        populateScheduleData();

        jButton2.setEnabled(true); // Enable save button
    }

    private void populateScheduleData() {
        DefaultTableModel tableModel = (DefaultTableModel) jTable1.getModel();
        tableModel.setRowCount(0); // Clear the table

        // Populate the table and editing controls with schedule data
        for (DoctorSchedule schedule : currentSchedule) {
            DayOfWeek day = schedule.getDayOfWeek();

            // Update table
            tableModel.addRow(new Object[]{
                day.toString(),
                schedule.isAvailable() ? "Yes" : "No",
                schedule.getStartTime() != null ? schedule.getStartTime().toString() : "Not Set",
                schedule.getEndTime() != null ? schedule.getEndTime().toString() : "Not Set"
            });

            // Update editing controls
            JCheckBox checkbox = dayCheckboxes.get(day);
            TimePicker startPicker = startTimePickers.get(day);
            TimePicker endPicker = endTimePickers.get(day);

            checkbox.setSelected(schedule.isAvailable());
            if (schedule.getStartTime() != null) {
                startPicker.setTime(schedule.getStartTime());
            }
            if (schedule.getEndTime() != null) {
                endPicker.setTime(schedule.getEndTime());
            }

            // Enable/disable time pickers based on availability
            startPicker.setEnabled(schedule.isAvailable());
            endPicker.setEnabled(schedule.isAvailable());
        }
    }

    private void updateScheduleDisplay() {
        if (selectedDoctor == null || currentSchedule == null) return;

        // Update the schedule objects with current UI values
        for (DoctorSchedule schedule : currentSchedule) {
            DayOfWeek day = schedule.getDayOfWeek();
            JCheckBox checkbox = dayCheckboxes.get(day);
            TimePicker startPicker = startTimePickers.get(day);
            TimePicker endPicker = endTimePickers.get(day);

            schedule.setAvailable(checkbox.isSelected());
            if (checkbox.isSelected()) {
                schedule.setStartTime(startPicker.getTime());
                schedule.setEndTime(endPicker.getTime());
            }
        }

        // Refresh the table display
        DefaultTableModel tableModel = (DefaultTableModel) jTable1.getModel();
        tableModel.setRowCount(0);

        for (DoctorSchedule schedule : currentSchedule) {
            tableModel.addRow(new Object[]{
                schedule.getDayOfWeek().toString(),
                schedule.isAvailable() ? "Yes" : "No",
                schedule.getStartTime() != null ? schedule.getStartTime().toString() : "Not Set",
                schedule.getEndTime() != null ? schedule.getEndTime().toString() : "Not Set"
            });
        }
    }

    private void clearScheduleEditing() {
        selectedDoctor = null;
        currentSchedule = null;
        jButton2.setEnabled(false);

        // Clear and disable all editing controls
        for (DayOfWeek day : DayOfWeek.values()) {
            dayCheckboxes.get(day).setSelected(false);
            startTimePickers.get(day).setEnabled(false);
            endTimePickers.get(day).setEnabled(false);
        }
    }

    private List<DoctorSchedule> createDefaultSchedule(User doctor) {
        List<DoctorSchedule> defaultSchedule = new ArrayList<>();
        for (DayOfWeek day : DayOfWeek.values()) {
            DoctorSchedule schedule = new DoctorSchedule();
            schedule.setDoctor(doctor);
            schedule.setDayOfWeek(day);
            schedule.setAvailable(false);
            schedule.setStartTime(LocalTime.of(9, 0)); // Default 9 AM
            schedule.setEndTime(LocalTime.of(17, 0));  // Default 5 PM
            defaultSchedule.add(schedule);
        }
        return defaultSchedule;
    }

    private void onSaveSchedule() {
        if (selectedDoctor == null || currentSchedule == null) {
            JOptionPane.showMessageDialog(this, "Please select a doctor first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Validate schedule before saving
        if (!validateSchedule()) {
            return;
        }

        // Update schedule with current UI values
        updateScheduleDisplay();

        boolean success = scheduleService.saveFullSchedule(currentSchedule);

        if (success) {
            JOptionPane.showMessageDialog(this,
                "Schedule for Dr. " + selectedDoctor.getLastName() + " saved successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            // Refresh the display
            populateScheduleData();
        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to save the schedule.",
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validateSchedule() {
        for (DayOfWeek day : DayOfWeek.values()) {
            JCheckBox checkbox = dayCheckboxes.get(day);
            if (checkbox.isSelected()) {
                TimePicker startPicker = startTimePickers.get(day);
                TimePicker endPicker = endTimePickers.get(day);

                LocalTime startTime = startPicker.getTime();
                LocalTime endTime = endPicker.getTime();

                if (startTime == null || endTime == null) {
                    JOptionPane.showMessageDialog(this,
                        "Please set both start and end times for " + day.toString(),
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                    return false;
                }

                if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
                    JOptionPane.showMessageDialog(this,
                        "Start time must be before end time for " + day.toString(),
                        "Validation Error",
                        JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            }
        }
        return true;
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
