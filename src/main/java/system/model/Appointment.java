
package system.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import system.enums.UserRole;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Let the database auto-generate the ID
    @Column(name = "appointment_id")
    private Long id;

    // Many appointments can be for one patient.
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    // Many appointments can be with one doctor.
    @ManyToOne
    @JoinColumn(name = "doctor_username", nullable = false)
    private User doctor;

    @Column(name = "appointment_datetime", nullable = false)
    private LocalDateTime appointmentDateTime;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private String status; // e.g., "SCHEDULED", "COMPLETED", "CANCELLED"
    
    // --- NEW FIELD ---
    @ManyToOne
    @JoinColumn(name = "scheduled_by_username", nullable = false)
    private User scheduledBy;
    
    // JPA requires a no-arg constructor
    public Appointment() {}

  // --- UPDATED CONSTRUCTOR ---
    public Appointment(Patient patient, User doctor, LocalDateTime appointmentDateTime, String description, User scheduledBy) {
        // Validation: Ensure the appointment is with a doctor.
        if (doctor.getRole() != UserRole.DOCTOR) {
            throw new IllegalArgumentException("Appointments can only be scheduled with a DOCTOR.");
        }
        this.patient = patient;
        this.doctor = doctor;
        this.appointmentDateTime = appointmentDateTime;
        this.description = description;
        this.scheduledBy = scheduledBy; // Assign the new field
        this.status = "SCHEDULED";
    }

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }
    public User getDoctor() { return doctor; }
    public void setDoctor(User doctor) { this.doctor = doctor; }
    public LocalDateTime getAppointmentDateTime() { return appointmentDateTime; }
    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) { this.appointmentDateTime = appointmentDateTime; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public User getScheduledBy() { return scheduledBy; }
    public void setScheduledBy(User scheduledBy) { this.scheduledBy = scheduledBy; }
}