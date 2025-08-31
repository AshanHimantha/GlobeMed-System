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
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;
import java.awt.Image;
import system.enums.ClaimStatus;
import system.model.Claim;
import system.patterns.chain.ClaimHandler;
import system.patterns.chain.InsuranceApprovalHandler;
import system.service.ClaimService;
import system.ui.components.RoundedPanel;

/**
 *
 * @author User
 */
public class InsuranceApprovalPanel extends javax.swing.JPanel {

    private final ClaimService claimService;
    private final DefaultListModel<String> listModel;
    private final Map<String, Claim> claimMap;

    // --- UI Components ---
    private JList<String> claimList;
    private JPanel detailsPanel; // Replace JTextArea with JPanel for cards
    private JButton approveButton;
    private JButton refreshButton;
    private JSplitPane mainSplitPane;
    private JPanel leftPanel;
    private JPanel rightPanel;

    public InsuranceApprovalPanel() {
        this.claimService = new ClaimService();
        this.listModel = new DefaultListModel<>();
        this.claimMap = new HashMap<>();

        initComponentsManual(); // Build the UI

        // Add event listeners
        claimList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) onClaimSelected();
        });
        refreshButton.addActionListener(e -> loadPendingClaims());
        
        ActionListener processAction = e -> onProcessClaim();
        approveButton.addActionListener(processAction);


        loadPendingClaims();
    }
    
    private void loadPendingClaims() {
        listModel.clear();
        claimMap.clear();
        showWelcomeCard();
        setButtonsEnabled(false);

        // Fetch claims that are waiting for this specific step in the chain
        List<Claim> pendingClaims = claimService.findClaimsByStatus(ClaimStatus.PENDING_INSURANCE_APPROVAL);
        
        if (pendingClaims.isEmpty()) {
            listModel.addElement("No claims are currently pending insurance approval.");
            claimList.setEnabled(false);
        } else {
            claimList.setEnabled(true);
            for (Claim claim : pendingClaims) {
                String displayText = String.format("Claim #%d - %s (Total: Rs.%.2f)",
                    claim.getId(),
                    claim.getAppointment().getPatient().getName(),
                    claim.getTotalAmount());
                listModel.addElement(displayText);
                claimMap.put(displayText, claim);
            }
        }
    }

    private void onClaimSelected() {
        String selectedValue = claimList.getSelectedValue();
        if (selectedValue == null || !claimMap.containsKey(selectedValue)) {
            setButtonsEnabled(false);
            showWelcomeCard();
            return;
        }

        Claim claim = claimMap.get(selectedValue);
        displayClaimDetailsCards(claim);
        setButtonsEnabled(true);
    }

    private void displayClaimDetailsCards(Claim claim) {
        detailsPanel.removeAll();

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

        // Claim Overview Card
        RoundedPanel overviewCard = createClaimInfoCard(
            "Claim Overview",
            "Claim #" + claim.getId(),
            "Insurance Authorization: " + claim.getInsuranceAuthorizationId(),
            "claim.png"
        );
        detailsPanel.add(overviewCard);
        detailsPanel.add(javax.swing.Box.createVerticalStrut(10)); // Add gap

        // Patient Information Card
        RoundedPanel patientCard = createClaimInfoCard(
            "Patient Information",
            claim.getAppointment().getPatient().getName(),
            "Patient ID: " + claim.getAppointment().getPatient().getPatientId(),
            "health-insurance.png"
        );
        detailsPanel.add(patientCard);
        detailsPanel.add(javax.swing.Box.createVerticalStrut(10)); // Add gap

        // Appointment Details Card
        RoundedPanel appointmentCard = createClaimInfoCard(
            "Appointment Details",
            "Appointment #" + claim.getAppointment().getId(),
            "Service: " + claim.getAppointment().getServiceName(),
            "appointment.png"
        );
        detailsPanel.add(appointmentCard);
        detailsPanel.add(javax.swing.Box.createVerticalStrut(10)); // Add gap

        // Financial Information Card
        RoundedPanel financialCard = createClaimInfoCard(
            "Financial Details",
            currencyFormat.format(claim.getTotalAmount()),
            "Total Amount for Review",
            "accounting.png"
        );
        detailsPanel.add(financialCard);

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

    private RoundedPanel createClaimInfoCard(String title, String mainInfo, String subtitle, String iconName) {
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

        // Right panel for icon at top right
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
        JLabel titleLabel = new JLabel("Insurance Claim Review");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 16));
        titleLabel.setForeground(new Color(44, 62, 80));
        leftPanel.add(titleLabel, BorderLayout.NORTH);

        // Instructions
        JPanel instructionPanel = new JPanel();
        instructionPanel.setLayout(new BoxLayout(instructionPanel, BoxLayout.Y_AXIS));
        instructionPanel.setOpaque(false);

        String[] instructions = {
            "Select a claim from the list to review details",
            "Review claim information in detail cards",
            "Process claim using action buttons below"
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
            ImageIcon icon = new ImageIcon(getClass().getResource("/img/safety.png"));
            Image scaledImage = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            JLabel iconLabel = new JLabel(new ImageIcon(scaledImage));
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
            iconLabel.setVerticalAlignment(SwingConstants.TOP);
            iconLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            rightPanel.add(iconLabel, BorderLayout.NORTH);
        } catch (Exception e) {
            JLabel placeholderIcon = new JLabel("🛡️");
            placeholderIcon.setFont(new Font("Arial", Font.BOLD, 32));
            placeholderIcon.setHorizontalAlignment(SwingConstants.CENTER);
            placeholderIcon.setVerticalAlignment(SwingConstants.TOP);
            placeholderIcon.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            rightPanel.add(placeholderIcon, BorderLayout.NORTH);
        }

        card.add(rightPanel, BorderLayout.EAST);
        return card;
    }
    
    private void onProcessClaim() {
        String selectedValue = claimList.getSelectedValue();
        if (selectedValue == null) return;
        
        Claim claimToProcess = claimMap.get(selectedValue);

        // --- CHAIN OF RESPONSIBILITY IN ACTION (Step 2) ---
        // 1. Create the handler for this specific step.
        ClaimHandler insuranceHandler = new InsuranceApprovalHandler();
        
        // 2. Process the claim. The handler will show its own interactive dialog.
        insuranceHandler.processClaim(claimToProcess);
        
        // 3. Refresh the list. The claim should now be gone from this queue.
        loadPendingClaims();
    }
    
    private void setButtonsEnabled(boolean enabled) {
        approveButton.setEnabled(enabled);

    }

    private void initComponentsManual() {
        setLayout(new BorderLayout(10, 0));
        setBackground(Color.WHITE);

        // Header Panel (matching AppointmentPanel style)
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(247, 247, 247));
        headerPanel.setMaximumSize(new Dimension(32767, 90));
        headerPanel.setMinimumSize(new Dimension(0, 90));
        headerPanel.setPreferredSize(new Dimension(987, 90));
        headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));

        JPanel headerContent = new JPanel();
        headerContent.setOpaque(false);
        headerContent.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Insurance Approval Portal");
        titleLabel.setFont(new Font("Inter 18pt", Font.BOLD, 24));
        titleLabel.setForeground(new Color(5, 5, 5));
        headerContent.add(titleLabel, BorderLayout.NORTH);

        JLabel subtitleLabel = new JLabel("Review and process claims pending insurance approval");
        subtitleLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(102, 102, 102));
        headerContent.add(subtitleLabel, BorderLayout.SOUTH);

        headerPanel.add(headerContent);
        add(headerPanel, BorderLayout.PAGE_START);

        // Main Content Panel with Split Pane (matching AppointmentPanel)
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

        // Left Panel - Claims List
        createClaimsListPanel();
        mainSplitPane.setLeftComponent(leftPanel);

        // Right Panel - Claim Details (replacing text area with cards)
        createDetailsPanel();
        mainSplitPane.setRightComponent(rightPanel);

        mainPanel.add(mainSplitPane, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void createClaimsListPanel() {
        leftPanel = new JPanel();
        leftPanel.setBackground(new Color(247, 247, 247));
        leftPanel.setMaximumSize(new Dimension(300, 32767));
        leftPanel.setMinimumSize(new Dimension(300, 100));
        leftPanel.setPreferredSize(new Dimension(300, 510));
        leftPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 0));

        // Create the claims list card
        RoundedPanel claimsCard = new RoundedPanel();
        claimsCard.setBackground(Color.WHITE);
        claimsCard.setForeground(new Color(234, 234, 234));
        claimsCard.setMinimumSize(new Dimension(330, 600));

        // Form elements
        JLabel claimsTitle = new JLabel("Pending Claims");
        claimsTitle.setFont(new Font("Inter 18pt SemiBold", Font.PLAIN, 24));
        claimsTitle.setForeground(Color.BLACK);

        JLabel claimsSubtitle = new JLabel("Select a claim to review details");
        claimsSubtitle.setFont(new Font("Inter", Font.PLAIN, 12));
        claimsSubtitle.setForeground(new Color(153, 153, 153));

        claimList = new JList<>(listModel);
        claimList.setFont(new Font("Inter", Font.PLAIN, 12));
        claimList.setBackground(Color.WHITE);
        claimList.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));

        JScrollPane listScrollPane = new JScrollPane(claimList);
        listScrollPane.setPreferredSize(new Dimension(300, 400));

        refreshButton = new JButton("Refresh List");
        refreshButton.setFont(new Font("Inter 18pt SemiBold", Font.PLAIN, 14));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setBackground(new Color(0, 153, 255));

        // Use GroupLayout like AppointmentPanel
        javax.swing.GroupLayout claimsCardLayout = new javax.swing.GroupLayout(claimsCard);
        claimsCard.setLayout(claimsCardLayout);
        claimsCardLayout.setHorizontalGroup(
            claimsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(claimsCardLayout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(claimsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(listScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                    .addComponent(refreshButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(claimsCardLayout.createSequentialGroup()
                        .addGroup(claimsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(claimsTitle)
                            .addComponent(claimsSubtitle))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(14, 14, 14))
        );
        claimsCardLayout.setVerticalGroup(
            claimsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(claimsCardLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(claimsTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(claimsSubtitle)
                .addGap(18, 18, 18)
                .addComponent(listScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 450, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(refreshButton, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );

        leftPanel.add(claimsCard);
    }

    private void createDetailsPanel() {
        rightPanel = new JPanel();
        rightPanel.setBackground(new Color(247, 247, 247));
        rightPanel.setMaximumSize(new Dimension(2147483647, 600));
        rightPanel.setPreferredSize(new Dimension(831, 600));
        rightPanel.setLayout(new BorderLayout(30, 0));

        // Create the details card (replacing text area)
        RoundedPanel detailsCard = new RoundedPanel();
        detailsCard.setBackground(Color.WHITE);
        detailsCard.setForeground(new Color(234, 234, 234));
        detailsCard.setMaximumSize(new Dimension(32767, 600));
        detailsCard.setMinimumSize(new Dimension(0, 600));
        detailsCard.setPreferredSize(new Dimension(811, 600));

        // Header elements
        JLabel detailsTitle = new JLabel("Claim Details");
        detailsTitle.setFont(new Font("Inter 18pt SemiBold", Font.PLAIN, 24));
        detailsTitle.setForeground(Color.BLACK);

        // Action Buttons in header
        JPanel headerButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        headerButtonPanel.setOpaque(false);

      

        approveButton = new JButton("Process");
        approveButton.setFont(new Font("Inter 18pt Medium", Font.PLAIN, 12));
        approveButton.setForeground(Color.WHITE);
        approveButton.setBackground(new Color(40, 167, 69));
        approveButton.setPreferredSize(new Dimension(80, 26));
        approveButton.setBorderPainted(false);
        approveButton.setFocusPainted(false);


        headerButtonPanel.add(approveButton);

        // Cards container (replacing text area)
        detailsPanel = new JPanel();
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));

        JScrollPane detailsScrollPane = new JScrollPane(detailsPanel);
        detailsScrollPane.setBackground(Color.WHITE);
        detailsScrollPane.setBorder(BorderFactory.createEmptyBorder(1, 1, 30, 1));
        detailsScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Add initial welcome card
        showWelcomeCard();

        // Use GroupLayout exactly like AppointmentPanel
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

