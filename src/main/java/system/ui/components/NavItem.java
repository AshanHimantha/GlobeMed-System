package system.ui.components;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Icon;
import javax.swing.JLabel;
import system.service.AuthenticationService;



public class NavItem extends javax.swing.JPanel {

    private JLabel iconLabel;
    private JLabel textLabel;
    
    
    private boolean active = false;

    private Color bgDefault = Color.WHITE;
    private Color bgActive = new Color(237, 237, 237);
    private Color textDefault = new Color(51, 51, 51);
    private Color textActive = Color.black;
    private Color textInactive = new Color(169, 169, 169); // Light gray for inactive items

    
    private Icon defaultIcon;
    private Icon activeIcon;

    public NavItem() {
        initComponents();      
        setupUI();
         
         
    }
    
     public NavItem(String text, Icon defaultIcon, Icon activeIcon) {
        
         this();
        this.defaultIcon = defaultIcon;
        this.activeIcon = activeIcon;
        setText(text);
        setIcons(defaultIcon, activeIcon);
    }
    
     private void setupUI() {
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        iconLabel = new JLabel();
        textLabel = new JLabel("Text");
        textLabel.setFont(new Font("Geist Medium", Font.PLAIN, 16));
        // Set initial color based on active state - inactive items should be light gray
        textLabel.setForeground(active ? textActive : textInactive);

        // Use FlowLayout with CENTER alignment for collapsed state, LEFT for expanded
        AuthenticationService auth = AuthenticationService.getInstance();
        boolean isCollapsed = auth.getMenuStatus();

        setLayout(new FlowLayout(isCollapsed ? FlowLayout.CENTER : FlowLayout.LEFT, 10, 8) {
            @Override
            public void layoutContainer(java.awt.Container target) {
                super.layoutContainer(target);
                // Calculate vertical centering for all visible components
                int containerHeight = target.getHeight();
                if (containerHeight <= 0) return; // Avoid division by zero

                for (java.awt.Component comp : target.getComponents()) {
                    if (comp.isVisible()) {
                        int compHeight = comp.getPreferredSize().height;
                        int yOffset = Math.max(0, (containerHeight - compHeight) / 2);
                        java.awt.Point location = comp.getLocation();
                        comp.setLocation(location.x, yOffset);
                    }
                }
            }
        });
        add(iconLabel);
        add(textLabel);

        // Click → set this item as active (don't toggle, set as active)
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setActive(true);
                // You may want to add logic here to deactivate other nav items
            }
        });
        
        textLabel.setVisible(!isCollapsed);

        // Set initial width based on collapse state
        if (isCollapsed) {
            setPreferredSize(new java.awt.Dimension(60, 50)); // Set height to reasonable default
            setMaximumSize(new java.awt.Dimension(60, 50));
        } else {
            setPreferredSize(new java.awt.Dimension(200, 50)); // Ensure consistent height
            setMaximumSize(new java.awt.Dimension(250, 50));
        }
    }

   public void setText(String text) {
        if (textLabel != null) {
            textLabel.setText(text);
        }
    }

    public void setIcons(Icon defaultIcon, Icon activeIcon) {
        this.defaultIcon = defaultIcon;
        this.activeIcon = activeIcon;
        if (iconLabel != null) {
            iconLabel.setIcon(defaultIcon);
        }
    }

    public void setActive(boolean state) {
        this.active = state;

        if (active) {
            if (iconLabel != null) iconLabel.setIcon(activeIcon);
            if (textLabel != null) textLabel.setForeground(textActive);
        } else {
            if (iconLabel != null) iconLabel.setIcon(defaultIcon);
            if (textLabel != null) textLabel.setForeground(textInactive); // Use light gray for inactive
        }
        repaint();
    }

    public boolean isActive() {
        return active;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(active ? bgActive : bgDefault);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

        g2.dispose();
    }


    public void updateTextVisibility() {
        AuthenticationService auth = AuthenticationService.getInstance();
        boolean isCollapsed = auth.getMenuStatus();
        textLabel.setVisible(!isCollapsed);

        // Store current height to maintain consistency
        int currentHeight = getPreferredSize().height > 0 ? getPreferredSize().height : 50;

        // Update layout alignment without removing components
        FlowLayout currentLayout = (FlowLayout) getLayout();
        currentLayout.setAlignment(isCollapsed ? FlowLayout.CENTER : FlowLayout.LEFT);
        currentLayout.setVgap(5); // Ensure consistent vertical gap

        // Set NavItem size based on collapse state with consistent height
        if (isCollapsed) {
            setPreferredSize(new java.awt.Dimension(60, currentHeight));
            setMaximumSize(new java.awt.Dimension(60, currentHeight));
        } else {
            // Set reasonable expanded width while maintaining height
            setPreferredSize(new java.awt.Dimension(200, currentHeight));
            setMaximumSize(new java.awt.Dimension(250, currentHeight));
        }

        revalidate();
        repaint();
    }

    public void setCollapsed(boolean collapsed) {
        // Update the AuthenticationService status
        AuthenticationService auth = AuthenticationService.getInstance();
        auth.setMenuStatus(collapsed);

        // Update text visibility
        textLabel.setVisible(!collapsed);
        revalidate();
        repaint();
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
