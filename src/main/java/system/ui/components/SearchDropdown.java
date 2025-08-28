package system.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;

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
    
    
    public interface SearchListener extends java.util.EventListener {
        void searchPerformed(String query);
    }

 private final RoundedTextField searchField;
    private final JButton actionButton;
    private final JPopupMenu popupMenu;
    private final JList<String> suggestionList;
    private final DefaultListModel<String> listModel;
    private final javax.swing.event.EventListenerList listenerList = new javax.swing.event.EventListenerList();
    private String selectedItem = null;
    
    private enum State { SEARCHING, SELECTED }

public SearchDropdown() {
     setLayout(new BorderLayout(5, 0));

        // --- 1. Initialize Components ---
        searchField = new RoundedTextField(20, 15);
        searchField.setPlaceholder("Type to search...");
        actionButton = new JButton();
        popupMenu = new JPopupMenu();
        listModel = new DefaultListModel<>();
        suggestionList = new JList<>(listModel);
        
        suggestionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(suggestionList);
        scrollPane.setBorder(null);

        popupMenu.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        popupMenu.setFocusable(false); // Prevents the popup from stealing focus
        popupMenu.add(scrollPane);

        add(searchField, BorderLayout.CENTER);
        add(actionButton, BorderLayout.EAST);

        setState(State.SEARCHING); // Set the initial visual state
        addListeners();
    }

  private void addListeners() {
        // The button clears the selection or fires a search for all items
        actionButton.addActionListener(e -> {
            if (selectedItem != null) {
                clearSelection();
            } else {
                fireSearchEvent("");
            }
        });

        // Listens for typing and fires a search event after a short delay
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            private final Timer timer = new Timer(300, ae -> fireSearchEvent(searchField.getText()));

            @Override public void insertUpdate(DocumentEvent e) { restartTimer(); }
            @Override public void removeUpdate(DocumentEvent e) { restartTimer(); }
            @Override public void changedUpdate(DocumentEvent e) { restartTimer(); }

            private void restartTimer() {
                timer.setRepeats(false);
                timer.restart();
            }
        });

        // Manages showing and hiding the popup when the field gains/loses focus
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                fireSearchEvent(searchField.getText());
            }
            @Override
            public void focusLost(FocusEvent e) {
                // Use a short delay to allow a click on the list to register before hiding
                Timer hideTimer = new Timer(200, ae -> popupMenu.setVisible(false));
                hideTimer.setRepeats(false);
                hideTimer.start();
            }
        });

        // Handles when a user clicks an item in the suggestion list
        suggestionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (suggestionList.getSelectedValue() != null) {
                    setSelectedItem(suggestionList.getSelectedValue());
                }
            }
        });
    }

    private void fireSearchEvent(String query) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i += 2) {
            if (listeners[i] == SearchListener.class) {
                ((SearchListener) listeners[i + 1]).searchPerformed(query);
            }
        }
    }


   public void addSearchListener(SearchListener listener) {
        listenerList.add(SearchListener.class, listener);
    }

    public void setPopupItems(List<String> items) {
        SwingUtilities.invokeLater(() -> {
            listModel.clear();
            if (items != null) items.forEach(listModel::addElement);
            
            if (!listModel.isEmpty() && searchField.isFocusOwner()) {
                popupMenu.setPopupSize(searchField.getWidth(), Math.min(items.size(), 8) * 24);
                if (!popupMenu.isVisible()) popupMenu.show(searchField, 0, searchField.getHeight() + 2);
                popupMenu.revalidate();
                popupMenu.repaint();
            } else {
                popupMenu.setVisible(false);
            }
        });
    }
    
    public String getSelectedItem() { return selectedItem; }
    
    public void clearSelection() {
        this.selectedItem = null;
        searchField.setText("");
        setState(State.SEARCHING);
        // Also fire an event to clear any associated lists in the parent
        fireSearchEvent("");
    }

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
            actionButton.setToolTipText("Show options");
        } else { // State.SELECTED
            searchField.setEnabled(false);
            searchField.setForeground(Color.DARK_GRAY);
            actionButton.setText("X");
            actionButton.setToolTipText("Clear selection");
        }
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
