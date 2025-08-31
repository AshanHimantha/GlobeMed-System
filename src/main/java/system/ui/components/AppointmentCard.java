package system.ui.components;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import com.github.lgooddatepicker.components.DateTimePicker;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import system.enums.AppointmentType;
import system.enums.PaymentMethod;
import system.enums.UserRole;
import system.model.Appointment;
import system.model.User;
import system.service.AppointmentService;
import system.service.AuthenticationService;

/**
 *
 * @author User
 */
public class AppointmentCard extends javax.swing.JPanel {

    private Appointment appointment;
    private final AppointmentService appointmentService;
    private Runnable onDataChangeCallback; // A callback to refresh the parent list
    private  User currentUser;
    
    public AppointmentCard() {
        initComponents();
        this.appointmentService = new AppointmentService();
    }

 public AppointmentCard(Appointment appointment, User currentUser, Runnable onDataChangeCallback) {
        initComponents();
        this.appointmentService = new AppointmentService();
        this.appointment = appointment;
        this.currentUser = currentUser;
        this.onDataChangeCallback = onDataChangeCallback;

        setupManualClickHandlers();
        populateData();
    }


   
private void setupManualClickHandlers() {
        // Designer handles 'edit' and 'appointment_status'. We handle labels here.
        pay.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (pay.isVisible()) processPayment();
            }
        });

        cancel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (cancel.isVisible()) cancelAppointment();
            }
        });
    }


  private void populateData() {
        if (appointment == null) return;
        
        time.setText(appointment.getAppointmentDateTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        date.setText(appointment.getAppointmentDateTime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")));
        patient_name.setText(appointment.getPatient().getName());
        id.setText("ID : " + appointment.getPatient().getPatientId());
        type.setText(appointment.getType().toString());

        switch (appointment.getType()) {
            case CONSULTATION:
            case SURGERY:
                User doctor = appointment.getDoctor();
                drname.setText("Dr. " + (doctor != null ? doctor.getFirstName() + " " + doctor.getLastName() : "N/A"));
                docicon.setIcon(loadImageIcon(appointment.getType() == AppointmentType.SURGERY ? "/img/surgery.png" : "/img/stethoscope.png"));
                break;
            case DIAGNOSTIC:
                drname.setText(appointment.getServiceName());
                docicon.setIcon(loadImageIcon("/img/microscope.png"));
                break;
            default:
                drname.setText(appointment.getServiceName());
                docicon.setVisible(false);
                break;
        }
        updateCardVisualState();
  }
 
  private ImageIcon loadImageIcon(String path) {
        java.net.URL imageUrl = getClass().getResource(path);
        if (imageUrl != null) return new ImageIcon(imageUrl);
        System.err.println("Could not find image icon at path: " + path);
        return null;
    }
  
  
private void updateCardVisualState() {
        if (appointment == null || currentUser == null) return;
        String status = appointment.getStatus().toUpperCase();
        UserRole role = currentUser.getRole();
        AppointmentType apptType = appointment.getType();

        pay.setVisible(false);
        edit.setVisible(false);
        cancel.setVisible(false);
        appointment_status.setEnabled(true);

        switch (status) {
            case "PENDING_PAYMENT":
                appointment_status.setText("Process Payment");
                appointment_status.setToolTipText("Click to process payment");
                appointment_status.setBackground(new java.awt.Color(255, 193, 7));
                if (role != UserRole.ADMIN && role != UserRole.NURSE) {
                    appointment_status.setEnabled(false);
                }
                break;
            case "PENDING_CONFIRMATION":
                appointment_status.setText("Confirm Surgery");
                appointment_status.setToolTipText("Click to confirm this surgery booking");
                appointment_status.setBackground(new java.awt.Color(23, 162, 184));
                if (role != UserRole.ADMIN) {
                    appointment_status.setEnabled(false);
                }
                break;
            case "SCHEDULED":
                appointment_status.setText("Mark Complete");
                appointment_status.setBackground(new java.awt.Color(0, 153, 255));
                if (role == UserRole.ADMIN || role == UserRole.NURSE) {
                    edit.setVisible(true);
                    cancel.setVisible(true);
                }
                boolean canMarkComplete = (role == UserRole.DOCTOR && (apptType == AppointmentType.CONSULTATION || apptType == AppointmentType.SURGERY))
                                          || (role == UserRole.NURSE && apptType == AppointmentType.DIAGNOSTIC)
                                          || (role == UserRole.ADMIN);
                appointment_status.setEnabled(canMarkComplete);
                break;
            case "COMPLETED":
                appointment_status.setText("Completed");

                appointment_status.setBackground(new java.awt.Color(40, 167, 69));
                break;
            case "CANCELLED":
                appointment_status.setText("Cancelled");

                appointment_status.setBackground(new java.awt.Color(220, 53, 69));
                break;
        }
    }


    private void processPayment() {
        // 1. Get the currently logged-in user FIRST. If no user, stop the process.
        User confirmingUser = AuthenticationService.getInstance().getLoggedInUser();
        if (confirmingUser == null) {
            JOptionPane.showMessageDialog(this, "Session error: Cannot identify the current user. Please log in again.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 2. Define the payment options and create the dialog message.
        String[] options = {"CASH", "CARD", "INSURANCE"};
        String message = String.format(
            "Select payment method for:\n%s\nAmount: Rs.%.2f",
            appointment.getServiceName(),
            appointment.getPrice()
        );

        // 3. Show the option dialog.
        int choice = JOptionPane.showOptionDialog(
            this,
            message,
            "Process Payment",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.PLAIN_MESSAGE,
            null,
            options,
            options[0]
        );

        // 4. Process the user's choice.
        if (choice != JOptionPane.CLOSED_OPTION) {
            PaymentMethod selectedMethod = PaymentMethod.valueOf(options[choice]);
            boolean success = false;

            // 5. Call the correct service method, passing the logged-in user.
            //    This is the corrected logic block.
            if (selectedMethod == PaymentMethod.INSURANCE) {
                success = appointmentService.updatePaymentMethodAndStatus(appointment.getId(), selectedMethod, "SCHEDULED", confirmingUser);
            } else { // For CASH or CARD
                success = appointmentService.processDirectPayment(appointment.getId(), selectedMethod, confirmingUser);
            }

            // 6. Provide feedback and refresh the UI.
            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Payment confirmed by " + confirmingUser.getUsername() + ".\nThe appointment is now scheduled.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                
                if (onDataChangeCallback != null) {
                    onDataChangeCallback.run();
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to process payment. The appointment may have been modified by another user.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // --- NEW METHOD FOR SURGERY CONFIRMATION ---
    private void confirmSurgery() {
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to confirm this surgery for " + appointment.getPatient().getName() + "?\n" +
            "This will officially schedule the operating room and staff.",
            "Confirm Surgery Booking",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            // We just need to change the status to SCHEDULED. The payment method is still null.
            boolean success = appointmentService.updateAppointmentStatus(appointment.getId(), "SCHEDULED");
            if (success) {
                JOptionPane.showMessageDialog(this, "Surgery has been confirmed and scheduled!");
                if (onDataChangeCallback != null) onDataChangeCallback.run();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to confirm the surgery.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
  private void handleCompletion() {
        AppointmentType type = appointment.getType();

        if (type == AppointmentType.CONSULTATION) {
            handleConsultationCompletion();
        } else if (type == AppointmentType.SURGERY) {
            handleSurgeryCompletion();
        } else { // This block handles DIAGNOSTIC and any other simple types
            handleSimpleCompletion(type);
        }
    }


    private void showEditDialog() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JDialog editDialog = new JDialog(parentFrame, "Edit Appointment Time", true);
        editDialog.setSize(400, 220);
        editDialog.setLocationRelativeTo(this);
        editDialog.setLayout(new java.awt.BorderLayout());

        JPanel contentPanel = new JPanel(new java.awt.GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);

        JLabel titleLabel = new JLabel("Change Appointment Time");
        titleLabel.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        contentPanel.add(titleLabel, gbc);

        JLabel currentTimeLabel = new JLabel("Current: " + appointment.getAppointmentDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        gbc.gridy = 1;
        contentPanel.add(currentTimeLabel, gbc);

        DateTimePicker newTimePicker = new DateTimePicker();
        newTimePicker.setDateTimePermissive(appointment.getAppointmentDateTime());
        gbc.gridy = 2;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        contentPanel.add(newTimePicker, gbc);

        JButton saveButton = new JButton("Save Changes");
        JButton cancelButton = new JButton("Cancel");
        JPanel buttonsPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        buttonsPanel.add(cancelButton);
        buttonsPanel.add(saveButton);
        gbc.gridy = 3;
        contentPanel.add(buttonsPanel, gbc);

        saveButton.addActionListener(e -> {
            LocalDateTime newDateTime = newTimePicker.getDateTimePermissive();
            if (newDateTime != null) {
                updateAppointmentTime(newDateTime);
                editDialog.dispose();
            } else {
                JOptionPane.showMessageDialog(editDialog, "Please select a valid date and time.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            }
        });
        cancelButton.addActionListener(e -> editDialog.dispose());

        editDialog.add(contentPanel);
        editDialog.setVisible(true);
    }

    private void updateAppointmentTime(LocalDateTime newDateTime) {
        // --- 1. Basic Validation ---
        if (newDateTime.isBefore(LocalDateTime.now())) {
            JOptionPane.showMessageDialog(this,
                    "Cannot reschedule an appointment to a time in the past.",
                    "Invalid Time",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- 2. Check for Scheduling Conflicts ---
        // This call to the service will fetch any appointments that clash with the new time.
        List<Appointment> conflictingAppointments = appointmentService.getConflictingAppointments(appointment.getDoctor(), newDateTime);

        // --- 3. Handle Self-Conflict Logic ---
        // A conflict is only a "real" problem if it's with a *different* appointment.
        // It's normal for the conflict check to find the appointment we are currently editing.
        boolean isRealConflict = false;
        if (!conflictingAppointments.isEmpty()) {
            // If there's only one conflict and its ID is the same as our current appointment, it's not a real conflict.
            if (conflictingAppointments.size() > 1 || !conflictingAppointments.get(0).getId().equals(appointment.getId())) {
                isRealConflict = true;
            }
        }

        if (isRealConflict) {
            // Build a helpful error message for the user
            StringBuilder conflictMessage = new StringBuilder();
            conflictMessage.append("Doctor is not available at the selected time.\n");
            conflictMessage.append("This time slot conflicts with another scheduled appointment.\n");
            conflictMessage.append("Please select a different time.");

            JOptionPane.showMessageDialog(this, conflictMessage.toString(), "Scheduling Conflict", JOptionPane.WARNING_MESSAGE);
            return; // Stop the update process
        }

        // --- 4. If all checks pass, update the appointment in the database ---
        boolean success = appointmentService.updateAppointmentTime(appointment.getId(), newDateTime);

        // --- 5. Provide Feedback and Refresh the UI ---
        if (success) {
            // First, update the local 'appointment' object to match the change
            appointment.setAppointmentDateTime(newDateTime);

            // Then, update the text on this card to show the new time
            populateData();

            JOptionPane.showMessageDialog(this, "Appointment time updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            // Finally, call the callback to tell the parent panel to refresh its entire list
            if (onDataChangeCallback != null) {
                onDataChangeCallback.run();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update the appointment time in the database.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelAppointment() {
        int result = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to cancel this appointment?", "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.YES_OPTION) {
            boolean cancelled = appointmentService.updateAppointmentStatus(appointment.getId(), "CANCELLED");
            if (cancelled) {
                appointment.setStatus("CANCELLED");
                updateCardVisualState();
                JOptionPane.showMessageDialog(this, "Appointment cancelled successfully.");
                if (onDataChangeCallback != null) {
                    onDataChangeCallback.run();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to cancel appointment.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
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
        pay = new javax.swing.JLabel();
        docicon = new javax.swing.JLabel();
        type = new javax.swing.JLabel();

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
        edit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                editMouseClicked(evt);
            }
        });

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

        pay.setFont(new java.awt.Font("Inter 18pt Medium", 0, 14)); // NOI18N
        pay.setForeground(new java.awt.Color(0, 204, 0));
        pay.setText("Pay");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .addComponent(appointment_status)
                .addGap(18, 18, 18)
                .addComponent(pay)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(edit)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cancel)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 9, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(edit)
                    .addComponent(cancel)
                    .addComponent(appointment_status)
                    .addComponent(pay)))
        );

        docicon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/stethoscope (1).png"))); // NOI18N

        type.setFont(new java.awt.Font("Inter 18pt Medium", 0, 10)); // NOI18N
        type.setForeground(new java.awt.Color(153, 153, 153));
        type.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        type.setText("2025.10.24");

        javax.swing.GroupLayout roundedPanel1Layout = new javax.swing.GroupLayout(roundedPanel1);
        roundedPanel1.setLayout(roundedPanel1Layout);
        roundedPanel1Layout.setHorizontalGroup(
            roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel1Layout.createSequentialGroup()
                .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundedPanel1Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(time)
                        .addGap(28, 28, 28))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(type, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundedPanel1Layout.createSequentialGroup()
                        .addComponent(date)
                        .addContainerGap(526, Short.MAX_VALUE))
                    .addGroup(roundedPanel1Layout.createSequentialGroup()
                        .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(roundedPanel1Layout.createSequentialGroup()
                                .addComponent(docicon)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(drname))
                            .addComponent(patient_name)
                            .addComponent(id))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18))))
        );
        roundedPanel1Layout.setVerticalGroup(
            roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel1Layout.createSequentialGroup()
                .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundedPanel1Layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel1Layout.createSequentialGroup()
                                .addComponent(patient_name)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(drname)
                                    .addComponent(docicon))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel1Layout.createSequentialGroup()
                                .addComponent(time)
                                .addGap(4, 4, 4)))
                        .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(type)
                            .addComponent(id, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(roundedPanel1Layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addComponent(date))
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
     if (appointment == null) return;
        String status = appointment.getStatus();
        if ("PENDING_PAYMENT".equals(status)) {
            processPayment();
        } else if ("PENDING_CONFIRMATION".equals(status)) {
            confirmSurgery();
        } else if ("SCHEDULED".equals(status)) {
            handleCompletion();
        }
    }//GEN-LAST:event_appointment_statusActionPerformed


   private void handleConsultationCompletion() {
        Object[] options = {"Complete without Prescription", "Complete and Write Prescription", "Cancel"};
        String message = "How do you want to complete this consultation for " + appointment.getPatient().getName() + "?";
        int choice = JOptionPane.showOptionDialog(this, message, "Complete Consultation",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice == 0) { // User chose "Complete without Prescription"
            updateStatusToCompleted();
        } else if (choice == 1) { // User chose "Complete and Write Prescription"
            openPrescriptionDialog();
        }
       
    }
   


    /**
     * Handles the completion workflow for a SURGERY.
     * Opens a dialog to enter the billable items.
     */
private void handleSurgeryCompletion() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        SurgeryCompletionDialog dialog = new SurgeryCompletionDialog(parentFrame, true, this.appointment, this.onDataChangeCallback);
        dialog.setVisible(true);
    }
    
    /**
     * Handles the simple completion workflow for types like DIAGNOSTIC.
     * Just asks for a simple Yes/No confirmation.
     */
  private void handleSimpleCompletion(AppointmentType type) {
        int result = JOptionPane.showConfirmDialog(
            this, "Mark this " + type.toString().toLowerCase() + " as completed?",
            "Confirm Completion",
            JOptionPane.YES_NO_OPTION
        );
        if (result == JOptionPane.YES_OPTION) {
            updateStatusToCompleted();
        }
    }

    /**
     * Helper method to open the Prescription Dialog.
     */
private void openPrescriptionDialog() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        PrescriptionDialog dialog = new PrescriptionDialog(parentFrame, true, this.appointment, this.onDataChangeCallback);
        dialog.setVisible(true);
    }

    /**
     * Helper method to call the service to update status and handle UI feedback.
     */
   private void updateStatusToCompleted() {
        boolean updated = appointmentService.updateAppointmentStatus(appointment.getId(), "COMPLETED");
        if (updated) {
            JOptionPane.showMessageDialog(this, "Appointment marked as completed.");
            if (onDataChangeCallback != null) {
                onDataChangeCallback.run();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update appointment status.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    private void editMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editMouseClicked
       if (edit.isVisible()) showEditDialog();
    }//GEN-LAST:event_editMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton appointment_status;
    private javax.swing.JLabel cancel;
    private javax.swing.JLabel date;
    private javax.swing.JLabel docicon;
    private javax.swing.JLabel drname;
    private javax.swing.JLabel edit;
    private javax.swing.JLabel id;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel patient_name;
    private javax.swing.JLabel pay;
    private system.ui.components.RoundedPanel roundedPanel1;
    private javax.swing.JLabel time;
    private javax.swing.JLabel type;
    // End of variables declaration//GEN-END:variables
}
