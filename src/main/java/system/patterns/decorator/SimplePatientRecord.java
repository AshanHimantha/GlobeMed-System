/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package system.patterns.decorator;

import jakarta.persistence.EntityManager;
import system.model.Patient;
import system.service.PersistenceManager;

/**
 *
 * @author User
 */
public class SimplePatientRecord implements PatientRecord {
    private final Patient patient;
    private final String requestedId; // Store the ID that was searched for

    public SimplePatientRecord(String patientId) {
        this.requestedId = patientId;
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        this.patient = em.find(Patient.class, patientId);
        em.close();
    }

    @Override
    public String getDetails() {
        if (patient != null) {
            return "--- Patient Record ---\n" + patient.toString();
        } else {
            return "--- Patient Record ---\n\nError: No patient found with ID: " + requestedId;
        }
    }
}