
package system.ui.panels;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import system.patterns.visitor.ActivityReportVisitor;
import system.patterns.visitor.FinancialReportVisitor;
import system.patterns.visitor.ReportVisitor;
import system.patterns.visitor.Visitable;
import system.service.AppointmentService;
import system.service.ClaimService;

/**
 *
 * @author User
 */
public class ReportGeneratorPanel extends javax.swing.JPanel {

// --- Services to fetch our data "elements" ---
    private final AppointmentService appointmentService;
    private final ClaimService claimService;

    // --- UI Components (replace with your designer variable names) ---
    private JComboBox<String> reportTypeComboBox;
    private JButton generateButton;
    private JTextArea reportTextArea;

    public ReportGeneratorPanel() {
        this.appointmentService = new AppointmentService();
        this.claimService = new ClaimService();

        initComponentsManual(); // Build the UI

        // Populate the combo box with report options
        reportTypeComboBox.addItem("Financial Report");
        reportTypeComboBox.addItem("System Activity Report");

        // Add listener to the generate button
        generateButton.addActionListener(e -> onGenerateReport());
    }
    
    private void onGenerateReport() {
        String selectedReport = (String) reportTypeComboBox.getSelectedItem();
        if (selectedReport == null) {
            JOptionPane.showMessageDialog(this, "Please select a report type.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        reportTextArea.setText("Generating report, please wait...");
        
        // --- VISITOR PATTERN IN ACTION ---

        // 1. Create the appropriate Visitor based on the user's selection.
        ReportVisitor visitor;
        if ("Financial Report".equals(selectedReport)) {
            visitor = new FinancialReportVisitor();
        } else {
            visitor = new ActivityReportVisitor();
        }

        // 2. Gather all the data elements that can be "visited".
        List<Visitable> dataElements = new ArrayList<>();
        // Fetch all appointments (not just for today)
        dataElements.addAll(appointmentService.getAllAppointments());
        // Fetch all claims
        dataElements.addAll(claimService.getAllClaims());

        // 3. The core of the pattern: Iterate through all elements and ask them to "accept" the visitor.
        // We don't care what type each element is. We just tell it to accept.
        // The element itself (via double dispatch) will call the correct "visit" method on the visitor.
        for (Visitable element : dataElements) {
            element.accept(visitor);
        }

        // 4. Once all elements have been visited, get the final formatted report from the visitor.
        String finalReport = visitor.getReport();
        reportTextArea.setText(finalReport);
    }
    
    // --- (Manual UI building method) ---
    private void initComponentsManual() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header
        JLabel titleLabel = new JLabel("System Reports (Visitor Pattern Demo)");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // Top panel for controls
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.add(new JLabel("Select Report Type:"));
        reportTypeComboBox = new JComboBox<>();
        controlPanel.add(reportTypeComboBox);
        generateButton = new JButton("Generate Report");
        controlPanel.add(generateButton);
        
        // Main content area
        reportTextArea = new JTextArea();
        reportTextArea.setEditable(false);
        reportTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(reportTextArea);
        
        // Add components to panel
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.add(controlPanel, BorderLayout.NORTH);
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
