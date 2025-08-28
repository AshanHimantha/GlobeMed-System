
package system.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import system.enums.PatientStatus;

@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @Column(name = "patient_id") // We will auto-generate this now
    private String patientId;
    
     @Column(name = "registered_date", nullable = false)
    private LocalDate registeredDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PatientStatus status;
    // --- END NEW FIELDS ---

    @Column(name = "name", nullable = false)
    private String name;
    
    // --- NEW FIELDS ---
    @Column(name = "age")
    private int age;

    @Column(name = "gender")
    private String gender;

    @Column(name = "contact_number_encrypted") // Stored encrypted
    private String contactNumberEncrypted;
    // --- END NEW FIELDS ---

    
    @Column(name = "last_visit_date")
    private LocalDate lastVisitDate;

    public Patient() {}

    // Updated constructor
    public Patient(String patientId, String name, int age, String gender, String contactNumberEncrypted, LocalDate lastVisitDate) {
        this.patientId = patientId;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.contactNumberEncrypted = contactNumberEncrypted;
        this.registeredDate = LocalDate.now(); // Automatically set to the creation date
        this.status = PatientStatus.ACTIVE;
    }

    // --- Add getters and setters for new fields ---
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getContactNumberEncrypted() { return contactNumberEncrypted; }
    public void setContactNumberEncrypted(String contactNumberEncrypted) { this.contactNumberEncrypted = contactNumberEncrypted; }
     public LocalDate getLastVisitDate() { return lastVisitDate; }
    public void setLastVisitDate(LocalDate lastVisitDate) { this.lastVisitDate = lastVisitDate; }
    

    // Other getters/setters remain the same...
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }


     public LocalDate getRegisteredDate() { return registeredDate; }
    public void setRegisteredDate(LocalDate registeredDate) { this.registeredDate = registeredDate; }
    public PatientStatus getStatus() { return status; }
    public void setStatus(PatientStatus status) { this.status = status; }

    @Override
    public String toString() {
        // ... (can be updated to show new fields if desired)
        return "Patient ID: " + patientId + "\nName: " + name;
    }
}