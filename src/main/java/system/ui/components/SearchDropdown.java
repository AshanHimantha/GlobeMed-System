package system.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


/**
 *
 * @author User
 */
public class SearchDropdown extends javax.swing.JPanel {

 private final RoundedTextField searchField;
    private final JButton actionButton;
    private final JPopupMenu popupMenu;
    private final JList<String> suggestionList;
    private final DefaultListModel<String> listModel;

    private List<String> allItems = Collections.emptyList();
    private String selectedItem = null;

    private enum State { SEARCHING, SELECTED }

    public SearchDropdown() {
        setLayout(new BorderLayout(5, 0));
       

        // --- 1. Initialize Components ---
        searchField = new RoundedTextField(20, 15);
        searchField.setPlaceholder("Search...");
        actionButton = new JButton();
        popupMenu = new JPopupMenu();
        listModel = new DefaultListModel<>();
        suggestionList = new JList<>(listModel);
        suggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(suggestionList);
        scrollPane.setBorder(null);

        popupMenu.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        popupMenu.setFocusable(false); // Important: prevents the popup from stealing focus
        popupMenu.add(scrollPane);

        add(searchField, BorderLayout.CENTER);
        add(actionButton, BorderLayout.EAST);

        setState(State.SEARCHING);

        // --- 2. Add SINGLE, CORRECT Listeners ---
        addListeners();
    }
    
    private void addListeners() {
        // ACTION BUTTON LISTENER
        actionButton.addActionListener(e -> {
            if (selectedItem != null) {
                clearSelection();
            } else {
                filterItems(true);
            }
        });

        // DOCUMENT LISTENER (for typing)
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterItems(false); }
            public void removeUpdate(DocumentEvent e) { filterItems(false); }
            public void changedUpdate(DocumentEvent e) { filterItems(false); }
        });

        // FOCUS LISTENER (the flicker-free version)
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                filterItems(true);
            }

            @Override
            public void focusLost(FocusEvent e) {
                // Hide the popup ONLY if the focus is moving to a component
                // that is NOT the popup menu itself.
                if (e.getOppositeComponent() != popupMenu) {
                     popupMenu.setVisible(false);
                }
            }
        });

        // MOUSE LISTENER (for selecting an item)
        suggestionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (suggestionList.getSelectedValue() != null) {
                    setSelectedItem(suggestionList.getSelectedValue());
                }
            }
        });
    }

    // --- Public API Methods ---
    public void setItems(List<String> items) {
        this.allItems = (items != null) ? items : Collections.emptyList();
        clearSelection();
    }
    
    public String getSelectedItem() {
        return this.selectedItem;
    }
    
    public void clearSelection() {
        this.selectedItem = null;
        searchField.setText("");
        setState(State.SEARCHING);
    }
    
    // --- Private Helper Methods ---
    private void setSelectedItem(String item) {
        this.selectedItem = item;
        searchField.setText(item);
        popupMenu.setVisible(false);
        setState(State.SELECTED);
    }
    
    private void setState(State state) {
        if (state == State.SEARCHING) {
            searchField.setEnabled(true);
            searchField.setForeground(Color.BLACK);
            actionButton.setText("▼");
            actionButton.setToolTipText("Show all options");
        } else {
            searchField.setEnabled(false);
            searchField.setForeground(Color.DARK_GRAY);
            actionButton.setText("X");
            actionButton.setToolTipText("Clear selection");
        }
    }

    private void filterItems(boolean showAll) {
        if (allItems.isEmpty()) return;
        
        String query = searchField.getText().trim().toLowerCase();
        List<String> filteredItems = (showAll || query.isEmpty()) ? allItems :
                allItems.stream()
                        .filter(item -> item.toLowerCase().contains(query))
                        .collect(Collectors.toList());
        
        updatePopup(filteredItems);
    }

    private void updatePopup(List<String> items) {
        // Use invokeLater to ensure UI updates happen on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            listModel.clear();
            items.forEach(listModel::addElement);

            if (!listModel.isEmpty() && searchField.isEnabled() && searchField.isShowing()) {
                popupMenu.setPopupSize(searchField.getWidth(), Math.min(items.size(), 8) * 24);
                if (!popupMenu.isVisible()) {
                    popupMenu.show(searchField, 0, searchField.getHeight() + 2);
                }
            } else {
                popupMenu.setVisible(false);
            }
        });
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
