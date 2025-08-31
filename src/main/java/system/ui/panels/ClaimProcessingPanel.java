/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package system.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import system.model.Appointment;
import system.model.BillableItem;
import system.model.Claim;
import system.patterns.chain.ClaimHandler;
import system.patterns.chain.ValidationHandler;
import system.service.BillingService;
import system.service.ClaimService;
import system.ui.components.RoundedPanel;


/**
 *
 * @author User
 */
public class ClaimProcessingPanel extends javax.swing.JPanel {

private final ClaimService claimService;
    private final BillingService billingService;
    private final DefaultListModel<String> listModel;
    private final Map<String, Appointment> appointmentMap;

    // --- UI Component Declarations ---
    private JList<String> appointmentList;
    private JPanel detailsPanel; // Replace JTextArea with JPanel for cards
    private JButton processClaimButton;
    private JButton refreshButton;
    private JButton openInsurancePanelButton;
    private JButton openPatientBillingButton;
    private JSplitPane mainSplitPane;
    private JPanel leftPanel;
    private JPanel rightPanel;

    public ClaimProcessingPanel() {
       this.claimService = new ClaimService();
        this.billingService = new BillingService();
        this.listModel = new DefaultListModel<>();
        this.appointmentMap = new HashMap<>();

        initComponentsManual();
        
        // Add event listeners for all interactive components
        appointmentList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onAppointmentSelected();
            }
        });
        processClaimButton.addActionListener(e -> onProcessClaim());
        refreshButton.addActionListener(e -> loadAppointmentsReadyForBilling());
        openInsurancePanelButton.addActionListener(e -> openInsurancePanel());
        openPatientBillingButton.addActionListener(e -> openPatientBillingPanel());

        // Load the initial data into the list
        loadAppointmentsReadyForBilling();
    }
    
    private void openPatientBillingPanel() {
        JDialog billingDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Patient Final Billing Portal", true);
        
        // Create an instance of the new panel
        PatientBillingPanel billingPanel = new PatientBillingPanel(); 
        
        billingDialog.setContentPane(billingPanel);
        billingDialog.pack(); // Automatically sizes the dialog to fit the panel
        billingDialog.setMinimumSize(new Dimension(800, 600)); // Set a reasonable minimum size
        billingDialog.setLocationRelativeTo(this);
        billingDialog.setVisible(true);
        
        // After the billing clerk is done, refresh this main list.
        loadAppointmentsReadyForBilling();
    }
    
    private void loadAppointmentsReadyForBilling() {
        listModel.clear();
        appointmentMap.clear();
        showWelcomeCard();
        processClaimButton.setEnabled(false);

        List<Appointment> appointments = claimService.getAppointmentsReadyForBilling();
        if (appointments.isEmpty()) {
            listModel.addElement("No appointments are pending insurance claims.");
            appointmentList.setEnabled(false);
        } else {
            appointmentList.setEnabled(true);
            for (Appointment appt : appointments) {
                String displayText = String.format("Appt #%d - %s (%s)",
                    appt.getId(), appt.getPatient().getName(), appt.getServiceName());
                listModel.addElement(displayText);
                appointmentMap.put(displayText, appt);
            }
        }
    }

      private void onAppointmentSelected() {
        String selectedValue = appointmentList.getSelectedValue();
        if (selectedValue == null || !appointmentMap.containsKey(selectedValue)) {
            showWelcomeCard();
            processClaimButton.setEnabled(false);
            return;
        }

        Appointment appt = appointmentMap.get(selectedValue);
        displayAppointmentDetailsCards(appt);
        processClaimButton.setEnabled(true);
    }

    private void displayAppointmentDetailsCards(Appointment appt) {
        detailsPanel.removeAll();
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

        // Appointment Overview Card
        RoundedPanel appointmentCard = createInfoCard(
            "Appointment Overview",
            "Appointment #" + appt.getId(),
            "Service: " + appt.getServiceName(),
            "appointment.png"
        );
        detailsPanel.add(appointmentCard);
        detailsPanel.add(Box.createVerticalStrut(10));

        // Patient Information Card
        RoundedPanel patientCard = createInfoCard(
            "Patient Information",
            appt.getPatient().getName(),
            "Patient ID: " + appt.getPatient().getPatientId(),
            "health-insurance.png"
        );
        detailsPanel.add(patientCard);
        detailsPanel.add(Box.createVerticalStrut(10));

        // Financial Overview Card
        double total = appt.getPrice();
        Claim associatedClaim = claimService.findClaimByAppointmentId(appt.getId());

        if (associatedClaim != null) {
            List<BillableItem> items = associatedClaim.getItems();
            if (!items.isEmpty()) {
                total = 0;
                for (BillableItem item : items) {
                    total += item.getCost();
                }
            }
        }

        RoundedPanel financialCard = createInfoCard(
            "Financial Summary",
            currencyFormat.format(total),
            "Total Amount to be Claimed",
            "accounting.png"
        );
        detailsPanel.add(financialCard);

        // Billable Items Card (if exists)
        if (associatedClaim != null) {
            List<BillableItem> items = associatedClaim.getItems();
            if (!items.isEmpty()) {
                detailsPanel.add(Box.createVerticalStrut(10));
                RoundedPanel itemsCard = createBillableItemsCard(items, associatedClaim.getId());
                detailsPanel.add(itemsCard);
            }
        }

        detailsPanel.revalidate();
        detailsPanel.repaint();
    }

    private void showWelcomeCard() {
        detailsPanel.removeAll();

        RoundedPanel welcomeCard = createWelcomeCard();
        detailsPanel.add(welcomeCard);

        detailsPanel.revalidate();
        detailsPanel.repaint();
    }

    private RoundedPanel createInfoCard(String title, String mainInfo, String subtitle, String iconName) {
        RoundedPanel card = new RoundedPanel();
        card.setLayout(new BorderLayout(10, 5));
        card.setBackground(Color.WHITE);
        card.setForeground(new Color(220, 220, 220));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        card.setMinimumSize(new Dimension(400, 120));
        card.setPreferredSize(new Dimension(800, 120));

        // Left content panel for text
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setOpaque(false);

        // Title at top left
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 16));
        titleLabel.setForeground(new Color(44, 62, 80));
        leftPanel.add(titleLabel, BorderLayout.NORTH);

        // Main info in center left
        JLabel mainInfoLabel = new JLabel(mainInfo);
        mainInfoLabel.setFont(new Font("Inter", Font.BOLD, 20));
        mainInfoLabel.setForeground(new Color(52, 152, 219));
        leftPanel.add(mainInfoLabel, BorderLayout.CENTER);

        // Subtitle at bottom left
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(127, 140, 141));
        leftPanel.add(subtitleLabel, BorderLayout.SOUTH);

        card.add(leftPanel, BorderLayout.CENTER);

        // Right panel for icon
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new Dimension(60, 120));

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/img/" + iconName));
            Image scaledImage = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            JLabel iconLabel = new JLabel(new ImageIcon(scaledImage));
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
            iconLabel.setVerticalAlignment(SwingConstants.TOP);
            iconLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            rightPanel.add(iconLabel, BorderLayout.NORTH);
        } catch (Exception e) {
            // Fallback icon
            JLabel placeholderIcon = new JLabel("📋");
            placeholderIcon.setFont(new Font("Arial", Font.BOLD, 32));
            placeholderIcon.setForeground(new Color(52, 152, 219));
            placeholderIcon.setHorizontalAlignment(SwingConstants.CENTER);
            placeholderIcon.setVerticalAlignment(SwingConstants.TOP);
            placeholderIcon.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            rightPanel.add(placeholderIcon, BorderLayout.NORTH);
        }

        card.add(rightPanel, BorderLayout.EAST);
        return card;
    }

    private RoundedPanel createBillableItemsCard(List<BillableItem> items, Long claimId) {
        RoundedPanel card = new RoundedPanel();
        card.setLayout(new BorderLayout(10, 5));
        card.setBackground(Color.WHITE);
        card.setForeground(new Color(220, 220, 220));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        card.setMinimumSize(new Dimension(400, 150));
        card.setPreferredSize(new Dimension(800, 180));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Billable Items");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 16));
        titleLabel.setForeground(new Color(44, 62, 80));

        JLabel claimLabel = new JLabel("From Claim #" + claimId);
        claimLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        claimLabel.setForeground(new Color(127, 140, 141));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(claimLabel, BorderLayout.EAST);

        // Items list
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setOpaque(false);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        for (BillableItem item : items) {
            JPanel itemRow = new JPanel(new BorderLayout());
            itemRow.setOpaque(false);
            itemRow.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

            JLabel descLabel = new JLabel(item.getDescription());
            descLabel.setFont(new Font("Inter", Font.PLAIN, 13));
            descLabel.setForeground(new Color(52, 73, 94));

            JLabel costLabel = new JLabel(currencyFormat.format(item.getCost()));
            costLabel.setFont(new Font("Inter", Font.BOLD, 13));
            costLabel.setForeground(new Color(39, 174, 96));

            itemRow.add(descLabel, BorderLayout.WEST);
            itemRow.add(costLabel, BorderLayout.EAST);
            itemsPanel.add(itemRow);
        }

        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(0, 10));
        contentPanel.setOpaque(false);
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(itemsPanel, BorderLayout.CENTER);

        card.add(contentPanel, BorderLayout.CENTER);

        // Icon
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new Dimension(60, 180));

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/img/doc2.png"));
            Image scaledImage = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            JLabel iconLabel = new JLabel(new ImageIcon(scaledImage));
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
            iconLabel.setVerticalAlignment(SwingConstants.TOP);
            iconLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            rightPanel.add(iconLabel, BorderLayout.NORTH);
        } catch (Exception e) {
            JLabel placeholderIcon = new JLabel("📋");
            placeholderIcon.setFont(new Font("Arial", Font.BOLD, 32));
            placeholderIcon.setForeground(new Color(52, 152, 219));
            placeholderIcon.setHorizontalAlignment(SwingConstants.CENTER);
            placeholderIcon.setVerticalAlignment(SwingConstants.TOP);
            placeholderIcon.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            rightPanel.add(placeholderIcon, BorderLayout.NORTH);
        }

        card.add(rightPanel, BorderLayout.EAST);
        return card;
    }

    private RoundedPanel createWelcomeCard() {
        RoundedPanel card = new RoundedPanel();
        card.setLayout(new BorderLayout(10, 5));
        card.setBackground(Color.WHITE);
        card.setForeground(new Color(220, 220, 220));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        card.setMinimumSize(new Dimension(400, 140));
        card.setPreferredSize(new Dimension(800, 140));

        // Left content panel for text
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setOpaque(false);

        // Title
        JLabel titleLabel = new JLabel("Claim Processing Portal");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 16));
        titleLabel.setForeground(new Color(44, 62, 80));
        leftPanel.add(titleLabel, BorderLayout.NORTH);

        // Instructions
        JPanel instructionPanel = new JPanel();
        instructionPanel.setLayout(new BoxLayout(instructionPanel, BoxLayout.Y_AXIS));
        instructionPanel.setOpaque(false);

        String[] instructions = {
            "Select an appointment from the list to review details",
            "Review appointment and billing information",
            "Generate and submit claim for processing"
        };

        for (String instruction : instructions) {
            JLabel lineLabel = new JLabel(instruction);
            lineLabel.setFont(new Font("Inter", Font.PLAIN, 12));
            lineLabel.setForeground(new Color(52, 73, 94));
            lineLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
            instructionPanel.add(lineLabel);
        }

        leftPanel.add(instructionPanel, BorderLayout.CENTER);
        card.add(leftPanel, BorderLayout.CENTER);

        // Right panel for icon
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new Dimension(60, 140));

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/img/claim.png"));
            Image scaledImage = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            JLabel iconLabel = new JLabel(new ImageIcon(scaledImage));
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
            iconLabel.setVerticalAlignment(SwingConstants.TOP);
            iconLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            rightPanel.add(iconLabel, BorderLayout.NORTH);
        } catch (Exception e) {
            JLabel placeholderIcon = new JLabel("🏥");
            placeholderIcon.setFont(new Font("Arial", Font.BOLD, 32));
            placeholderIcon.setHorizontalAlignment(SwingConstants.CENTER);
            placeholderIcon.setVerticalAlignment(SwingConstants.TOP);
            placeholderIcon.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            rightPanel.add(placeholderIcon, BorderLayout.NORTH);
        }

        card.add(rightPanel, BorderLayout.EAST);
        return card;
    }

    private void initComponentsManual() {
        setLayout(new BorderLayout(10, 0));
        setBackground(Color.WHITE);

        // Header Panel (matching InsuranceApprovalPanel style)
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(247, 247, 247));
        headerPanel.setMaximumSize(new Dimension(32767, 90));
        headerPanel.setMinimumSize(new Dimension(0, 90));
        headerPanel.setPreferredSize(new Dimension(987, 90));
        headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));

        JPanel headerContent = new JPanel();
        headerContent.setOpaque(false);
        headerContent.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Claim Processing Portal");
        titleLabel.setFont(new Font("Inter 18pt", Font.BOLD, 24));
        titleLabel.setForeground(new Color(5, 5, 5));
        headerContent.add(titleLabel, BorderLayout.NORTH);

        JLabel subtitleLabel = new JLabel("Generate claims from completed appointments and submit for validation");
        subtitleLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(102, 102, 102));
        headerContent.add(subtitleLabel, BorderLayout.SOUTH);

        headerPanel.add(headerContent);
        add(headerPanel, BorderLayout.PAGE_START);

        // Main Content Panel with Split Pane
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(247, 247, 247));
        mainPanel.setMinimumSize(new Dimension(900, 700));
        mainPanel.setPreferredSize(new Dimension(903, 700));
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Create Split Pane
        mainSplitPane = new JSplitPane();
        mainSplitPane.setDividerLocation(360);
        mainSplitPane.setDividerSize(0);
        mainSplitPane.setMaximumSize(new Dimension(2147483647, 500));
        mainSplitPane.setMinimumSize(new Dimension(800, 300));
        mainSplitPane.setPreferredSize(new Dimension(1250, 510));

        // Left Panel - Appointments List
        createAppointmentsListPanel();
        mainSplitPane.setLeftComponent(leftPanel);

        // Right Panel - Appointment Details
        createDetailsPanel();
        mainSplitPane.setRightComponent(rightPanel);

        mainPanel.add(mainSplitPane, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void createAppointmentsListPanel() {
        leftPanel = new JPanel();
        leftPanel.setBackground(new Color(247, 247, 247));
        leftPanel.setMaximumSize(new Dimension(300, 32767));
        leftPanel.setMinimumSize(new Dimension(300, 100));
        leftPanel.setPreferredSize(new Dimension(300, 510));
        leftPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 0));

        // Create the appointments list card
        RoundedPanel appointmentsCard = new RoundedPanel();
        appointmentsCard.setBackground(Color.WHITE);
        appointmentsCard.setForeground(new Color(234, 234, 234));
        appointmentsCard.setMinimumSize(new Dimension(330, 600));

        // Form elements
        JLabel appointmentsTitle = new JLabel("Ready for Billing");
        appointmentsTitle.setFont(new Font("Inter 18pt SemiBold", Font.PLAIN, 24));
        appointmentsTitle.setForeground(Color.BLACK);

        JLabel appointmentsSubtitle = new JLabel("Select an appointment to process claim");
        appointmentsSubtitle.setFont(new Font("Inter", Font.PLAIN, 12));
        appointmentsSubtitle.setForeground(new Color(153, 153, 153));

        appointmentList = new JList<>(listModel);
        appointmentList.setFont(new Font("Inter", Font.PLAIN, 12));
        appointmentList.setBackground(Color.WHITE);
        appointmentList.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        JScrollPane listScrollPane = new JScrollPane(appointmentList);
        listScrollPane.setPreferredSize(new Dimension(300, 400));

        refreshButton = new JButton("Refresh List");
        refreshButton.setFont(new Font("Inter 18pt SemiBold", Font.PLAIN, 14));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setBackground(new Color(0, 153, 255));

        // Use GroupLayout
        javax.swing.GroupLayout appointmentsCardLayout = new javax.swing.GroupLayout(appointmentsCard);
        appointmentsCard.setLayout(appointmentsCardLayout);
        appointmentsCardLayout.setHorizontalGroup(
            appointmentsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(appointmentsCardLayout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(appointmentsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(listScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                    .addComponent(refreshButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(appointmentsCardLayout.createSequentialGroup()
                        .addGroup(appointmentsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(appointmentsTitle)
                            .addComponent(appointmentsSubtitle))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(14, 14, 14))
        );
        appointmentsCardLayout.setVerticalGroup(
            appointmentsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(appointmentsCardLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(appointmentsTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(appointmentsSubtitle)
                .addGap(18, 18, 18)
                .addComponent(listScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(refreshButton, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );

        leftPanel.add(appointmentsCard);
    }

    private void createDetailsPanel() {
        rightPanel = new JPanel();
        rightPanel.setBackground(new Color(247, 247, 247));
        rightPanel.setMaximumSize(new Dimension(2147483647, 600));
        rightPanel.setPreferredSize(new Dimension(831, 600));
        rightPanel.setLayout(new BorderLayout(30, 0));

        // Create the details card
        RoundedPanel detailsCard = new RoundedPanel();
        detailsCard.setBackground(Color.WHITE);
        detailsCard.setForeground(new Color(234, 234, 234));
        detailsCard.setMaximumSize(new Dimension(32767, 600));
        detailsCard.setMinimumSize(new Dimension(0, 600));
        detailsCard.setPreferredSize(new Dimension(811, 600));

        // Header elements
        JLabel detailsTitle = new JLabel("Appointment Details");
        detailsTitle.setFont(new Font("Inter 18pt SemiBold", Font.PLAIN, 24));
        detailsTitle.setForeground(Color.BLACK);

        // Action Buttons in header
        JPanel headerButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        headerButtonPanel.setOpaque(false);

        openPatientBillingButton = new JButton("Patient Billing");
        openPatientBillingButton.setFont(new Font("Inter 18pt Medium", Font.PLAIN, 12));
        openPatientBillingButton.setForeground(new Color(102, 102, 102));
        openPatientBillingButton.setBackground(new Color(248, 249, 250));
        openPatientBillingButton.setPreferredSize(new Dimension(120, 26));
        openPatientBillingButton.setBorderPainted(false);
        openPatientBillingButton.setFocusPainted(false);

        openInsurancePanelButton = new JButton("Insurance Portal");
        openInsurancePanelButton.setFont(new Font("Inter 18pt Medium", Font.PLAIN, 12));
        openInsurancePanelButton.setForeground(new Color(102, 102, 102));
        openInsurancePanelButton.setBackground(new Color(248, 249, 250));
        openInsurancePanelButton.setPreferredSize(new Dimension(130, 26));
        openInsurancePanelButton.setBorderPainted(false);
        openInsurancePanelButton.setFocusPainted(false);

        processClaimButton = new JButton("Generate Claim");
        processClaimButton.setFont(new Font("Inter 18pt Medium", Font.PLAIN, 12));
        processClaimButton.setForeground(Color.WHITE);
        processClaimButton.setBackground(new Color(40, 167, 69));
        processClaimButton.setPreferredSize(new Dimension(130, 26));
        processClaimButton.setBorderPainted(false);
        processClaimButton.setFocusPainted(false);
        processClaimButton.setEnabled(false);

        headerButtonPanel.add(openPatientBillingButton);
        headerButtonPanel.add(openInsurancePanelButton);
        headerButtonPanel.add(processClaimButton);

        // Cards container
        detailsPanel = new JPanel();
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));

        JScrollPane detailsScrollPane = new JScrollPane(detailsPanel);
        detailsScrollPane.setBackground(Color.WHITE);
        detailsScrollPane.setBorder(BorderFactory.createEmptyBorder(1, 1, 30, 1));
        detailsScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Add initial welcome card
        showWelcomeCard();

        // Use GroupLayout
        javax.swing.GroupLayout detailsCardLayout = new javax.swing.GroupLayout(detailsCard);
        detailsCard.setLayout(detailsCardLayout);
        detailsCardLayout.setHorizontalGroup(
            detailsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, detailsCardLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(detailsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(detailsCardLayout.createSequentialGroup()
                        .addComponent(detailsTitle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(headerButtonPanel))
                    .addComponent(detailsScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 856, Short.MAX_VALUE))
                .addGap(20, 20, 20))
        );
        detailsCardLayout.setVerticalGroup(
            detailsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(detailsCardLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(detailsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(detailsTitle)
                    .addComponent(headerButtonPanel))
                .addGap(18, 18, 18)
                .addComponent(detailsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
                .addContainerGap())
        );

        rightPanel.add(detailsCard, BorderLayout.CENTER);
    }

    private void onProcessClaim() {
        String selectedValue = appointmentList.getSelectedValue();
        if (selectedValue == null) return;

        Appointment appointmentToBill = appointmentMap.get(selectedValue);

        // 1. Create the initial Claim object and save it.
        Claim newClaim = claimService.createClaimFromAppointment(appointmentToBill);
        if (newClaim == null) {
            JOptionPane.showMessageDialog(this, "Could not create the initial claim record in the database.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Successfully created new Claim #" + newClaim.getId() + ".\nStarting validation process.", "Claim Created", JOptionPane.INFORMATION_MESSAGE);

        // 2. Create and execute the first handler in the chain.
        ClaimHandler validationHandler = new ValidationHandler();
        validationHandler.processClaim(newClaim);

        // 3. Refresh the list.
        loadAppointmentsReadyForBilling();
    }

    private void openInsurancePanel() {
        JDialog insuranceDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Insurance Agent Portal", true);

        InsuranceApprovalPanel insurancePanel = new InsuranceApprovalPanel();

        insuranceDialog.setContentPane(insurancePanel);
        insuranceDialog.pack();
        insuranceDialog.setMinimumSize(new Dimension(800, 600));
        insuranceDialog.setLocationRelativeTo(this);
        insuranceDialog.setVisible(true);

        // After the insurance agent is done, this main list might have changed, so we refresh it.
        loadAppointmentsReadyForBilling();
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
