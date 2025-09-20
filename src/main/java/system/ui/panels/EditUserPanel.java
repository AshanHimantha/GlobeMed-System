package system.ui.panels;

import java.awt.Font;
import javax.swing.*;

import system.model.User;
import system.service.UserService;
import system.enums.UserRole;

/**
 * Edit User Panel - Allows administrators to update user details
 * @author User
 */
public class EditUserPanel extends javax.swing.JPanel {

    private final UserService userService;
    private User selectedUser;
    private boolean isInitialized = false;

    /**
     * Creates new EditUserPanel
     */
    public EditUserPanel() {
        this.userService = new UserService();
        initComponents();
        configureComponents();
        isInitialized = true;
    }

    private void configureComponents() {
        // Initialize role combo box
        roleComboBox.removeAllItems();
        for (UserRole role : UserRole.values()) {
            roleComboBox.addItem(role);
        }

        // Configure consultation fee field visibility
        UserRole initialRole = (UserRole) roleComboBox.getSelectedItem();
        boolean isDoctorRole = (initialRole == UserRole.DOCTOR);
        consultationFeeField.setVisible(isDoctorRole);
        jLabel6.setVisible(isDoctorRole); // Consultation fee label

        // Role combo box listener to show/hide consultation fee field
        roleComboBox.addActionListener(e -> {
            if (!isInitialized) return;
            UserRole selectedRole = (UserRole) roleComboBox.getSelectedItem();
            boolean showConsultationFee = (selectedRole == UserRole.DOCTOR);
            consultationFeeField.setVisible(showConsultationFee);
            jLabel6.setVisible(showConsultationFee);
        });

        // Update button action
        updateButton.addActionListener(e -> updateUser());

        // Cancel button action
        cancelButton.addActionListener(e -> {
            // Notify parent that edit was cancelled
            firePropertyChange("editCancelled", false, true);
        });
    }

    public void loadUser(String username) {
        isInitialized = false; // Prevent action events during setup

        // Load user from database
        selectedUser = userService.findUserByUsername(username);
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(this,
                "User not found: " + username,
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Set panel title
        titleLabel.setText("Edit User: " + username);

        // Fill form fields
        usernameField.setText(selectedUser.getUsername());
        usernameField.setEnabled(false); // Username cannot be changed (primary key)

        firstNameField.setText(selectedUser.getFirstName());
        lastNameField.setText(selectedUser.getLastName());

        // Set role
        for (int i = 0; i < roleComboBox.getItemCount(); i++) {
            if (roleComboBox.getItemAt(i) == selectedUser.getRole()) {
                roleComboBox.setSelectedIndex(i);
                break;
            }
        }

        // Set contact info
        contactField.setText(selectedUser.getContactNumber() != null ?
                             selectedUser.getContactNumber() : "");

        addressArea.setText(selectedUser.getAddress() != null ?
                           selectedUser.getAddress() : "");

        // Set consultation fee if doctor
        if (selectedUser.getRole() == UserRole.DOCTOR && selectedUser.getConsultationFee() != null) {
            consultationFeeField.setText(selectedUser.getConsultationFee().toString());
        } else {
            consultationFeeField.setText("");
        }

        isInitialized = true;
    }

    private void updateUser() {
        try {
            if (selectedUser == null) {
                JOptionPane.showMessageDialog(this,
                    "No user selected for update.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            String username = usernameField.getText().trim();
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String contactNumber = contactField.getText().trim();
            String address = addressArea.getText().trim();
            UserRole role = (UserRole) roleComboBox.getSelectedItem();

            // Validation
            if (firstName.isEmpty() || lastName.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "First name and last name are required.",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Handle consultation fee
            Double consultationFee = null;
            if (role == UserRole.DOCTOR && !consultationFeeField.getText().trim().isEmpty()) {
                try {
                    consultationFee = Double.parseDouble(consultationFeeField.getText().trim());
                    if (consultationFee < 0) {
                        throw new NumberFormatException("Consultation fee cannot be negative");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this,
                        "Please enter a valid consultation fee.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Update user in database
            userService.updateUser(
                username,
                role,
                firstName,
                lastName,
                contactNumber.isEmpty() ? null : contactNumber,
                address.isEmpty() ? null : address,
                consultationFee
            );

            // Show success message and close both dialogs when OK is clicked
            int option = JOptionPane.showConfirmDialog(
                SwingUtilities.getWindowAncestor(this),
                "User updated successfully!",
                "Success",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE
            );

            if (option == JOptionPane.OK_OPTION) {
                // Close the parent edit dialog
                JDialog parentDialog = (JDialog) SwingUtilities.getWindowAncestor(this);
                if (parentDialog != null) {
                    parentDialog.dispose();
                }

                // Notify parent that edit was successful
                firePropertyChange("editComplete", false, true);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error updating user: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
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

        jPanel1 = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        usernameField = new system.ui.components.RoundedTextField();
        jLabel2 = new javax.swing.JLabel();
        firstNameField = new system.ui.components.RoundedTextField();
        jLabel3 = new javax.swing.JLabel();
        lastNameField = new system.ui.components.RoundedTextField();
        jLabel4 = new javax.swing.JLabel();
        roleComboBox = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        contactField = new system.ui.components.RoundedTextField();
        jLabel6 = new javax.swing.JLabel();
        consultationFeeField = new system.ui.components.RoundedTextField();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        addressArea = new javax.swing.JTextArea();
        updateButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        titleLabel.setFont(new java.awt.Font("Inter", Font.BOLD, 18));
        titleLabel.setText("Edit User");

        jLabel1.setFont(new java.awt.Font("Inter", Font.BOLD, 12));
        jLabel1.setText("Username:");

        usernameField.setFont(new java.awt.Font("Inter", Font.PLAIN, 12));
        usernameField.setPlaceholder("Username");
        usernameField.setRadius(10);
        usernameField.setEnabled(false);

        jLabel2.setFont(new java.awt.Font("Inter", Font.BOLD, 12));
        jLabel2.setText("First Name:");

        firstNameField.setFont(new java.awt.Font("Inter", Font.PLAIN, 12));
        firstNameField.setPlaceholder("First Name");
        firstNameField.setRadius(10);

        jLabel3.setFont(new java.awt.Font("Inter", Font.BOLD, 12));
        jLabel3.setText("Last Name:");

        lastNameField.setFont(new java.awt.Font("Inter", Font.PLAIN, 12));
        lastNameField.setPlaceholder("Last Name");
        lastNameField.setRadius(10);

        jLabel4.setFont(new java.awt.Font("Inter", Font.BOLD, 12));
        jLabel4.setText("Role:");

        roleComboBox.setFont(new java.awt.Font("Inter", Font.PLAIN, 12));

        jLabel5.setFont(new java.awt.Font("Inter", Font.BOLD, 12));
        jLabel5.setText("Contact Number:");

        contactField.setFont(new java.awt.Font("Inter", Font.PLAIN, 12));
        contactField.setPlaceholder("Contact Number");
        contactField.setRadius(10);

        jLabel6.setFont(new java.awt.Font("Inter", Font.BOLD, 12));
        jLabel6.setText("Consultation Fee ($):");

        consultationFeeField.setFont(new java.awt.Font("Inter", Font.PLAIN, 12));
        consultationFeeField.setPlaceholder("Consultation Fee");
        consultationFeeField.setRadius(10);

        jLabel7.setFont(new java.awt.Font("Inter", Font.BOLD, 12));
        jLabel7.setText("Address:");

        addressArea.setColumns(20);
        addressArea.setFont(new java.awt.Font("Inter", Font.PLAIN, 12));
        addressArea.setRows(4);
        addressArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        jScrollPane1.setViewportView(addressArea);

        updateButton.setBackground(new java.awt.Color(0, 153, 204));
        updateButton.setFont(new java.awt.Font("Inter", Font.BOLD, 12));
        updateButton.setForeground(new java.awt.Color(255, 255, 255));
        updateButton.setText("Update");

        cancelButton.setBackground(new java.awt.Color(204, 204, 204));
        cancelButton.setFont(new java.awt.Font("Inter", Font.BOLD, 12));
        cancelButton.setText("Cancel");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(titleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(usernameField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(firstNameField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lastNameField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(roleComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(contactField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(consultationFeeField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(updateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleLabel)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(usernameField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(firstNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lastNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(roleComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(contactField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(consultationFeeField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(updateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea addressArea;
    private javax.swing.JButton cancelButton;
    private system.ui.components.RoundedTextField consultationFeeField;
    private system.ui.components.RoundedTextField contactField;
    private system.ui.components.RoundedTextField firstNameField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private system.ui.components.RoundedTextField lastNameField;
    private javax.swing.JComboBox<UserRole> roleComboBox;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JButton updateButton;
    private system.ui.components.RoundedTextField usernameField;
    // End of variables declaration//GEN-END:variables
}
