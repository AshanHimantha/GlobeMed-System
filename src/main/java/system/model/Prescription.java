/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package system.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author User
 */
@Entity
@Table(name = "prescriptions")
public class Prescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prescription_id")
    private Long id;

    // The appointment that generated this prescription.
    @OneToOne
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescribing_doctor_username", nullable = false)
    private User prescribingDoctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "prescription_date", nullable = false)
    private LocalDate prescriptionDate;

    @Column(name = "status", nullable = false)
    private String status; // e.g., "PENDING_PHARMACY", "FILLED", "CANCELLED"

    // A prescription has a list of items (medications).
    // CascadeType.ALL means if we save/delete a Prescription, its items are also saved/deleted.
    // orphanRemoval = true means if we remove an item from this list, it gets deleted from the database.
    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PrescriptionItem> items = new ArrayList<>();

    public Prescription() {}

    public Prescription(Appointment appointment) {
        if (appointment == null) {
            throw new IllegalArgumentException("Prescription must be linked to an appointment.");
        }
        this.appointment = appointment;
        this.patient = appointment.getPatient();
        this.prescribingDoctor = appointment.getDoctor();
        this.prescriptionDate = LocalDate.now();
        this.status = "PENDING_PHARMACY";
    }

    // Helper method to easily add items and maintain the bidirectional relationship
    public void addItem(PrescriptionItem item) {
        items.add(item);
        item.setPrescription(this);
    }

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Appointment getAppointment() { return appointment; }
    public void setAppointment(Appointment appointment) { this.appointment = appointment; }
    public User getPrescribingDoctor() { return prescribingDoctor; }
    public void setPrescribingDoctor(User prescribingDoctor) { this.prescribingDoctor = prescribingDoctor; }
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
    public LocalDate getPrescriptionDate() { return prescriptionDate; }
    public void setPrescriptionDate(LocalDate prescriptionDate) { this.prescriptionDate = prescriptionDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public List<PrescriptionItem> getItems() { return items; }
    public void setItems(List<PrescriptionItem> items) { this.items = items; }
}