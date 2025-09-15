package system.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Image;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.BoxLayout;
import system.model.Appointment;
import system.model.Claim;
import system.patterns.visitor.ActivityReportVisitor;
import system.patterns.visitor.FinancialReportVisitor;
import system.patterns.visitor.ReportVisitor;
import system.patterns.visitor.Visitable;
import system.service.AppointmentService;
import system.service.ClaimService;
import system.ui.components.RoundedPanel;

/**
 *
 * @author User
 */
public class ReportGeneratorPanel extends javax.swing.JPanel {

    // --- Services to fetch our data "elements" ---
    private final AppointmentService appointmentService;
    private final ClaimService claimService;

    // --- UI Components ---
    private JComboBox<String> reportTypeComboBox;
    private JButton generateButton;
    private JSpinner fromDateSpinner;
    private JSpinner toDateSpinner;
    private JPanel cardsPanel;
    private JSplitPane mainSplitPane;
    private JPanel formPanel;
    private JPanel resultsPanel;

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

        // Get date range
        Date fromDate = (Date) fromDateSpinner.getValue();
        Date toDate = (Date) toDateSpinner.getValue();

        if (fromDate.after(toDate)) {
            JOptionPane.showMessageDialog(this, "From date must be before or equal to the To date.", "Invalid Date Range", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Clear previous results and show loading
        cardsPanel.removeAll();
        cardsPanel.add(createLoadingCard());
        cardsPanel.revalidate();
        cardsPanel.repaint();

        // Convert dates to LocalDate for filtering
        LocalDate fromLocalDate = fromDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate toLocalDate = toDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        // --- VISITOR PATTERN WITH DATE FILTERING ---
        ReportVisitor visitor;
        if ("Financial Report".equals(selectedReport)) {
            visitor = new FinancialReportVisitor();
        } else {
            visitor = new ActivityReportVisitor();
        }

        // Gather filtered data elements
        List<Visitable> dataElements = new ArrayList<>();

        // Filter appointments by date range
        List<Appointment> allAppointments = appointmentService.getAllAppointments();
        for (Appointment appointment : allAppointments) {
            LocalDate appointmentDate = appointment.getAppointmentDateTime().toLocalDate();
            if (!appointmentDate.isBefore(fromLocalDate) && !appointmentDate.isAfter(toLocalDate)) {
                dataElements.add(appointment);
            }
        }

        // Filter claims by date range
        List<Claim> allClaims = claimService.getAllClaims();
        for (Claim claim : allClaims) {
            LocalDate claimDate = claim.getClaimDate();
            if (!claimDate.isBefore(fromLocalDate) && !claimDate.isAfter(toLocalDate)) {
                dataElements.add(claim);
            }
        }

        // Apply visitor pattern
        for (Visitable element : dataElements) {
            element.accept(visitor);
        }

        // Display results in card format
        displayReportCards(visitor, selectedReport, dataElements.size());
    }

    // --- Enhanced UI building method to match AppointmentPanel layout ---
    private void initComponentsManual() {
        setLayout(new BorderLayout(10, 0));
        setBackground(Color.WHITE);

        // Header Panel (similar to AppointmentPanel)
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(247, 247, 247));
        headerPanel.setMaximumSize(new Dimension(32767, 90));
        headerPanel.setMinimumSize(new Dimension(0, 90));
        headerPanel.setPreferredSize(new Dimension(987, 90));
        headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));

        JPanel headerContent = new JPanel();
        headerContent.setOpaque(false);
        headerContent.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Report Generator");
        titleLabel.setFont(new Font("Inter 18pt", Font.BOLD, 24));
        titleLabel.setForeground(new Color(5, 5, 5));
        headerContent.add(titleLabel, BorderLayout.NORTH);

        JLabel subtitleLabel = new JLabel("Generate financial and activity reports with date filtering");
        subtitleLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(102, 102, 102));
        headerContent.add(subtitleLabel, BorderLayout.SOUTH);

        headerPanel.add(headerContent);
        add(headerPanel, BorderLayout.PAGE_START);

        // Main Content Panel with Split Pane (similar to AppointmentPanel)
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

        // Left Panel - Report Configuration Form
        createFormPanel();
        mainSplitPane.setLeftComponent(formPanel);

        // Right Panel - Report Results
        createResultsPanel();
        mainSplitPane.setRightComponent(resultsPanel);

        mainPanel.add(mainSplitPane, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    private void createFormPanel() {
        formPanel = new JPanel();
        formPanel.setBackground(new Color(247, 247, 247));
        formPanel.setMaximumSize(new Dimension(300, 32767));
        formPanel.setMinimumSize(new Dimension(300, 100));
        formPanel.setPreferredSize(new Dimension(300, 510));
        formPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 30, 0));

        // Create the form card using GroupLayout like AppointmentPanel
        RoundedPanel formCard = new RoundedPanel();
        formCard.setBackground(Color.WHITE);
        formCard.setForeground(new Color(220, 220, 220)); // Light gray foreground
        formCard.setMinimumSize(new Dimension(330, 600));

        // Form elements
        JLabel formTitle = new JLabel("Generate Report");
        formTitle.setFont(new Font("Inter 18pt SemiBold", Font.PLAIN, 24));
        formTitle.setForeground(Color.BLACK);

        JLabel formSubtitle = new JLabel("Configure and generate system reports");
        formSubtitle.setFont(new Font("Inter", Font.PLAIN, 12));
        formSubtitle.setForeground(new Color(153, 153, 153));

        JLabel reportLabel = new JLabel("Report Type");
        reportLabel.setFont(new Font("Inter 18pt SemiBold", Font.PLAIN, 14));
        reportLabel.setForeground(new Color(51, 51, 51));

        reportTypeComboBox = new JComboBox<>();
        reportTypeComboBox.setFont(new Font("Inter", Font.PLAIN, 12));
        reportTypeComboBox.setBackground(Color.WHITE);

        JLabel fromLabel = new JLabel("From Date");
        fromLabel.setFont(new Font("Inter 18pt SemiBold", Font.PLAIN, 14));
        fromLabel.setForeground(new Color(51, 51, 51));

        fromDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor fromDateEditor = new JSpinner.DateEditor(fromDateSpinner, "dd/MM/yyyy");
        fromDateSpinner.setEditor(fromDateEditor);
        fromDateSpinner.setFont(new Font("Inter", Font.PLAIN, 12));
        Date defaultFromDate = Date.from(LocalDate.now().minusDays(30).atStartOfDay(ZoneId.systemDefault()).toInstant());
        fromDateSpinner.setValue(defaultFromDate);

        JLabel toLabel = new JLabel("To Date");
        toLabel.setFont(new Font("Inter 18pt SemiBold", Font.PLAIN, 14));
        toLabel.setForeground(new Color(51, 51, 51));

        toDateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor toDateEditor = new JSpinner.DateEditor(toDateSpinner, "dd/MM/yyyy");
        toDateSpinner.setEditor(toDateEditor);
        toDateSpinner.setFont(new Font("Inter", Font.PLAIN, 12));
        toDateSpinner.setValue(new Date());

        generateButton = new JButton("Generate Report");
        generateButton.setFont(new Font("Inter 18pt SemiBold", Font.PLAIN, 14));
        generateButton.setForeground(Color.WHITE);
        generateButton.setBackground(new Color(0, 153, 255));

        // Use GroupLayout like AppointmentPanel
        javax.swing.GroupLayout formCardLayout = new javax.swing.GroupLayout(formCard);
        formCard.setLayout(formCardLayout);
        formCardLayout.setHorizontalGroup(
            formCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(formCardLayout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(formCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(reportTypeComboBox, 0, 312, Short.MAX_VALUE)
                    .addComponent(fromDateSpinner)
                    .addComponent(toDateSpinner)
                    .addComponent(generateButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(formCardLayout.createSequentialGroup()
                        .addGroup(formCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(formTitle)
                            .addComponent(formSubtitle)
                            .addComponent(reportLabel)
                            .addComponent(fromLabel)
                            .addComponent(toLabel))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(14, 14, 14))
        );
        formCardLayout.setVerticalGroup(
            formCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(formCardLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(formTitle)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(formSubtitle)
                .addGap(18, 18, 18)
                .addComponent(reportLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(reportTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(fromLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fromDateSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(toLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(toDateSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(generateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );

        formPanel.add(formCard);
    }

    private void createResultsPanel() {
        resultsPanel = new JPanel();
        resultsPanel.setBackground(new Color(247, 247, 247));
        resultsPanel.setMaximumSize(new Dimension(2147483647, 600));
        resultsPanel.setPreferredSize(new Dimension(831, 600));
        resultsPanel.setLayout(new BorderLayout(30, 0));

        // Create the results card exactly like AppointmentPanel's roundedPanel2
        RoundedPanel resultsCard = new RoundedPanel();
        resultsCard.setBackground(Color.WHITE);
        resultsCard.setForeground(new Color(234, 234, 234));
        resultsCard.setMaximumSize(new Dimension(32767, 600));
        resultsCard.setMinimumSize(new Dimension(0, 600));
        resultsCard.setPreferredSize(new Dimension(811, 600));

        // Header elements
        JLabel resultsTitle = new JLabel("Report Results");
        resultsTitle.setFont(new Font("Inter 18pt SemiBold", Font.PLAIN, 24));
        resultsTitle.setForeground(Color.BLACK);

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Inter 18pt Medium", Font.PLAIN, 12));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setBackground(new Color(0, 153, 255));
        refreshButton.addActionListener(e -> onGenerateReport());

        // Cards container
        cardsPanel = new JPanel();
        cardsPanel.setBackground(Color.WHITE);
        cardsPanel.setForeground(Color.WHITE);
        cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));

        JScrollPane cardsScrollPane = new JScrollPane(cardsPanel);
        cardsScrollPane.setBackground(Color.WHITE);
        cardsScrollPane.setBorder(BorderFactory.createEmptyBorder(1, 1, 30, 1));
        cardsScrollPane.setForeground(Color.WHITE);

        // Add initial instruction card
        cardsPanel.add(createInstructionCard());

        // Use GroupLayout exactly like AppointmentPanel's roundedPanel2
        javax.swing.GroupLayout resultsCardLayout = new javax.swing.GroupLayout(resultsCard);
        resultsCard.setLayout(resultsCardLayout);
        resultsCardLayout.setHorizontalGroup(
            resultsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, resultsCardLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(resultsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(resultsCardLayout.createSequentialGroup()
                        .addComponent(resultsTitle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(refreshButton))
                    .addComponent(cardsScrollPane, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 856, Short.MAX_VALUE))
                .addGap(20, 20, 20))
        );
        resultsCardLayout.setVerticalGroup(
            resultsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(resultsCardLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(resultsCardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(resultsTitle)
                    .addComponent(refreshButton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(cardsScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
                .addContainerGap())
        );

        resultsPanel.add(resultsCard, BorderLayout.CENTER);
    }

    private void displayReportCards(ReportVisitor visitor, String reportType, int totalElements) {
        cardsPanel.removeAll();

        List<RoundedPanel> cards;

        if ("Financial Report".equals(reportType)) {
            cards = getFinancialReportCards((FinancialReportVisitor) visitor, totalElements);
        } else {
            cards = getActivityReportCards((ActivityReportVisitor) visitor, totalElements);
        }

        // Add cards with gaps between them
        for (int i = 0; i < cards.size(); i++) {
            RoundedPanel card = cards.get(i);
            card.setAlignmentX(JPanel.LEFT_ALIGNMENT);
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
            cardsPanel.add(card);

            // Add gap between cards (except after the last card)
            if (i < cards.size() - 1) {
                cardsPanel.add(javax.swing.Box.createVerticalStrut(10));
            }
        }

        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private List<RoundedPanel> getFinancialReportCards(FinancialReportVisitor visitor, int totalElements) {
        List<RoundedPanel> cards = new ArrayList<>();
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

        cards.add(createModernMetricCard("Total Revenue",
            currencyFormat.format(visitor.getTotalRevenue()),
            "Revenue from all sources", "profit-up_1.png"));

        cards.add(createModernMetricCard("Direct Payments",
            currencyFormat.format(visitor.getTotalRevenueFromAppointments()),
            visitor.getVisitedAppointments() + " appointments processed", "cash-payment.png"));

        cards.add(createModernMetricCard("Insurance Claims",
            currencyFormat.format(visitor.getTotalRevenueFromClaims()),
            visitor.getVisitedClaims() + " claims processed", "claim.png"));

        Date fromDate = (Date) fromDateSpinner.getValue();
        Date toDate = (Date) toDateSpinner.getValue();
        cards.add(createModernSummaryCard("Report Summary",
            "Total Elements: " + totalElements + "\n" +
            "Appointments: " + visitor.getVisitedAppointments() + "\n" +
            "Claims: " + visitor.getVisitedClaims() + "\n" +
            "Period: " + new java.text.SimpleDateFormat("dd/MM/yyyy").format(fromDate) +
            " to " + new java.text.SimpleDateFormat("dd/MM/yyyy").format(toDate),
            "report.png"));

        return cards;
    }

    private List<RoundedPanel> getActivityReportCards(ActivityReportVisitor visitor, int totalElements) {
        List<RoundedPanel> cards = new ArrayList<>();

        cards.add(createModernMetricCard("Scheduled Appointments",
            String.valueOf(visitor.getScheduledAppointments()),
            "Upcoming appointments", "accounting.png"));

        cards.add(createModernMetricCard("Completed Appointments",
            String.valueOf(visitor.getCompletedAppointments()),
            "Finished appointments", "check.png"));

        cards.add(createModernMetricCard("Pending Claims",
            String.valueOf(visitor.getPendingClaims()),
            "Claims awaiting processing", "online-doctor.png"));

        cards.add(createModernMetricCard("Closed Claims",
            String.valueOf(visitor.getClosedClaims()),
            "Processed claims", "safety.png"));

        cards.add(createModernSummaryCard("Activity Summary",
            "Total Elements: " + totalElements + "\n" +
            "Total Appointments: " + visitor.getTotalAppointments() + "\n" +
            "Total Claims: " + visitor.getTotalClaims(),
            "health-report.png"));

        return cards;
    }

    private RoundedPanel createModernMetricCard(String title, String mainValue, String subtitle, String iconName) {
        RoundedPanel card = new RoundedPanel();
        card.setLayout(new BorderLayout(10, 5));
        card.setBackground(Color.WHITE);
        card.setForeground(new Color(220, 220, 220)); // Light gray foreground
        // Remove border completely
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        // Make cards full width like AppointmentCards
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

        // Main value in center left
        JLabel valueLabel = new JLabel(mainValue);
        valueLabel.setFont(new Font("Inter", Font.BOLD, 28));
        valueLabel.setForeground(new Color(52, 152, 219));
        leftPanel.add(valueLabel, BorderLayout.CENTER);

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

        // Icon at top right
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/img/" + iconName));
            Image scaledImage = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            JLabel iconLabel = new JLabel(new ImageIcon(scaledImage));
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
            iconLabel.setVerticalAlignment(SwingConstants.TOP);
            iconLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            rightPanel.add(iconLabel, BorderLayout.NORTH);
        } catch (Exception e) {
            // Fallback: create a simple colored icon as placeholder
            JLabel placeholderIcon = new JLabel("●");
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

    private RoundedPanel createModernSummaryCard(String title, String content, String iconName) {
        RoundedPanel card = new RoundedPanel();
        card.setLayout(new BorderLayout(10, 5));
        card.setBackground(Color.WHITE);
        card.setForeground(new Color(220, 220, 220)); // Light gray foreground
        // Remove border completely
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        // Make cards full width like AppointmentCards
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        card.setMinimumSize(new Dimension(400, 140));
        card.setPreferredSize(new Dimension(800, 140));

        // Left content panel for text
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setOpaque(false);

        // Title at top left
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 16));
        titleLabel.setForeground(new Color(44, 62, 80));
        leftPanel.add(titleLabel, BorderLayout.NORTH);

        // Content in center left
        String[] lines = content.split("\n");
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        for (int i = 0; i < lines.length && i < 5; i++) {
            JLabel lineLabel = new JLabel(lines[i]);
            lineLabel.setFont(new Font("Inter", Font.PLAIN, 12));
            lineLabel.setForeground(new Color(52, 73, 94));
            lineLabel.setAlignmentX(JLabel.LEFT_ALIGNMENT);
            contentPanel.add(lineLabel);
        }

        leftPanel.add(contentPanel, BorderLayout.CENTER);
        card.add(leftPanel, BorderLayout.CENTER);

        // Right panel for icon at top right
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new Dimension(60, 140));

        // Icon at top right
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/img/" + iconName));
            Image scaledImage = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            JLabel iconLabel = new JLabel(new ImageIcon(scaledImage));
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
            iconLabel.setVerticalAlignment(SwingConstants.TOP);
            iconLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            rightPanel.add(iconLabel, BorderLayout.NORTH);
        } catch (Exception e) {
            // Fallback: create a simple colored shape as placeholder
            JLabel placeholderIcon = new JLabel("◆");
            placeholderIcon.setFont(new Font("Arial", Font.BOLD, 28));
            placeholderIcon.setForeground(new Color(149, 165, 166));
            placeholderIcon.setHorizontalAlignment(SwingConstants.CENTER);
            placeholderIcon.setVerticalAlignment(SwingConstants.TOP);
            placeholderIcon.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            rightPanel.add(placeholderIcon, BorderLayout.NORTH);
        }

        card.add(rightPanel, BorderLayout.EAST);

        return card;
    }

    private RoundedPanel createLoadingCard() {
        RoundedPanel card = new RoundedPanel();
        card.setLayout(new BorderLayout(10, 5));
        card.setBackground(Color.WHITE);
        card.setForeground(new Color(220, 220, 220)); // Light gray foreground
        // Remove border completely
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        card.setMinimumSize(new Dimension(400, 120));
        card.setPreferredSize(new Dimension(800, 120));

        // Loading text on left
        JLabel loadingLabel = new JLabel("Generating report...");
        loadingLabel.setFont(new Font("Inter", Font.BOLD, 16));
        loadingLabel.setForeground(new Color(52, 152, 219));
        card.add(loadingLabel, BorderLayout.CENTER);

        // Loading icon on right
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new Dimension(60, 120));

        JLabel loadingIcon = new JLabel("⟳");
        loadingIcon.setFont(new Font("Arial", Font.BOLD, 32));
        loadingIcon.setForeground(new Color(52, 152, 219));
        loadingIcon.setHorizontalAlignment(SwingConstants.CENTER);
        loadingIcon.setVerticalAlignment(SwingConstants.TOP);
        loadingIcon.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        rightPanel.add(loadingIcon, BorderLayout.NORTH);

        card.add(rightPanel, BorderLayout.EAST);

        return card;
    }

    private RoundedPanel createInstructionCard() {
        RoundedPanel card = new RoundedPanel();
        card.setLayout(new BorderLayout(10, 5));
        card.setBackground(Color.WHITE);
        card.setForeground(new Color(220, 220, 220)); // Light gray foreground
        // Remove border completely
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        card.setMinimumSize(new Dimension(400, 140));
        card.setPreferredSize(new Dimension(800, 140));

        // Left content panel for text
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setOpaque(false);

        // Title at top left
        JLabel titleLabel = new JLabel("Welcome to Reports");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 16));
        titleLabel.setForeground(new Color(44, 62, 80));
        leftPanel.add(titleLabel, BorderLayout.NORTH);

        // Instructions in center left
        JPanel instructionPanel = new JPanel();
        instructionPanel.setLayout(new BoxLayout(instructionPanel, BoxLayout.Y_AXIS));
        instructionPanel.setOpaque(false);

        String[] instructions = {
            "1. Select report type & date range",
            "2. Click Generate to view results",
            "3. Data will appear as cards below"
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

        // Right panel for icon at top right
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);
        rightPanel.setPreferredSize(new Dimension(60, 140));

        // Welcome icon
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/img/dashboard.png"));
            Image scaledImage = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            JLabel iconLabel = new JLabel(new ImageIcon(scaledImage));
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
            iconLabel.setVerticalAlignment(SwingConstants.TOP);
            iconLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            rightPanel.add(iconLabel, BorderLayout.NORTH);
        } catch (Exception e) {
            // Fallback icon
            JLabel placeholderIcon = new JLabel("📊");
            placeholderIcon.setFont(new Font("Arial", Font.BOLD, 32));
            placeholderIcon.setHorizontalAlignment(SwingConstants.CENTER);
            placeholderIcon.setVerticalAlignment(SwingConstants.TOP);
            placeholderIcon.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
            rightPanel.add(placeholderIcon, BorderLayout.NORTH);
        }

        card.add(rightPanel, BorderLayout.EAST);

        return card;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
