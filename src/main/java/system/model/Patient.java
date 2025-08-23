/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package system.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @Column(name = "patient_id", nullable = false, unique = true)
    private String patientId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "medical_history", length = 1000) // Allow for a longer history text
    private String medicalHistory;
    
    // JPA requires a no-arg constructor
    public Patient() {}

    public Patient(String patientId, String name, String medicalHistory) {
        this.patientId = patientId;
        this.name = name;
        this.medicalHistory = medicalHistory;
    }

    // Getters and Setters for all fields
    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }

    @Override
    public String toString() {
        return "Patient ID:      " + patientId + "\n" +
               "Name:            " + name + "\n" +
               "Medical History: " + medicalHistory;
    }
}