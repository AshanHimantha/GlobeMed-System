
package system.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @Column(name = "patient_id") // We will auto-generate this now
    private String patientId;

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

    @Column(name = "medical_history", length = 1000)
    private String medicalHistory;

    public Patient() {}

    // Updated constructor
    public Patient(String patientId, String name, int age, String gender, String contactNumberEncrypted, String medicalHistory) {
        this.patientId = patientId;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.contactNumberEncrypted = contactNumberEncrypted;
        this.medicalHistory = medicalHistory;
    }

    // --- Add getters and setters for new fields ---
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getContactNumberEncrypted() { return contactNumberEncrypted; }
    public void setContactNumberEncrypted(String contactNumberEncrypted) { this.contactNumberEncrypted = contactNumberEncrypted; }

    // Other getters/setters remain the same...
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }

    @Override
    public String toString() {
        // ... (can be updated to show new fields if desired)
        return "Patient ID: " + patientId + "\nName: " + name;
    }
}