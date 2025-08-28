package system.ui;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import system.enums.UserRole;
import system.model.User;
import system.service.AuthenticationService;
import system.ui.components.NavItem;
import system.ui.panels.AppointmentPanel;
import system.ui.panels.ClaimProcessingPanel;
import system.ui.panels.DashbaordPanel;
import system.ui.panels.PatientRecordPanel;
import system.ui.panels.PermissionTestPanel;
import system.ui.panels.ReportGeneratorPanel;


public class MainFrame extends javax.swing.JFrame {

    private List<NavItem> navItems = new ArrayList<>();
    private Map<String, javax.swing.JPanel> panels = new HashMap<>();
    private List<String> currentPanelOrder = new ArrayList<>();

    public MainFrame() {
        initComponents();
        SwingUtilities.invokeLater(() -> {
            // Initialize AuthenticationService menu status to match UI state
            AuthenticationService auth = AuthenticationService.getInstance();
            auth.setMenuStatus(true); // Set to collapsed initially

            // 1. Force the panel's visual state to match our boolean variable.
            jPanel1.setPreferredSize(new Dimension(65, jPanel1.getHeight()));

            // 2. Also hide the text label initially since it's collapsed.
            jLabel2.setVisible(false);

            // 3. Tell the container to apply this new size and redraw.
            // Using getParent() is more robust as it tells the layout manager to update.
            jPanel1.getParent().revalidate();
            jPanel1.getParent().repaint();

            jLabel3.setText("Welcome, " + auth.getLoggedInUser().getFirstName());

            setTitle("GlobeMed Healthcare");
            Image icon = Toolkit.getDefaultToolkit().getImage("src/main/resources/img/icon.png");
            setIconImage(icon);

            initializePanels(); // Initialize all panels first
            buildNavigationPanel(); // Use role-based navigation instead of initializeNavItems()
            // Ensure NavItem components are properly initialized with correct visibility
            updateNavItemsTextVisibility();


            // You can set icons here if you have them:
            // navItem1.setIcons(defaultIcon, activeIcon);

        });
    }


    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel5 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel12 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        navItem1 = new system.ui.components.NavItem();
        navItem2 = new system.ui.components.NavItem();
        jPanel4 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1200, 700));
        setPreferredSize(new java.awt.Dimension(1300, 825));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.LINE_AXIS));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(229, 229, 229)));
        jPanel1.setMaximumSize(new java.awt.Dimension(250, 32767));
        jPanel1.setMinimumSize(new java.awt.Dimension(250, 0));
        jPanel1.setPreferredSize(new java.awt.Dimension(250, 825));

        jPanel12.setBackground(new java.awt.Color(255, 255, 255));
        jPanel12.setForeground(new java.awt.Color(255, 255, 51));
        jPanel12.setMaximumSize(new java.awt.Dimension(32767, 66));
        jPanel12.setMinimumSize(new java.awt.Dimension(0, 66));
        jPanel12.setPreferredSize(new java.awt.Dimension(248, 66));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/glob1.png"))); // NOI18N

        jPanel11.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/MNU.png"))); // NOI18N
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(jLabel1)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel1)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 67, Short.MAX_VALUE)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 2, Short.MAX_VALUE))
        );

        //jPanel12.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(204, 204, 204)));

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));
        jPanel6.setLayout(new java.awt.GridLayout(10, 1, 20, 10));

        navItem1.setFont(new java.awt.Font("Inter", 0, 14)); // NOI18N
        navItem1.setName("Hello"); // NOI18N
        jPanel6.add(navItem1);

        navItem2.setFont(new java.awt.Font("SF Pro Display Medium", 0, 14)); // NOI18N
        navItem2.setName("Hello"); // NOI18N
        jPanel6.add(navItem2);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(301, Short.MAX_VALUE))
        );

        jPanel5.add(jPanel1);

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jSplitPane1.setDividerSize(0);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 1, 0, new java.awt.Color(229,229,229)));
        jPanel2.setMaximumSize(new java.awt.Dimension(32767, 65));
        jPanel2.setMinimumSize(new java.awt.Dimension(100, 65));

        jLabel3.setFont(new java.awt.Font("Inter", 0, 18)); // NOI18N
        jLabel3.setText("jLabel3");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel3)
                .addContainerGap(888, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGap(20, 20, 20))
        );

        jSplitPane1.setLeftComponent(jPanel2);

        jPanel3.setBackground(new java.awt.Color(237, 237, 237));
        jPanel3.setLayout(new java.awt.CardLayout());
        jSplitPane1.setRightComponent(jPanel3);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1)
        );

        jPanel5.add(jPanel4);

        getContentPane().add(jPanel5, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private Timer animationTimer;
    private boolean isCollapsed = true;


    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
        if (animationTimer != null && animationTimer.isRunning()) {
            return; // Do nothing if it's already animating.
        }

        AuthenticationService auth = AuthenticationService.getInstance();

        // 2. Determine the target state and width based on the current state.
        // If it's currently collapsed (isCollapsed = true), the target is to be expanded.
        final boolean targetIsCollapsed = !isCollapsed;
        final int targetWidth = targetIsCollapsed ? 65 : 250;
        final int currentWidth = jPanel1.getWidth();

        // 3. Set up animation parameters.
        final int duration = 100; // Total animation time in milliseconds
        final int delay = 10;     // Delay between each animation step in milliseconds
        final int steps = duration / delay;
        final double increment = (double) (targetWidth - currentWidth) / steps;

        // --- Visual Logic ---
        // Immediately show the label if we are expanding. It looks better this way.
        if (!targetIsCollapsed) {
            jLabel2.setVisible(true);
        }

        // 4. Create and configure the Timer for the animation.
        animationTimer = new Timer(delay, new ActionListener() {
            private double animatedWidth = currentWidth;

            @Override
            public void actionPerformed(ActionEvent e) {
                animatedWidth += increment;

                // Clamp the value to the target to prevent overshooting due to double precision.
                if ((increment > 0 && animatedWidth > targetWidth) || (increment < 0 && animatedWidth < targetWidth)) {
                    animatedWidth = targetWidth;
                }

                // Apply the new width to the panel.
                jPanel1.setPreferredSize(new Dimension((int) animatedWidth, jPanel1.getHeight()));

                // IMPORTANT: Revalidate the PARENT container to force it to apply the new size.
                // This is more reliable than revalidating the component itself.
                jPanel1.getParent().revalidate();
                jPanel1.getParent().repaint();

                // Check if the animation is finished.
                if (animatedWidth == targetWidth) {
                    ((Timer) e.getSource()).stop();

                    // Hide the label only AFTER collapsing is fully finished.
                    if (targetIsCollapsed) {
                        jLabel2.setVisible(false);
                    }

                    // 5. CRITICAL FIX: Update the state variable ONLY when the animation is complete.
                    isCollapsed = targetIsCollapsed;

                    // Update AuthenticationService with the new menu status
                    auth.setMenuStatus(targetIsCollapsed);

                    // Notify all NavItem components to update their text visibility and width
                    updateNavItemsTextVisibility();
                }
            }
        });

        // 6. Start the animation.
        animationTimer.start();

    }//GEN-LAST:event_jLabel1MouseClicked

    // Method to update text visibility for all NavItem components
    private void updateNavItemsTextVisibility() {
        for (system.ui.components.NavItem navItem : navItems) {
            navItem.updateTextVisibility();
        }
    }


    private void initializeNavItems() {
        // Clear existing nav items from the panel
        jPanel6.removeAll();
        navItems.clear();

        String[][] navItemData = {
                {"Dashboard", "/img/dashboard.png", "/img/dashboard.png", "DASHBOARD"},
                {"Patient Records", "/img/health-report.png", "/img/health-report.png", "PATIENT_RECORDS"},
                {"Appointments", "/img/calendar.png", "/img/calendar.png", "APPOINTMENTS"},
                {"Billing & Claims", "/img/bill.png", "/img/bill.png", "BILLING"},
                {"Permissions", "/img/safety.png", "/img/safety.png", "PERMISSIONS"},
                {"Reports", "/img/report.png", "/img/report.png", "REPORTS"}
        };

        // Create and add nav items with icons
        for (int i = 0; i < navItemData.length; i++) {
            String[] itemData = navItemData[i];
            String itemName = itemData[0];
            String defaultIconPath = itemData[1];
            String activeIconPath = itemData[2];

            try {
                // Load icons from resources (remove /src/main/resources prefix)
                javax.swing.ImageIcon defaultIcon = new javax.swing.ImageIcon(getClass().getResource(defaultIconPath));
                javax.swing.ImageIcon activeIcon = new javax.swing.ImageIcon(getClass().getResource(activeIconPath));

                // Create NavItem with text and icons
                system.ui.components.NavItem navItem = new system.ui.components.NavItem(itemName, defaultIcon, activeIcon);
                navItem.setFont(new java.awt.Font("Inter", 0, 16));
                navItem.setName(itemName);

                // Add click listener for mutual exclusion
                final int itemIndex = i;
                navItem.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        setActiveNavItem(itemIndex);
                    }
                });

                // Don't set active here - we'll do it properly after all items are created
                // Add to list and panel
                navItems.add(navItem);
                jPanel6.add(navItem);
            } catch (Exception e) {
                // Fallback: create without icons if icon loading fails
                system.ui.components.NavItem navItem = new system.ui.components.NavItem();
                navItem.setFont(new java.awt.Font("Inter", 0, 16));
                navItem.setText(itemName);
                navItem.setName(itemName);

                // Add click listener for mutual exclusion
                final int itemIndex = i;
                navItem.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        setActiveNavItem(itemIndex);
                    }
                });

                // Don't set active here - we'll do it properly after all items are created
                navItems.add(navItem);
                jPanel6.add(navItem);

                System.err.println("Could not load icons for " + itemName + ": " + e.getMessage());
            }
        }

        // Refresh the panel
        jPanel6.revalidate();
        jPanel6.repaint();

        // Set Dashboard (index 0) as active by default using the proper method
        if (!navItems.isEmpty()) {
            setActiveNavItem(0);
        }
    }


    private void setActiveNavItem(int activeIndex) {
        // Deactivate all nav items
        for (int i = 0; i < navItems.size(); i++) {
            system.ui.components.NavItem navItem = navItems.get(i);
            navItem.setActive(i == activeIndex);
        }

        // Optional: Handle navigation logic here based on activeIndex
        switch (activeIndex) {
            case 0: // Dashboard
                // Handle dashboard navigation
                break;
            case 1: // Patients
                // Handle patients navigation
                break;
            case 2: // Appointments
                // Handle appointments navigation
                break;
            case 3: // Reports
                // Handle reports navigation
                break;
            case 4: // Settings
                // Handle settings navigation
                break;
        }
    }


    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JSplitPane jSplitPane1;
    private system.ui.components.NavItem navItem1;
    private system.ui.components.NavItem navItem2;
    // End of variables declaration//GEN-END:variables

    /**
     * Initialize all panels and add them to jPanel3 using CardLayout
     */
    private void initializePanels() {
        // Create instances of all panels
        panels.put("DASHBOARD", new DashbaordPanel());
        panels.put("PATIENT_RECORDS", new PatientRecordPanel());
        panels.put("APPOINTMENTS", new AppointmentPanel());
        panels.put("BILLING", new ClaimProcessingPanel());
        panels.put("PERMISSIONS", new PermissionTestPanel());
        panels.put("REPORTS", new ReportGeneratorPanel());

        // Add all panels to jPanel3 with CardLayout
        for (Map.Entry<String, javax.swing.JPanel> entry : panels.entrySet()) {
            jPanel3.add(entry.getValue(), entry.getKey());
        }
    }

    /**
     * Switch to the specified panel using CardLayout
     */
    private void switchToPanel(String panelName) {
        CardLayout cardLayout = (CardLayout) jPanel3.getLayout();
        cardLayout.show(jPanel3, panelName);

        // Update the title label if needed
        String displayName = getPanelDisplayName(panelName);
        // You can update jLabel3 or another label to show current panel name
        // jLabel3.setText("Current Panel: " + displayName);
    }

    /**
     * Get the display name for a panel identifier
     */
    private String getPanelDisplayName(String panelName) {
        switch (panelName) {
            case "DASHBOARD": return "Dashboard";
            case "PATIENT_RECORDS": return "Patient Records";
            case "APPOINTMENTS": return "Appointments";
            case "BILLING": return "Billing & Claims";
            case "PERMISSIONS": return "Permissions";
            case "REPORTS": return "Reports";
            default: return panelName;
        }
    }

    private void buildNavigationPanel() {
        // 1. Get the currently logged-in user and their role
        User currentUser = AuthenticationService.getInstance().getLoggedInUser();
        UserRole role = currentUser.getRole();

        // 2. A central permission map or logic block
        // This could also be implemented using the Strategy pattern for a cleaner design!
        Map<UserRole, List<String>> rolePermissions = new HashMap<>();
        rolePermissions.put(UserRole.DOCTOR, List.of("APPOINTMENTS","REPORTS"));
        rolePermissions.put(UserRole.NURSE, List.of("DASHBOARD", "PATIENT_RECORDS", "APPOINTMENTS", "PERMISSIONS"));
        rolePermissions.put(UserRole.PHARMACIST, List.of("DASHBOARD", "PATIENT_RECORDS", "PERMISSIONS"));
        rolePermissions.put(UserRole.ADMIN, List.of("DASHBOARD", "APPOINTMENTS", "BILLING", "PERMISSIONS", "REPORTS"));

        // 3. Get the list of allowed panels for the current user's role
        List<String> allowedPanels = rolePermissions.get(role);

        // Handle case where role is not found in permissions map
        if (allowedPanels == null) {
            allowedPanels = List.of("DASHBOARD"); // Default to dashboard only
        }

        // Clear existing nav items from the panel
        jPanel6.removeAll();
        navItems.clear();
        currentPanelOrder.clear(); // Clear the current panel order

        String[][] navItemData = {
                {"Dashboard", "/img/dashboard.png", "/img/dashboard.png", "DASHBOARD"},
                {"Patient Records", "/img/health-report.png", "/img/health-report.png", "PATIENT_RECORDS"},
                {"Appointments", "/img/calendar.png", "/img/calendar.png", "APPOINTMENTS"},
                {"Billing & Claims", "/img/bill.png", "/img/bill.png", "BILLING"},
                {"Permissions", "/img/safety.png", "/img/safety.png", "PERMISSIONS"},
                {"Reports", "/img/report.png", "/img/report.png", "REPORTS"}
        };

        // 4. Iterate through the master navItemData and only create buttons for allowed panels
        int itemIndex = 0;
        for (String[] navItem : navItemData) {
            String itemName = navItem[0];
            String defaultIconPath = navItem[1];
            String activeIconPath = navItem[2];
            String panelName = navItem[3]; // e.g., "PATIENT_RECORDS"

            if (allowedPanels.contains(panelName)) {
                // Track the panel order for navigation
                currentPanelOrder.add(panelName);

                try {
                    // Load icons from resources
                    javax.swing.ImageIcon defaultIcon = new javax.swing.ImageIcon(getClass().getResource(defaultIconPath));
                    javax.swing.ImageIcon activeIcon = new javax.swing.ImageIcon(getClass().getResource(activeIconPath));

                    // Create NavItem with text and icons
                    system.ui.components.NavItem navItemComponent = new system.ui.components.NavItem(itemName, defaultIcon, activeIcon);
                    navItemComponent.setFont(new java.awt.Font("Geist Medium", java.awt.Font.PLAIN, 16));
                    navItemComponent.setName(itemName);

                    // Add click listener for mutual exclusion and panel switching
                    final int currentIndex = itemIndex;
                    final String finalPanelName = panelName;
                    navItemComponent.addMouseListener(new java.awt.event.MouseAdapter() {
                        @Override
                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                            setActiveNavItem(currentIndex);
                            switchToPanel(finalPanelName); // Switch to the corresponding panel
                        }
                    });

                    // Add to list and panel
                    navItems.add(navItemComponent);
                    jPanel6.add(navItemComponent);
                    itemIndex++;

                } catch (Exception e) {
                    // Fallback: create without icons if icon loading fails
                    system.ui.components.NavItem navItemComponent = new system.ui.components.NavItem();
                    navItemComponent.setFont(new java.awt.Font("Geist Medium", java.awt.Font.PLAIN, 16));
                    navItemComponent.setText(itemName);
                    navItemComponent.setName(itemName);

                    // Add click listener for mutual exclusion and panel switching
                    final int currentIndex = itemIndex;
                    final String finalPanelName = panelName;
                    navItemComponent.addMouseListener(new java.awt.event.MouseAdapter() {
                        @Override
                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                            setActiveNavItem(currentIndex);
                            switchToPanel(finalPanelName); // Switch to the corresponding panel
                        }
                    });

                    navItems.add(navItemComponent);
                    jPanel6.add(navItemComponent);
                    itemIndex++;

                    System.err.println("Could not load icons for " + itemName + ": " + e.getMessage());
                }
            }
        }

        // Refresh the panel
        jPanel6.revalidate();
        jPanel6.repaint();

        // Set the first available item as active by default and show its panel
        if (!navItems.isEmpty()) {
            setActiveNavItem(0);
            switchToPanel(currentPanelOrder.get(0)); // Show the first panel
        }
    }
}
