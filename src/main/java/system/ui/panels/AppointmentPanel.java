/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package system.ui.panels;


import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import system.enums.AppointmentType;
import system.enums.UserRole;
import system.model.Appointment;
import system.model.MedicalService;
import system.model.Patient;
import system.model.User;
import system.patterns.mediator.AppointmentMediator;
import system.patterns.mediator.AppointmentScheduler;
import system.service.AppointmentService;
import system.service.AuthenticationService;
import system.service.MedicalServiceService;
import system.service.PatientService;
import system.service.UserService;
import system.ui.components.AppointmentCard;


/**
 *
 * @author User
 */
public class AppointmentPanel extends javax.swing.JPanel {


    private final PatientService patientService;
    private final UserService userService;
    private final AppointmentService appointmentService;
    private final AppointmentMediator appointmentMediator;
    private final MedicalServiceService medicalServiceService;
    private Map<String, Patient> patientMap;
    private Map<String, User> doctorMap;
    private Map<String, MedicalService> serviceMap;
    private Timer refreshTimer;
    private system.ui.components.SearchDropdown serviceSearchDropdown; // Add missing field

    public AppointmentPanel() {
        // Initialize the missing serviceSearchDropdown component BEFORE initComponents
        serviceSearchDropdown = new system.ui.components.SearchDropdown();

        initComponents();
        
       this.patientService = new PatientService();
        this.userService = new UserService();
        this.appointmentService = new AppointmentService();
        this.appointmentMediator = new AppointmentScheduler();
        this.medicalServiceService = new MedicalServiceService();
        this.patientMap = new HashMap<>();
        this.doctorMap = new HashMap<>();
        this.serviceMap = new HashMap<>();
        
        configureUIForRole();
        configureDateTimePicker();
        updateTodayLabel();
        onAppointmentTypeChange();
        setupSearchListeners();
        loadTodaysAppointments();
        startAutoRefresh();
    }

    
    private void startAutoRefresh() {
        // Set the refresh interval in milliseconds (e.g., 30000 ms = 30 seconds)
        int refreshInterval = 30000; 

        // Create the ActionListener that will be executed by the timer
        ActionListener refreshAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("TIMER: Auto-refreshing appointment list...");
                // The timer's action is to simply call our existing data loading method
                loadTodaysAppointments();
            }
        };
         

        // Create and start the timer
        refreshTimer = new Timer(refreshInterval, refreshAction);
        refreshTimer.setInitialDelay(0); // Fire the first time immediately (optional, but good for initial load)
        refreshTimer.start();
        System.out.println("Auto-refresh timer started. Interval: " + refreshInterval / 1000 + " seconds.");
    }
    
    private void setupSearchListeners() {
        searchDropdown1.addSearchListener(query -> {
            List<User> doctors = userService.searchDoctorsByName(query, 20);
            // Preserve existing selected items while updating the map
            Map<String, User> newDoctorMap = doctors.stream()
                .collect(Collectors.toMap(d -> "Dr. " + d.getFirstName() + " " + d.getLastName(), d -> d));

            // Preserve any currently selected doctor that might not be in the new search results
            if (searchDropdown1.getSelectedItem() != null && doctorMap.containsKey(searchDropdown1.getSelectedItem())) {
                newDoctorMap.putIfAbsent(searchDropdown1.getSelectedItem(), doctorMap.get(searchDropdown1.getSelectedItem()));
            }

            doctorMap = newDoctorMap;
            searchDropdown1.setPopupItems(new ArrayList<>(doctors.stream()
                .map(d -> "Dr. " + d.getFirstName() + " " + d.getLastName())
                .collect(Collectors.toList())));
        });

        searchDropdown2.addSearchListener(query -> {
            List<Patient> patients = patientService.searchPatientsByName(query, 20);
            // Preserve existing selected items while updating the map
            Map<String, Patient> newPatientMap = patients.stream()
                .collect(Collectors.toMap(p -> p.getPatientId() + " - " + p.getName(), p -> p));

            // Preserve any currently selected patient that might not be in the new search results
            if (searchDropdown2.getSelectedItem() != null && patientMap.containsKey(searchDropdown2.getSelectedItem())) {
                newPatientMap.putIfAbsent(searchDropdown2.getSelectedItem(), patientMap.get(searchDropdown2.getSelectedItem()));
            }

            patientMap = newPatientMap;
            searchDropdown2.setPopupItems(new ArrayList<>(patients.stream()
                .map(p -> p.getPatientId() + " - " + p.getName())
                .collect(Collectors.toList())));
        });
        
        // Add search listener for medical services
        serviceSearchDropdown.addSearchListener(query -> {
            List<MedicalService> filteredServices = medicalServiceService.getAllServices().stream()
                .filter(s -> s.getType() == AppointmentType.DIAGNOSTIC)
                .filter(s -> s.getName().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());

            // Preserve existing selected items while updating the map
            Map<String, MedicalService> newServiceMap = filteredServices.stream()
                .collect(Collectors.toMap(MedicalService::getName, s -> s));

            // Preserve any currently selected service that might not be in the new search results
            if (serviceSearchDropdown.getSelectedItem() != null && serviceMap.containsKey(serviceSearchDropdown.getSelectedItem())) {
                newServiceMap.putIfAbsent(serviceSearchDropdown.getSelectedItem(), serviceMap.get(serviceSearchDropdown.getSelectedItem()));
            }

            serviceMap = newServiceMap;
            serviceSearchDropdown.setPopupItems(new ArrayList<>(filteredServices.stream()
                .map(MedicalService::getName)
                .collect(Collectors.toList())));
        });

        // Initialize service map with all diagnostic services
        List<MedicalService> allServices = medicalServiceService.getAllServices();
        serviceMap = allServices.stream()
            .filter(s -> s.getType() == AppointmentType.DIAGNOSTIC)
            .collect(Collectors.toMap(MedicalService::getName, s -> s));
    }
    
    

    
    private void configureDateTimePicker() {
        dateTimePicker1.setDateTimePermissive(LocalDateTime.now().plusMinutes(15));
    }
   
  private void configureUIForRole() {
        User currentUser = AuthenticationService.getInstance().getLoggedInUser();
        if (currentUser == null) return;
        UserRole role = currentUser.getRole();

        if (role == UserRole.DOCTOR) {
            jSplitPane1.setVisible(false);
            this.remove(jSplitPane1);
            jPanel4.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
            this.add(jPanel4, BorderLayout.CENTER);
            jLabel1.setText("My Daily Schedule");
            jLabel2.setText("Review and manage your scheduled appointments.");
        } else if (role != UserRole.ADMIN && role != UserRole.NURSE) {
            jSplitPane1.setVisible(false);
            jLabel1.setText("Access Denied");
            jLabel2.setText("You do not have permission to view appointments.");
        }
    }


    
public void loadTodaysAppointments() {
        jPanel5.removeAll();
        User currentUser = AuthenticationService.getInstance().getLoggedInUser();
        if (currentUser == null) return;
        
        List<Appointment> appointmentsToShow;
        if (currentUser.getRole() == UserRole.ADMIN || currentUser.getRole() == UserRole.NURSE) {
            appointmentsToShow = appointmentService.getTodaysAppointments();
        } else if (currentUser.getRole() == UserRole.DOCTOR) {
            appointmentsToShow = appointmentService.getTodaysAppointmentsForDoctor(currentUser);
        } else {
            appointmentsToShow = new ArrayList<>();
        }
        
        for (Appointment appt : appointmentsToShow) {
            jPanel5.add(new AppointmentCard(appt, currentUser, this::loadTodaysAppointments));
        }
        jPanel5.revalidate();
        jPanel5.repaint();
    }
    
private void updateTodayLabel() {
        jLabel11.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")));
    }
    
private void addAppointment() {
    try {
        // --- 1. GATHER DATA ---
        String selectedPatientStr = searchDropdown2.getSelectedItem();
        String selectedTypeStr = (String) jComboBox1.getSelectedItem();
        LocalDateTime selectedDateTime = dateTimePicker1.getDateTimePermissive();

        // --- 2. VALIDATE INPUT ---
        if (selectedPatientStr == null || selectedTypeStr == null || selectedDateTime == null) {
            JOptionPane.showMessageDialog(this, "Patient, Appointment Type, and Date/Time must be selected.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- 3. RETRIEVE OBJECTS ---
        Patient patient = patientMap.get(selectedPatientStr);
        if (patient == null) {
            JOptionPane.showMessageDialog(this, "Selected patient not found. Please search and select a patient again.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Map<String, AppointmentType> typeMap = new HashMap<>();
        typeMap.put("Consultations", AppointmentType.CONSULTATION);
        typeMap.put("Diagnostics", AppointmentType.DIAGNOSTIC);
        typeMap.put("Surgeries", AppointmentType.SURGERY);
        AppointmentType type = typeMap.get(selectedTypeStr);
        if (type == null) {
            JOptionPane.showMessageDialog(this, "Invalid appointment type selected.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

         MedicalService linkedService = null; // Default to null

        // Handle diagnostic appointments - require service selection
        if (type == AppointmentType.DIAGNOSTIC) {
            String selectedServiceStr = serviceSearchDropdown.getSelectedItem();
            if (selectedServiceStr == null) {
                JOptionPane.showMessageDialog(this, "A medical service must be selected for diagnostic appointments.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Get the full MedicalService object from our map
            linkedService = serviceMap.get(selectedServiceStr);
            if (linkedService == null) {
                JOptionPane.showMessageDialog(this, "Selected medical service not found. Please search and select a service again.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        User doctor = null; // Can remain null for Diagnostics
        if (type == AppointmentType.CONSULTATION || type == AppointmentType.SURGERY) {
            String selectedDoctorStr = searchDropdown1.getSelectedItem();
            if (selectedDoctorStr == null) {
                JOptionPane.showMessageDialog(this, "A Doctor must be selected for this appointment type.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            doctor = doctorMap.get(selectedDoctorStr);
            if (doctor == null) {
                JOptionPane.showMessageDialog(this, "Selected doctor not found. Please search and select a doctor again.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // --- 4. CALL THE SIMPLIFIED MEDIATOR ---
        // The UI no longer needs to calculate price or service name. It just provides the core selections.
    boolean success = appointmentMediator.bookAppointment(
        patient,
        doctor,      // Correctly null for Diagnostics
        type,
        selectedDateTime,
        linkedService // Pass the service object (it will be null for non-diagnostics)
    );
        // --- END OF FIX ---

        // --- 5. PROVIDE FEEDBACK ---
        if (success) {
            JOptionPane.showMessageDialog(this, "Appointment booked successfully!");
            loadTodaysAppointments();
            clearForm();
        } else {
            JOptionPane.showMessageDialog(this, "Booking Failed. Please check for scheduling conflicts.", "Mediator Response", JOptionPane.ERROR_MESSAGE);
        }

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "A critical error occurred: " + e.getMessage(), "Application Error", JOptionPane.ERROR_MESSAGE);
    }
}
    
    private void clearForm() {
        searchDropdown1.clearSelection();
        searchDropdown2.clearSelection();
        serviceSearchDropdown.clearSelection();
        jComboBox1.setSelectedIndex(0);
        dateTimePicker1.setDateTimePermissive(LocalDateTime.now().plusHours(1));
    }
    
    private void onAppointmentTypeChange() {
        String selectedType = (String) jComboBox1.getSelectedItem();
        if (selectedType == null) return;
        
        boolean isDiagnostic = "Diagnostics".equals(selectedType);
        
        // Show/hide Doctor selector
        searchDropdown1.setVisible(!isDiagnostic);
        jLabel13.setVisible(!isDiagnostic);

        // Show/hide Service selector
        serviceSearchDropdown.setVisible(isDiagnostic);
        jLabel14.setVisible(isDiagnostic);

        if (isDiagnostic) {
            List<String> diagnosticServices = serviceMap.values().stream()
                    .filter(s -> s.getType() == AppointmentType.DIAGNOSTIC)
                    .map(MedicalService::getName)
                    .collect(Collectors.toList());
            serviceSearchDropdown.setPopupItems(diagnosticServices);
        }
    }
    
   
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        roundedPanel1 = new system.ui.components.RoundedPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        searchDropdown1 = new system.ui.components.SearchDropdown();
        jLabel12 = new javax.swing.JLabel();
        searchDropdown2 = new system.ui.components.SearchDropdown();
        dateTimePicker1 = new com.github.lgooddatepicker.components.DateTimePicker();
        jLabel13 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jPanel4 = new javax.swing.JPanel();
        roundedPanel2 = new system.ui.components.RoundedPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel5 = new javax.swing.JPanel();
        searchbutton = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setLayout(new java.awt.BorderLayout(10, 0));

        jPanel3.setBackground(new java.awt.Color(247, 247, 247));
        jPanel3.setMaximumSize(new java.awt.Dimension(32767, 90));
        jPanel3.setMinimumSize(new java.awt.Dimension(0, 90));
        jPanel3.setPreferredSize(new java.awt.Dimension(987, 90));

        jLabel1.setText("Appointment Scheduling");
        jLabel1.setFont(new java.awt.Font("Inter 18pt", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(5, 5, 5));

        jLabel2.setText("Manage patient appintments and doctor schedules");
        jLabel2.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addContainerGap(967, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        add(jPanel3, java.awt.BorderLayout.PAGE_START);

        jPanel1.setBackground(new java.awt.Color(247, 247, 247));
        jPanel1.setMinimumSize(new java.awt.Dimension(900, 700));
        jPanel1.setPreferredSize(new java.awt.Dimension(903, 700));
        jPanel1.setLayout(new java.awt.CardLayout(10, 10));

        jSplitPane1.setDividerLocation(360);
        jSplitPane1.setDividerSize(0);
        jSplitPane1.setMaximumSize(new java.awt.Dimension(2147483647, 500));
        jSplitPane1.setMinimumSize(new java.awt.Dimension(800, 300));
        jSplitPane1.setPreferredSize(new java.awt.Dimension(1250, 510));

        jPanel2.setBackground(new java.awt.Color(247, 247, 247));
        jPanel2.setMaximumSize(new java.awt.Dimension(300, 32767));
        jPanel2.setMinimumSize(new java.awt.Dimension(300, 100));
        jPanel2.setPreferredSize(new java.awt.Dimension(300, 510));
        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 30, 0));

        roundedPanel1.setBackground(new java.awt.Color(255, 255, 255));
        roundedPanel1.setForeground(new java.awt.Color(234, 234, 234));
        roundedPanel1.setMinimumSize(new java.awt.Dimension(330, 600));

        jLabel3.setText("Book Appointment");
        jLabel3.setBackground(new java.awt.Color(0, 0, 0));
        jLabel3.setFont(new java.awt.Font("Inter 18pt SemiBold", 0, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));

        jLabel4.setText("Schedule a new appointment");
        jLabel4.setBackground(new java.awt.Color(153, 153, 153));
        jLabel4.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(153, 153, 153));

        jLabel5.setText("Patinet Name");
        jLabel5.setBackground(new java.awt.Color(51, 51, 51));
        jLabel5.setFont(new java.awt.Font("Inter 18pt SemiBold", 0, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(51, 51, 51));

        jLabel6.setText("Date and time");
        jLabel6.setBackground(new java.awt.Color(51, 51, 51));
        jLabel6.setFont(new java.awt.Font("Inter 18pt SemiBold", 0, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(51, 51, 51));

        jButton3.setText("Add Appointment");
        jButton3.setBackground(new java.awt.Color(0, 153, 255));
        jButton3.setFont(new java.awt.Font("Inter 18pt SemiBold", 0, 14)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        searchDropdown1.setBackground(new java.awt.Color(255, 255, 255));
        searchDropdown1.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        searchDropdown1.setForeground(new java.awt.Color(102, 102, 102));

        jLabel12.setText("Appointment Type");
        jLabel12.setBackground(new java.awt.Color(51, 51, 51));
        jLabel12.setFont(new java.awt.Font("Inter 18pt SemiBold", 0, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(51, 51, 51));

        searchDropdown2.setBackground(new java.awt.Color(255, 255, 255));
        searchDropdown2.setForeground(new java.awt.Color(102, 102, 102));

        dateTimePicker1.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N

        jLabel13.setText("Doctor");
        jLabel13.setBackground(new java.awt.Color(51, 51, 51));
        jLabel13.setFont(new java.awt.Font("Inter 18pt SemiBold", 0, 14)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(51, 51, 51));

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Consultations", "Diagnostics", "Surgeries" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jLabel14.setText("Search Service");
        jLabel14.setBackground(new java.awt.Color(51, 51, 51));
        jLabel14.setFont(new java.awt.Font("Inter 18pt SemiBold", 0, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(51, 51, 51));

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout roundedPanel1Layout = new javax.swing.GroupLayout(roundedPanel1);
        roundedPanel1.setLayout(roundedPanel1Layout);
        roundedPanel1Layout.setHorizontalGroup(
            roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel1Layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundedPanel1Layout.createSequentialGroup()
                        .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13)
                            .addComponent(jLabel12))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(roundedPanel1Layout.createSequentialGroup()
                        .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dateTimePicker1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(searchDropdown1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(searchDropdown2, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                            .addComponent(jComboBox2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(roundedPanel1Layout.createSequentialGroup()
                                .addGroup(roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel14))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(14, 14, 14))))
        );
        roundedPanel1Layout.setVerticalGroup(
            roundedPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(searchDropdown2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel13)
                .addGap(3, 3, 3)
                .addComponent(searchDropdown1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dateTimePicker1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );

        jPanel2.add(roundedPanel1);

        jSplitPane1.setLeftComponent(jPanel2);

        jPanel4.setBackground(new java.awt.Color(247, 247, 247));
        jPanel4.setMaximumSize(new java.awt.Dimension(2147483647, 600));
        jPanel4.setPreferredSize(new java.awt.Dimension(831, 600));
        jPanel4.setLayout(new java.awt.BorderLayout(30, 0));

        roundedPanel2.setBackground(new java.awt.Color(255, 255, 255));
        roundedPanel2.setForeground(new java.awt.Color(234, 234, 234));
        roundedPanel2.setMaximumSize(new java.awt.Dimension(32767, 600));
        roundedPanel2.setMinimumSize(new java.awt.Dimension(0, 600));
        roundedPanel2.setPreferredSize(new java.awt.Dimension(811, 600));

        jLabel10.setText("Today's Schedule");
        jLabel10.setBackground(new java.awt.Color(0, 0, 0));
        jLabel10.setFont(new java.awt.Font("Inter 18pt SemiBold", 0, 24)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 0, 0));

        jLabel11.setText("Saturday, August 23, 2025");
        jLabel11.setBackground(new java.awt.Color(153, 153, 153));
        jLabel11.setFont(new java.awt.Font("Inter", 0, 12)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(153, 153, 153));

        jScrollPane2.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 30, 1));
        jScrollPane2.setForeground(new java.awt.Color(255, 255, 255));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setForeground(new java.awt.Color(255, 255, 255));
        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.Y_AXIS));
        jScrollPane2.setViewportView(jPanel5);

        searchbutton.setText("Refresh");
        searchbutton.setBackground(new java.awt.Color(0, 153, 255));
        searchbutton.setFont(new java.awt.Font("Inter 18pt Medium", 0, 12)); // NOI18N
        searchbutton.setForeground(new java.awt.Color(255, 255, 255));
        searchbutton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                searchbuttonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout roundedPanel2Layout = new javax.swing.GroupLayout(roundedPanel2);
        roundedPanel2.setLayout(roundedPanel2Layout);
        roundedPanel2Layout.setHorizontalGroup(
            roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel2Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(roundedPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(searchbutton))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 856, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roundedPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(20, 20, 20))
        );
        roundedPanel2Layout.setVerticalGroup(
            roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roundedPanel2Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(roundedPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(searchbutton, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addComponent(jLabel11)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel4.add(roundedPanel2, java.awt.BorderLayout.CENTER);

        jSplitPane1.setRightComponent(jPanel4);

        jPanel1.add(jSplitPane1, "card2");

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        addAppointment();

    }//GEN-LAST:event_jButton3ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
    onAppointmentTypeChange();
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void searchbuttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_searchbuttonActionPerformed
       loadTodaysAppointments();
    }//GEN-LAST:event_searchbuttonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.github.lgooddatepicker.components.DateTimePicker dateTimePicker1;
    private javax.swing.JButton jButton3;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private system.ui.components.RoundedPanel roundedPanel1;
    private system.ui.components.RoundedPanel roundedPanel2;
    private system.ui.components.SearchDropdown searchDropdown1;
    private system.ui.components.SearchDropdown searchDropdown2;
    private javax.swing.JButton searchbutton;
    // End of variables declaration//GEN-END:variables
}
