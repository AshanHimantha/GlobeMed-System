package system.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;

public class RoundedTextField extends JTextField {
    private int radius = 15; // default radius
    private Color borderColor = new Color(219, 219, 219);
    private Color focusColor = new Color(0, 120, 215); // blue highlight

    // Placeholder functionality
    private String placeholder = "";
    private Color placeholderColor = new Color(150, 150, 150);
    private boolean isShowingPlaceholder = false;

    // --- Constructors ---
    public RoundedTextField() {
        this(20, 10); // default values for GUI Builder
    }

    public RoundedTextField(int columns, int radius) {
        super(columns);
        this.radius = radius;
        setOpaque(false);       // needed for custom background
        setDragEnabled(true);   // enable text drag & drop
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // padding inside

        // Add focus listeners for placeholder functionality
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (isShowingPlaceholder) {
                    setText("");
                    setForeground(Color.BLACK);
                    isShowingPlaceholder = false;
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (getText().isEmpty() && !placeholder.isEmpty()) {
                    showPlaceholder();
                }
            }
        });
    }

    // --- Custom painting ---
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // background
        g2.setColor(getBackground());
        g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));

        // clip for text
        g2.setClip(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius));

        super.paintComponent(g2);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // border color depends on focus
        if (isFocusOwner()) {
            g2.setColor(focusColor);
        } else {
            g2.setColor(borderColor);
        }

        g2.setStroke(new BasicStroke(1));
        g2.draw(new RoundRectangle2D.Float(1, 1, getWidth() - 2, getHeight() - 2, radius, radius));
        g2.dispose();
    }

    // --- Getters / Setters for NetBeans Properties ---
    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
        repaint();
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        repaint();
    }

    public Color getFocusColor() {
        return focusColor;
    }

    public void setFocusColor(Color focusColor) {
        this.focusColor = focusColor;
        repaint();
    }

    // --- Placeholder methods ---
    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder != null ? placeholder : "";
        if (getText().isEmpty() && !isFocusOwner()) {
            showPlaceholder();
        }
    }

    public Color getPlaceholderColor() {
        return placeholderColor;
    }

    public void setPlaceholderColor(Color placeholderColor) {
        this.placeholderColor = placeholderColor;
        if (isShowingPlaceholder) {
            setForeground(placeholderColor);
        }
    }

    private void showPlaceholder() {
        setText(placeholder);
        setForeground(placeholderColor);
        isShowingPlaceholder = true;
    }

    @Override
    public String getText() {
        return isShowingPlaceholder ? "" : super.getText();
    }

    @Override
    public void setText(String text) {
        super.setText(text);
        if (text == null || text.isEmpty()) {
            if (!isFocusOwner() && !placeholder.isEmpty()) {
                showPlaceholder();
            }
        } else {
            isShowingPlaceholder = false;
            setForeground(Color.BLACK);
        }
    }
}
