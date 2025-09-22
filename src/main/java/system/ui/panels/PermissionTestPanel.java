package system.ui.panels;

import java.awt.Font;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import system.model.User;
import system.service.UserService;
import system.enums.UserRole;
import system.patterns.strategy.PermissionContext;

/**
 * Staff Management Panel with Permission Testing (Strategy Pattern)
 * @author User
 */
public class PermissionTestPanel extends javax.swing.JPanel {

    private final UserService userService;
    private final Map<String, User> userMap = new HashMap<>();

    public PermissionTestPanel() {
        this.userService = new UserService();

        initComponents(); // NetBeans-generated components
        configureComponents();
        addListeners();
    }

    private void configureComponents() {
        // Set up the JTable with the correct columns for staff management
        DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Username", "Full Name", "Role", "Contact", "Address", "Consultation Fee"}, 0) {
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

        // Configure staff search field with placeholder (right panel)
        roundedTextField2.setPlaceholder("Search by username, name, or role...");
        roundedTextField2.setRadius(10);

        // Initialize role combo box
        roleComboBox.removeAllItems();
        for (UserRole role : UserRole.values()) {
            roleComboBox.addItem(role);
        }

        // Set consultation fee field visibility based on initially selected role
        UserRole initialRole = (UserRole) roleComboBox.getSelectedItem();
        boolean isDoctorRole = (initialRole == UserRole.DOCTOR);
        consultationFeeField.setVisible(isDoctorRole);
        jLabel15.setVisible(isDoctorRole); // Consultation fee label

        // Load all users initially
        loadAllUsers();
    }

    private void addListeners() {
        // Listener for the staff search text field (right panel)
        roundedTextField2.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterStaffTable(); }
            public void removeUpdate(DocumentEvent e) { filterStaffTable(); }
            public void changedUpdate(DocumentEvent e) { /* Not needed */ }
        });

        // The search button for staff table (right panel)
        jButton2.addActionListener(e -> filterStaffTable());

        // Add staff button
        addUserButton.addActionListener(e -> addNewUser());

        // Role combo box listener to show/hide consultation fee field
        roleComboBox.addActionListener(e -> {
            UserRole selectedRole = (UserRole) roleComboBox.getSelectedItem();
            boolean isDoctorRole = (selectedRole == UserRole.DOCTOR);
            consultationFeeField.setVisible(isDoctorRole);
            jLabel15.setVisible(isDoctorRole);
        });

        // Table selection listener for permission analysis
        jTable1.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onUserSelected();
            }
        });
    }

    private void loadAllUsers() {
        List<User> users = userService.getAllUsers();
        DefaultTableModel tableModel = (DefaultTableModel) jTable1.getModel();
        tableModel.setRowCount(0); // Clear the table
        userMap.clear();

        // Reset user info labels
        jLabel7.setText("User ID :");
        jLabel8.setText("User Name :");

        for (User user : users) {
            String consultationFeeStr = "";
            if (user.getRole() == UserRole.DOCTOR && user.getConsultationFee() != null) {
                consultationFeeStr = "Rs." + user.getConsultationFee();
            }

            String fullName = user.getFirstName() + " " + user.getLastName();
            userMap.put(user.getUsername(), user);

            tableModel.addRow(new Object[]{
                user.getUsername(),
                fullName,
                user.getRole(),
                user.getContactNumber() != null ? user.getContactNumber() : "N/A",
                user.getAddress() != null ? user.getAddress() : "N/A",
                consultationFeeStr
            });
        }
    }

    private void filterStaffTable() {
        String searchText = roundedTextField2.getText().trim();
        DefaultTableModel tableModel = (DefaultTableModel) jTable1.getModel();
        tableModel.setRowCount(0); // Clear the table
        userMap.clear();

        if (searchText.isEmpty()) {
            loadAllUsers();
            return;
        }

        List<User> users = userService.getAllUsers();
        boolean found = false;

        for (User user : users) {
            String fullName = user.getFirstName() + " " + user.getLastName();
            if (user.getUsername().toLowerCase().contains(searchText.toLowerCase()) ||
                fullName.toLowerCase().contains(searchText.toLowerCase()) ||
                user.getRole().toString().toLowerCase().contains(searchText.toLowerCase())) {

                String consultationFeeStr = "";
                if (user.getRole() == UserRole.DOCTOR && user.getConsultationFee() != null) {
                    consultationFeeStr = "Rs." + user.getConsultationFee();
                }

                userMap.put(user.getUsername(), user);

                tableModel.addRow(new Object[]{
                    user.getUsername(),
                    fullName,
                    user.getRole(),
                    user.getContactNumber() != null ? user.getContactNumber() : "N/A",
                    user.getAddress() != null ? user.getAddress() : "N/A",
                    consultationFeeStr
                });
                found = true;
            }
        }

        if (!found) {
            tableModel.addRow(new Object[]{"No users found matching search criteria.", "", "", "", "", ""});
        }
    }

    private void onUserSelected() {
        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow < 0) return;

        String username = (String) jTable1.getValueAt(selectedRow, 0);
        User selectedUser = userMap.get(username);

        if (selectedUser == null) return;

        // Update the info labels with actual data
        jLabel7.setText("User ID : " + selectedUser.getUsername());
        jLabel8.setText("User Name : " + selectedUser.getFirstName() + " " + selectedUser.getLastName());

        // Open the EditUserPanel instead of showing permission analysis
        openEditUserDialog(selectedUser.getUsername());
    }

    /**
     * Opens the edit user dialog for the specified user
     * @param username The username of the user to edit
     */
    private void openEditUserDialog(String username) {
        try {
            // Create an EditUserPanel
            EditUserPanel editPanel = new EditUserPanel();
            editPanel.loadUser(username);

            // Show it in a dialog
            JOptionPane optionPane = new JOptionPane(editPanel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
            JDialog dialog = optionPane.createDialog(this, "Edit User: " + username);

            // Add property change listener to handle dialog closing
            optionPane.addPropertyChangeListener(e -> {
                if (JOptionPane.VALUE_PROPERTY.equals(e.getPropertyName())) {
                    dialog.dispose();
                    // Refresh the user table after editing
                    loadAllUsers();
                }
            });

            // Show the dialog
            dialog.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error opening edit dialog: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showPermissionAnalysis(User user) {
        StringBuilder analysis = new StringBuilder();
        analysis.append("--- Permission Analysis for: ").append(user.getUsername()).append(" ---\n");
        analysis.append("Role: ").append(user.getRole()).append("\n\n");
        analysis.append("Strategy Pattern in Action:\n");
        analysis.append("PermissionContext delegates to role-specific strategy.\n\n");

        // --- STRATEGY PATTERN IN ACTION ---
        PermissionContext context = new PermissionContext(user);

        analysis.append("PERMISSIONS:\n");
        analysis.append(formatPermission("View Patient Records", context.canViewPatientRecords()));
        analysis.append(formatPermission("Edit Patient Records", context.canEditPatientRecords()));
        analysis.append(formatPermission("Schedule Appointments", context.canScheduleAppointments()));
        analysis.append(formatPermission("Schedule Appointments", context.canScheduleAppointments()));
        analysis.append(formatPermission("Process Billing", context.canProcessBilling()));
        analysis.append(formatPermission("Generate Financial Reports", context.canGenerateFinancialReports()));

        // Show in a dialog with proper formatting
        JTextArea textArea = new JTextArea(analysis.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new java.awt.Dimension(500, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "Permission Analysis - Strategy Pattern", JOptionPane.INFORMATION_MESSAGE);
    }

    private String formatPermission(String permissionName, boolean isGranted) {
        String result = isGranted ? "GRANTED" : "DENIED";
        return String.format("• %-30s [%s]\n", permissionName, result);
    }

    private void addNewUser() {
        try {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim(); // RoundedTextField uses getText(), not getPassword()
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String contactNumber = contactField.getText().trim();
            String address = addressArea.getText().trim();
            UserRole role = (UserRole) roleComboBox.getSelectedItem();

            // Validation
            if (username.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all required fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Double consultationFee = null;
            if (role == UserRole.DOCTOR && consultationFeeField.isVisible() && !consultationFeeField.getText().trim().isEmpty()) {
                try {
                    consultationFee = Double.parseDouble(consultationFeeField.getText().trim());
                    if (consultationFee < 0) {
                        throw new NumberFormatException("Consultation fee cannot be negative");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid consultation fee.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            userService.addUser(username, password, role, firstName, lastName,
                              contactNumber.isEmpty() ? null : contactNumber,
                              address.isEmpty() ? null : address,
                              consultationFee);

            JOptionPane.showMessageDialog(this, "Staff member added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            // Clear the form
            clearForm();

            // Refresh the table
            loadAllUsers();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error adding staff member: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        usernameField.setText("");
        passwordField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        contactField.setText("");
        addressArea.setText("");
        consultationFeeField.setText("");
        roleComboBox.setSelectedIndex(0);
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
        jScrollPane3 = new javax.swing.JScrollPane();
        roundedPanel1 = new system.ui.components.RoundedPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        // Create missing labels for the form
        javax.swing.JLabel roleLabel = new javax.swing.JLabel();
        javax.swing.JLabel contactLabel = new javax.swing.JLabel();
        javax.swing.JLabel addressLabel = new javax.swing.JLabel();
        usernameField = new system.ui.components.RoundedTextField();
        passwordField = new system.ui.components.RoundedTextField();
        firstNameField = new system.ui.components.RoundedTextField();
        lastNameField = new system.ui.components.RoundedTextField();
        contactField = new system.ui.components.RoundedTextField();
        consultationFeeField = new system.ui.components.RoundedTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        addressArea = new javax.swing.JTextArea();
        roleComboBox = new javax.swing.JComboBox<>();
        addUserButton = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        roundedPanel2 = new system.ui.components.RoundedPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        roundedTextField2 = new system.ui.components.RoundedTextField();
        jButton2 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new java.awt.BorderLayout(10, 0));

        jPanel3.setBackground(new java.awt.Color(247, 247, 247));
        jPanel3.setMaximumSize(new java.awt.Dimension(32767, 90));
        jPanel3.setMinimumSize(new java.awt.Dimension(0, 90));
        jPanel3.setPreferredSize(new java.awt.Dimension(987, 90));

        jLabel1.setText("Staff Management Portal - User Permissions ");
        jLabel1.setFont(new java.awt.Font("Inter 18pt", Font.BOLD, 24));
        jLabel1.setForeground(new java.awt.Color(5, 5, 5));

        jLabel2.setText("Add new staff on the left panel and search existing staff on the right panel.");
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

        jSplitPane1.setDividerLocation(420);
        jSplitPane1.setDividerSize(5);
        jSplitPane1.setMaximumSize(new java.awt.Dimension(2147483647, 500));
        jSplitPane1.setMinimumSize(new java.awt.Dimension(800, 300));
        jSplitPane1.setPreferredSize(new java.awt.Dimension(1250, 510));

        // LEFT PANEL - ADD STAFF FORM (SCROLLABLE)
        jPanel2.setBackground(new java.awt.Color(247, 247, 247));
        jPanel2.setMaximumSize(new java.awt.Dimension(420, 32767));
        jPanel2.setMinimumSize(new java.awt.Dimension(420, 100));
        jPanel2.setPreferredSize(new java.awt.Dimension(420, 510));
        jPanel2.setLayout(new java.awt.BorderLayout());

        // Make the left panel scrollable
        jScrollPane3.setBackground(new java.awt.Color(247, 247, 247));
        jScrollPane3.setBorder(null);
        jScrollPane3.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane3.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        roundedPanel1.setBackground(new java.awt.Color(255, 255, 255));
        roundedPanel1.setForeground(new java.awt.Color(234, 234, 234));
        roundedPanel1.setMinimumSize(new java.awt.Dimension(400, 800));
        roundedPanel1.setPreferredSize(new java.awt.Dimension(400, 800));

        jLabel3.setText("Add New Staff Member");
        roundedPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jLabel3.setFont(new java.awt.Font("Inter 18pt SemiBold", Font.PLAIN, 20));
        jLabel3.setForeground(new java.awt.Color(23, 23, 23));

        jLabel4.setText("Fill in the details to add a new staff member");
        jLabel4.setBackground(new java.awt.Color(153, 153, 153));
        jLabel4.setFont(new java.awt.Font("Inter", Font.PLAIN, 12));
        jLabel4.setForeground(new java.awt.Color(153, 153, 153));

        jLabel13.setText("Username:");
        jLabel13.setFont(new java.awt.Font("Inter", Font.BOLD, 12));
        jLabel13.setForeground(new java.awt.Color(51, 51, 51));

        jLabel14.setText("Password:");
        jLabel14.setFont(new java.awt.Font("Inter", Font.BOLD, 12));
        jLabel14.setForeground(new java.awt.Color(51, 51, 51));

        jLabel11.setText("First Name:");
        jLabel11.setFont(new java.awt.Font("Inter", Font.BOLD, 12));
        jLabel11.setForeground(new java.awt.Color(51, 51, 51));

        jLabel12.setText("Last Name:");
        jLabel12.setFont(new java.awt.Font("Inter", Font.BOLD, 12));
        jLabel12.setForeground(new java.awt.Color(51, 51, 51));

        roleLabel.setText("Role:");
        roleLabel.setFont(new java.awt.Font("Inter", Font.BOLD, 12));
        roleLabel.setForeground(new java.awt.Color(51, 51, 51));

        contactLabel.setText("Contact Number:");
        contactLabel.setFont(new java.awt.Font("Inter", Font.BOLD, 12));
        contactLabel.setForeground(new java.awt.Color(51, 51, 51));

        addressLabel.setText("Address:");
        addressLabel.setFont(new java.awt.Font("Inter", Font.BOLD, 12));
        addressLabel.setForeground(new java.awt.Color(51, 51, 51));

        jLabel15.setText("Consultation Fee (Rs.):");
        jLabel15.setFont(new java.awt.Font("Inter", Font.BOLD, 12));
        jLabel15.setForeground(new java.awt.Color(51, 51, 51));

        // Configure rounded text fields with placeholders
        usernameField.setFont(new java.awt.Font("Inter", Font.PLAIN, 12));
        usernameField.setPlaceholder("Enter username");
        usernameField.setRadius(10);

        passwordField.setFont(new java.awt.Font("Inter", Font.PLAIN, 12));
        passwordField.setPlaceholder("Enter password");
        passwordField.setRadius(10);

        firstNameField.setFont(new java.awt.Font("Inter", Font.PLAIN, 12));
        firstNameField.setPlaceholder("Enter first name");
        firstNameField.setRadius(10);

        lastNameField.setFont(new java.awt.Font("Inter", Font.PLAIN, 12));
        lastNameField.setPlaceholder("Enter last name");
        lastNameField.setRadius(10);

        contactField.setFont(new java.awt.Font("Inter", Font.PLAIN, 12));
        contactField.setPlaceholder("Enter contact number");
        contactField.setRadius(10);

        consultationFeeField.setFont(new java.awt.Font("Inter", Font.PLAIN, 12));
        consultationFeeField.setPlaceholder("Enter consultation fee");
        consultationFeeField.setRadius(10);

        addressArea.setColumns(20);
        addressArea.setRows(4);
        addressArea.setFont(new java.awt.Font("Inter", Font.PLAIN, 12));
        addressArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        jScrollPane1.setViewportView(addressArea);

        roleComboBox.setFont(new java.awt.Font("Inter", Font.PLAIN, 12));

        addUserButton.setText("Add Staff");
        addUserButton.setBackground(new java.awt.Color(0, 204, 102));
        addUserButton.setFont(new java.awt.Font("Inter 18pt SemiBold", Font.BOLD, 14));
        addUserButton.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout roundedPanel1Layout = new javax.swing.GroupLayout(roundedPanel1);
        roundedPanel1.setLayout(roundedPanel1Layout);
        roundedPanel1Layout.setHorizontalGroup(
            roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel13)
                    .addComponent(usernameField, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(firstNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(lastNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(roleLabel)
                    .addComponent(roleComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(contactLabel)
                    .addComponent(contactField, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(consultationFeeField, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addressLabel)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addUserButton, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        roundedPanel1Layout.setVerticalGroup(
            roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel3)
                .addGap(6, 6, 6)
                .addComponent(jLabel4)
                .addGap(20, 20, 20)
                .addComponent(jLabel13)
                .addGap(6, 6, 6)
                .addComponent(usernameField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(jLabel14)
                .addGap(6, 6, 6)
                .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(jLabel11)
                .addGap(6, 6, 6)
                .addComponent(firstNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(jLabel12)
                .addGap(6, 6, 6)
                .addComponent(lastNameField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(roleLabel)
                .addGap(6, 6, 6)
                .addComponent(roleComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(contactLabel)
                .addGap(6, 6, 6)
                .addComponent(contactField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(jLabel15)
                .addGap(6, 6, 6)
                .addComponent(consultationFeeField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(addressLabel)
                .addGap(6, 6, 6)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addComponent(addUserButton, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        jScrollPane3.setViewportView(roundedPanel1);
        jPanel2.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        jSplitPane1.setLeftComponent(jPanel2);

        // RIGHT PANEL - STAFF DIRECTORY
        jPanel4.setBackground(new java.awt.Color(247, 247, 247));
        jPanel4.setMaximumSize(new java.awt.Dimension(2147483647, 600));
        jPanel4.setPreferredSize(new java.awt.Dimension(831, 600));
        jPanel4.setLayout(new java.awt.BorderLayout(10, 10));

        roundedPanel2.setBackground(new java.awt.Color(255, 255, 255));
        roundedPanel2.setMaximumSize(new java.awt.Dimension(32767, 600));
        roundedPanel2.setMinimumSize(new java.awt.Dimension(0, 600));
        roundedPanel2.setPreferredSize(new java.awt.Dimension(811, 600));

        jLabel9.setText("Staff Directory & Permissions");
        roundedPanel2.setBackground(new java.awt.Color(255, 255, 255));
        roundedPanel2.setForeground(new java.awt.Color(234, 234, 234));
        jLabel9.setFont(new java.awt.Font("Inter 18pt SemiBold", Font.PLAIN, 20));
        jLabel9.setForeground(new java.awt.Color(23, 23, 23));

        jLabel10.setText("Search and view staff members. Select a user to analyze their permissions.");
        jLabel10.setBackground(new java.awt.Color(153, 153, 153));
        jLabel10.setFont(new java.awt.Font("Inter", Font.PLAIN, 12));
        jLabel10.setForeground(new java.awt.Color(153, 153, 153));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Username", "Full Name", "Role", "Contact", "Address", "Consultation Fee"
            }
        ) {
            final boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable1);

        jLabel5.setText("Search Staff:");
        jLabel5.setFont(new java.awt.Font("Inter", Font.BOLD, 12));
        jLabel5.setForeground(new java.awt.Color(51, 51, 51));

        roundedTextField2.setBackground(new java.awt.Color(255, 255, 255));
        roundedTextField2.setFont(new java.awt.Font("Inter", Font.PLAIN, 12));
        roundedTextField2.setForeground(new java.awt.Color(102, 102, 102));

        jButton2.setText("Search");
        jButton2.setBackground(new java.awt.Color(0, 153, 255));
        jButton2.setFont(new java.awt.Font("Inter 18pt SemiBold", Font.PLAIN, 12));
        jButton2.setForeground(new java.awt.Color(255, 255, 255));

        jLabel6.setText("Selected User Information:");
        jLabel6.setFont(new java.awt.Font("Inter", Font.BOLD, 12));
        jLabel6.setForeground(new java.awt.Color(51, 51, 51));

        jLabel7.setText("User ID :");
        jLabel7.setFont(new java.awt.Font("Inter", Font.PLAIN, 12));
        jLabel7.setForeground(new java.awt.Color(51, 51, 51));

        jLabel8.setText("User Name :");
        jLabel8.setFont(new java.awt.Font("Inter", Font.PLAIN, 12));
        jLabel8.setForeground(new java.awt.Color(51, 51, 51));

        javax.swing.GroupLayout roundedPanel2Layout = new javax.swing.GroupLayout(roundedPanel2);
        roundedPanel2.setLayout(roundedPanel2Layout);
        roundedPanel2Layout.setHorizontalGroup(
            roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundedPanel2Layout.createSequentialGroup()
                        .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(roundedPanel2Layout.createSequentialGroup()
                        .addComponent(roundedTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 760, Short.MAX_VALUE))
                .addGap(20, 20, 20))
        );
        roundedPanel2Layout.setVerticalGroup(
            roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel9)
                .addGap(6, 6, 6)
                .addComponent(jLabel10)
                .addGap(20, 20, 20)
                .addComponent(jLabel5)
                .addGap(6, 6, 6)
                .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(roundedTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                .addGap(20, 20, 20)
                .addComponent(jLabel6)
                .addGap(10, 10, 10)
                .addComponent(jLabel7)
                .addGap(6, 6, 6)
                .addComponent(jLabel8)
                .addGap(20, 20, 20))
        );

        jPanel4.add(roundedPanel2, java.awt.BorderLayout.CENTER);

        jSplitPane1.setRightComponent(jPanel4);

        jPanel1.add(jSplitPane1, "card2");

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addUserButton;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTable jTable1;
    private system.ui.components.RoundedPanel roundedPanel1;
    private system.ui.components.RoundedPanel roundedPanel2;
    private system.ui.components.RoundedTextField roundedTextField2;
    private javax.swing.JTextArea addressArea;
    private system.ui.components.RoundedTextField contactField;
    private system.ui.components.RoundedTextField consultationFeeField;
    private system.ui.components.RoundedTextField firstNameField;
    private system.ui.components.RoundedTextField lastNameField;
    private javax.swing.JComboBox<UserRole> roleComboBox;
    private system.ui.components.RoundedTextField usernameField;
    private system.ui.components.RoundedTextField passwordField;
    // End of variables declaration//GEN-END:variables

}

