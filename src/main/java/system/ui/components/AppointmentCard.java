/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package system.ui.components;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.ImageIcon;
import com.github.lgooddatepicker.components.DateTimePicker;
import system.model.Appointment;
import system.service.AppointmentService;

/**
 *
 * @author User
 */
public class AppointmentCard extends javax.swing.JPanel {

    private Appointment appointment;
    private AppointmentService appointmentService;
    private Runnable onStatusChangeCallback;

    /**
     * Creates new form AppointmentCard
     */
    public AppointmentCard() {
        initComponents();
        this.appointmentService = new AppointmentService();
        setupImprovedClickHandlers();
    }

    /**
     * Creates new form AppointmentCard with appointment data
     */
    public AppointmentCard(Appointment appointment) {
        this();
        this.appointment = appointment;
        populateData();
        updateCardForStatus();
    }

    /**
     * Creates new form AppointmentCard with appointment data and callback
     */
    public AppointmentCard(Appointment appointment, Runnable onStatusChangeCallback) {
        this(appointment);
        this.onStatusChangeCallback = onStatusChangeCallback;
    }

    private void setupImprovedClickHandlers() {
        // Simple edit button click handler - no hover effects
        edit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (appointment != null && "SCHEDULED".equals(appointment.getStatus())) {
                    showEditDialog();
                }
            }
        });

        // Simple cancel button click handler - no hover effects
        cancel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (appointment != null && "SCHEDULED".equals(appointment.getStatus())) {
                    cancelAppointment();
                }
            }
        });
    }

    private void updateCardForStatus() {
        if (appointment == null) return;

        String status = appointment.getStatus().toUpperCase();

        switch (status) {
            case "SCHEDULED":
                appointment_status.setText("Mark Complete");
                appointment_status.setToolTipText("Click to mark this appointment as completed");
                appointment_status.setEnabled(true);
                appointment_status.setBackground(new java.awt.Color(0, 153, 255)); // Blue for scheduled

                // Show edit and cancel options
                edit.setVisible(true);
                cancel.setVisible(true);
                edit.setText("Edit");
                cancel.setText("Cancel");
                break;

            case "COMPLETED":
                appointment_status.setText("Completed");
                appointment_status.setToolTipText("This appointment has been completed");
                appointment_status.setEnabled(true);
                appointment_status.setBackground(new java.awt.Color(40, 167, 69)); // Green for completed
                appointment_status.setOpaque(false); // Ensure background color shows even when disabled
                appointment_status.setForeground(new java.awt.Color(255, 255, 255)); // Keep white text

                // Hide edit/cancel for completed appointments
                edit.setVisible(false);
                cancel.setVisible(false);
                break;

            case "CANCELLED":
                appointment_status.setText("Cancelled");
                appointment_status.setToolTipText("This appointment has been cancelled");
                appointment_status.setEnabled(true);
                appointment_status.setBackground(new java.awt.Color(220, 53, 69)); // Red for cancelled
                appointment_status.setOpaque(false); // Ensure background color shows even when disabled
                appointment_status.setForeground(new java.awt.Color(255, 255, 255)); // Keep white text

                // Hide edit/cancel for cancelled appointments
                edit.setVisible(false);
                cancel.setVisible(false);
                break;
        }

        // Force repaint to ensure visual changes are applied immediately
        appointment_status.repaint();
    }

    private void showEditDialog() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog editDialog = new JDialog(parentFrame, "Edit Appointment Time", true);
        editDialog.setSize(400, 200);
        editDialog.setLocationRelativeTo(this);
        editDialog.setLayout(new java.awt.BorderLayout());

        // Create content panel
        javax.swing.JPanel contentPanel = new javax.swing.JPanel();
        contentPanel.setLayout(new java.awt.GridBagLayout());
        contentPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));

        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);

        // Title
        JLabel titleLabel = new JLabel("Change Appointment Time");
        titleLabel.setFont(new java.awt.Font("Inter 18pt SemiBold", 0, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        contentPanel.add(titleLabel, gbc);

        // Current time info
        JLabel currentTimeLabel = new JLabel("Current: " + appointment.getAppointmentDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        currentTimeLabel.setFont(new java.awt.Font("Inter", 0, 12));
        currentTimeLabel.setForeground(new java.awt.Color(102, 102, 102));
        gbc.gridy = 1;
        contentPanel.add(currentTimeLabel, gbc);

        // New time picker
        DateTimePicker newTimePicker = new DateTimePicker();
        newTimePicker.setDateTimePermissive(appointment.getAppointmentDateTime());
        gbc.gridy = 2; gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        contentPanel.add(newTimePicker, gbc);

        // Buttons panel
        javax.swing.JPanel buttonsPanel = new javax.swing.JPanel();
        buttonsPanel.setLayout(new java.awt.FlowLayout());

        JButton saveButton = new JButton("Save Changes");
        saveButton.setBackground(new java.awt.Color(0, 153, 255));
        saveButton.setForeground(java.awt.Color.WHITE);
        saveButton.setFont(new java.awt.Font("Inter", 0, 12));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new java.awt.Color(108, 117, 125));
        cancelButton.setForeground(java.awt.Color.WHITE);
        cancelButton.setFont(new java.awt.Font("Inter", 0, 12));

        saveButton.addActionListener(e -> {
            LocalDateTime newDateTime = newTimePicker.getDateTimePermissive();
            if (newDateTime != null) {
                updateAppointmentTime(newDateTime);
                editDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(editDialog, "Please select a valid date and time", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> editDialog.dispose());

        buttonsPanel.add(saveButton);
        buttonsPanel.add(cancelButton);

        gbc.gridy = 3; gbc.gridwidth = 2;
        contentPanel.add(buttonsPanel, gbc);

        editDialog.add(contentPanel);
        editDialog.setVisible(true);
    }

    private void updateAppointmentTime(LocalDateTime newDateTime) {
        try {
            // Validate new time is not in the past
            if (newDateTime.isBefore(LocalDateTime.now())) {
                JOptionPane.showMessageDialog(this, "Cannot schedule appointment in the past", "Invalid Time", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Check doctor availability at new time
            if (!appointmentService.isDoctorAvailable(appointment.getDoctor(), newDateTime)) {
                var conflictingAppointments = appointmentService.getConflictingAppointments(appointment.getDoctor(), newDateTime);

                if (!conflictingAppointments.isEmpty()) {
                    StringBuilder conflictMessage = new StringBuilder();
                    conflictMessage.append("Doctor is not available at the selected time.\n");
                    conflictMessage.append("Appointments must have at least 10 minutes gap between them.\n\n");
                    conflictMessage.append("Conflicting appointments:\n");

                    DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("HH:mm");
                    for (Appointment conflict : conflictingAppointments) {
                        // Skip the current appointment being edited
                        if (!conflict.getId().equals(appointment.getId())) {
                            conflictMessage.append("• ")
                                         .append(conflict.getAppointmentDateTime().format(displayFormatter))
                                         .append(" - ")
                                         .append(conflict.getPatient().getName())
                                         .append("\n");
                        }
                    }

                    conflictMessage.append("\nPlease select a different time slot.");
                    JOptionPane.showMessageDialog(this, conflictMessage.toString(), "Scheduling Conflict", JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Doctor is not available at the selected time.\nAppointments must have at least 10 minutes gap between them.", "Scheduling Conflict", JOptionPane.WARNING_MESSAGE);
                }
                return;
            }

            // Update appointment in database
            boolean updated = appointmentService.updateAppointmentTime(appointment.getId(), newDateTime);
            if (updated) {
                appointment.setAppointmentDateTime(newDateTime);
                populateData(); // Refresh the display

                JOptionPane.showMessageDialog(this, "Appointment time updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

                // Call callback if provided (to refresh the parent list)
                if (onStatusChangeCallback != null) {
                    onStatusChangeCallback.run();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update appointment time", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while updating the appointment: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelAppointment() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to cancel this appointment?\n\nPatient: " + appointment.getPatient().getName() +
            "\nTime: " + appointment.getAppointmentDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
            "Cancel Appointment",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            boolean cancelled = appointmentService.updateAppointmentStatus(appointment.getId(), "CANCELLED");
            if (cancelled) {
                appointment.setStatus("CANCELLED");
                updateCardForStatus(); // Use the new method instead of updateStatusButtonColor()

                JOptionPane.showMessageDialog(this, "Appointment cancelled successfully", "Success", JOptionPane.INFORMATION_MESSAGE);

                // Call callback if provided (to refresh the parent list)
                if (onStatusChangeCallback != null) {
                    onStatusChangeCallback.run();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to cancel appointment", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void populateData() {
        if (appointment != null) {
            // Format time (HH:mm)
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            time.setText(appointment.getAppointmentDateTime().format(timeFormatter));

            // Format date (yyyy.MM.dd)
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            date.setText(appointment.getAppointmentDateTime().format(dateFormatter));

            // Set patient name
            patient_name.setText(appointment.getPatient().getName());

            // Set doctor name
            drname.setText("Dr. " + appointment.getDoctor().getFirstName() + " " + appointment.getDoctor().getLastName());

            // Set patient ID
            id.setText("ID : " + appointment.getPatient().getPatientId());

            // Update the card for current status (color, visibility, text)
            updateCardForStatus();
        }
    }

    private void updateStatusButtonColor() {
        if (appointment != null) {
            switch (appointment.getStatus().toUpperCase()) {
                case "SCHEDULED":
                    appointment_status.setBackground(new java.awt.Color(0, 153, 255)); // Blue
                    break;
                case "COMPLETED":
                    appointment_status.setBackground(new java.awt.Color(40, 167, 69)); // Green
                    break;
                case "CANCELLED":
                    appointment_status.setBackground(new java.awt.Color(220, 53, 69)); // Red
                    break;
                default:
                    appointment_status.setBackground(new java.awt.Color(108, 117, 125)); // Gray
                    break;
            }
        }
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
        populateData();
    }

    public Appointment getAppointment() {
        return appointment;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        roundedPanel1 = new system.ui.components.RoundedPanel();
        time = new javax.swing.JLabel();
        date = new javax.swing.JLabel();
        patient_name = new javax.swing.JLabel();
        drname = new javax.swing.JLabel();
        id = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        edit = new javax.swing.JLabel();
        cancel = new javax.swing.JLabel();
        appointment_status = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 10, 1));

        roundedPanel1.setBackground(new java.awt.Color(247, 247, 247));
        roundedPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 10, 1));
        roundedPanel1.setForeground(new java.awt.Color(224, 224, 224));
        roundedPanel1.setMaximumSize(new java.awt.Dimension(32767, 106));
        roundedPanel1.setMinimumSize(new java.awt.Dimension(100, 106));
        roundedPanel1.setPreferredSize(new java.awt.Dimension(707, 106));

        time.setFont(new java.awt.Font("Inter 18pt SemiBold", 0, 24)); // NOI18N
        time.setForeground(new java.awt.Color(0, 153, 255));
        time.setText("09:00");

        date.setFont(new java.awt.Font("Inter 18pt Medium", 0, 10)); // NOI18N
        date.setForeground(new java.awt.Color(153, 153, 153));
        date.setText("2025.10.24");

        patient_name.setFont(new java.awt.Font("Inter 18pt Medium", 0, 18)); // NOI18N
        patient_name.setForeground(new java.awt.Color(51, 51, 51));
        patient_name.setText("Ashan Himantha");

        drname.setFont(new java.awt.Font("Inter 18pt Medium", 0, 12)); // NOI18N
        drname.setForeground(new java.awt.Color(153, 153, 153));
        drname.setText("Dr. Ashan Himantha");

        id.setFont(new java.awt.Font("Inter 18pt Medium", 0, 12)); // NOI18N
        id.setForeground(new java.awt.Color(153, 153, 153));
        id.setText("ID : P001");

        jPanel1.setBackground(new java.awt.Color(247, 247, 247));

        edit.setFont(new java.awt.Font("Inter 18pt Medium", 0, 14)); // NOI18N
        edit.setForeground(new java.awt.Color(102, 102, 102));
        edit.setText("Edit");

        cancel.setFont(new java.awt.Font("Inter 18pt Medium", 0, 14)); // NOI18N
        cancel.setForeground(new java.awt.Color(255, 51, 51));
        cancel.setText("Cancel");

        appointment_status.setBackground(new java.awt.Color(0, 153, 255));
        appointment_status.setFont(new java.awt.Font("Inter 18pt Medium", 0, 12)); // NOI18N
        appointment_status.setForeground(new java.awt.Color(255, 255, 255));
        appointment_status.setText("Scheduled");
        appointment_status.setBorderPainted(false);
        appointment_status.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                appointment_statusActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addComponent(appointment_status)
                .addGap(24, 24, 24)
                .addComponent(edit)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cancel)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 15, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(edit)
                    .addComponent(cancel)
                    .addComponent(appointment_status)))
        );

        javax.swing.GroupLayout roundedPanel1Layout = new javax.swing.GroupLayout(roundedPanel1);
        roundedPanel1.setLayout(roundedPanel1Layout);
        roundedPanel1Layout.setHorizontalGroup(
            roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(time)
                    .addGroup(roundedPanel1Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(date)))
                .addGap(31, 31, 31)
                .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundedPanel1Layout.createSequentialGroup()
                        .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(drname)
                            .addComponent(patient_name))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 203, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18))
                    .addGroup(roundedPanel1Layout.createSequentialGroup()
                        .addComponent(id)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        roundedPanel1Layout.setVerticalGroup(
            roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel1Layout.createSequentialGroup()
                .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundedPanel1Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(time)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(date))
                    .addGroup(roundedPanel1Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(roundedPanel1Layout.createSequentialGroup()
                                .addComponent(patient_name)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(drname))
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(id, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(roundedPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 705, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(roundedPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void appointment_statusActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_appointment_statusActionPerformed
        if (appointment != null && "SCHEDULED".equals(appointment.getStatus())) {
            // Show confirmation dialog for completing appointment
            int result = JOptionPane.showConfirmDialog(
                this,
                "Mark this appointment as completed?\n\nPatient: " + appointment.getPatient().getName() +
                "\nTime: " + appointment.getAppointmentDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")),
                "Complete Appointment",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );

            if (result == JOptionPane.YES_OPTION) {
                // Update to completed status
                boolean updated = appointmentService.updateAppointmentStatus(appointment.getId(), "COMPLETED");
                if (updated) {
                    appointment.setStatus("COMPLETED");
                    updateCardForStatus(); // Use the new method instead

                    JOptionPane.showMessageDialog(this, "Appointment marked as completed!", "Success", JOptionPane.INFORMATION_MESSAGE);

                    // Call callback if provided (to refresh the parent list)
                    if (onStatusChangeCallback != null) {
                        onStatusChangeCallback.run();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to update appointment status", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }//GEN-LAST:event_appointment_statusActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton appointment_status;
    private javax.swing.JLabel cancel;
    private javax.swing.JLabel date;
    private javax.swing.JLabel drname;
    private javax.swing.JLabel edit;
    private javax.swing.JLabel id;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel patient_name;
    private system.ui.components.RoundedPanel roundedPanel1;
    private javax.swing.JLabel time;
    // End of variables declaration//GEN-END:variables
}
