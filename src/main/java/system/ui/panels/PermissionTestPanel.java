
package system.ui.panels;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import system.model.User;
import system.patterns.strategy.PermissionContext;
import system.service.AuthenticationService;

/**
 *
 * @author User
 */
public class PermissionTestPanel extends JPanel {

    
    
    private final PermissionContext permissionContext;
    private final User currentUser;

    // --- UI Components ---
    private JLabel currentUserLabel;
    private JButton testPrescribeButton;
    private JButton testBillingButton;
    private JButton testScheduleButton;
    private JTextArea resultTextArea;

    public PermissionTestPanel() {
        // --- 1. Get the current user and create the context ---
        this.currentUser = AuthenticationService.getInstance().getLoggedInUser();
        this.permissionContext = new PermissionContext(currentUser);

        // --- 2. Build the UI ---
        initComponentsManual();

        // --- 3. Populate initial data ---
        if (currentUser != null) {
            currentUserLabel.setText("Testing Permissions for User: " + currentUser.getUsername() + " (Role: " + currentUser.getRole() + ")");
        } else {
            currentUserLabel.setText("No user logged in. All actions will be denied.");
        }

        // --- 4. Add action listeners to the test buttons ---
        testPrescribeButton.addActionListener(e -> testPermission("Prescribe Medication"));
        testBillingButton.addActionListener(e -> testPermission("Process Billing"));
        testScheduleButton.addActionListener(e -> testPermission("Schedule Appointment"));
    }
    
    private void testPermission(String action) {
        resultTextArea.setText(""); // Clear previous results
        log("User '" + (currentUser != null ? currentUser.getUsername() : "Guest") + "' is attempting to: " + action);
        
        boolean hasPermission = false;

        // --- STRATEGY PATTERN IN ACTION ---
        // The UI (client) doesn't know which strategy is being used.
        // It just calls the method on the context object. The context delegates
        // the call to the concrete strategy that was selected based on the user's role.
        switch (action) {
            case "Prescribe Medication":
                log("Calling permissionContext.canPrescribeMedication()...");
                hasPermission = permissionContext.canPrescribeMedication();
                break;
            case "Process Billing":
                log("Calling permissionContext.canProcessBilling()...");
                hasPermission = permissionContext.canProcessBilling();
                break;
            case "Schedule Appointment":
                log("Calling permissionContext.canScheduleAppointments()...");
                hasPermission = permissionContext.canScheduleAppointments();
                break;
        }
        
        log("\n--- Result ---");
        if (hasPermission) {
            log("ACCESS GRANTED");
            JOptionPane.showMessageDialog(this, "Access Granted!", "Permission Check", JOptionPane.INFORMATION_MESSAGE);
        } else {
            log("ACCESS DENIED");
            JOptionPane.showMessageDialog(this, "Access Denied. Your role does not have this permission.", "Permission Check", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void log(String message) {
        resultTextArea.append(message + "\n");
    }

    private void initComponentsManual() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        JLabel titleLabel = new JLabel("Permissions Test Panel (Strategy Pattern)");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        currentUserLabel = new JLabel("Testing Permissions for: ...");
        headerPanel.add(titleLabel);
        headerPanel.add(currentUserLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Center panel for buttons
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 10, 10)); // 1 column, variable rows
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Test Actions"));
        
        testPrescribeButton = new JButton("Attempt to Prescribe Medication");
        testBillingButton = new JButton("Attempt to Process Billing");
        testScheduleButton = new JButton("Attempt to Schedule Appointment");

        buttonPanel.add(testPrescribeButton);
        buttonPanel.add(testBillingButton);
        buttonPanel.add(testScheduleButton);

        // Log area at the bottom
        resultTextArea = new JTextArea(10, 40);
        resultTextArea.setEditable(false);
        resultTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(resultTextArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Execution Log"));

        // Add components to main panel
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.add(buttonPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);
    }
    
    
    
    
    
    
    
    
    
     
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}

