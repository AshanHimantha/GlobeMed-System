/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package system.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;
import system.model.Patient;
import system.model.Prescription;

/**
 *
 * @author User
 */
public class PrescriptionService {

    /**
     * Saves a new Prescription, including all its PrescriptionItems, to the database.
     * The CascadeType.ALL on the entity handles saving the items automatically.
     *
     * @param prescription The complete Prescription object with its list of items.
     * @return The saved Prescription object with its generated ID, or null on failure.
     */
    public Prescription createPrescription(Prescription prescription) {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(prescription);
            em.getTransaction().commit();
            return prescription;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Retrieves all prescriptions for a specific patient.
     *
     * @param patient The patient whose prescription history is needed.
     * @return A List of their Prescription objects.
     */
    public List<Prescription> getPrescriptionsForPatient(Patient patient) {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            TypedQuery<Prescription> query = em.createQuery(
                "SELECT p FROM Prescription p WHERE p.patient = :patient ORDER BY p.prescriptionDate DESC",
                Prescription.class
            );
            query.setParameter("patient", patient);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Retrieves all prescriptions that have a 'PENDING_PHARMACY' status.
     * This would be used by a Pharmacist's UI.
     *
     * @return A List of pending Prescription objects.
     */
    public List<Prescription> getPendingPrescriptions() {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            TypedQuery<Prescription> query = em.createQuery(
                "SELECT p FROM Prescription p WHERE p.status = 'PENDING_PHARMACY' ORDER BY p.prescriptionDate ASC",
                Prescription.class
            );
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}
