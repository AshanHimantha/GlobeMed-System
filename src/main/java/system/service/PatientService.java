
package system.service;
import system.model.Patient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.Collections;
import java.util.List;

/**
 * Service class to manage all database operations related to Patients.
 * This encapsulates the JPA logic and separates it from the UI.
 */
public class PatientService {

    /**
     * Retrieves a list of all patients from the database.
     *
     * @return A List of all Patient objects, or an empty list if none are found or an error occurs.
     */
    public List<Patient> getAllPatients() {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            // JPQL (Java Persistence Query Language) to select all patient entities.
            TypedQuery<Patient> query = em.createQuery("SELECT p FROM Patient p ORDER BY p.patientId", Patient.class);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error fetching all patients: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList(); // Return an empty list on error
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }


    public Patient addPatient(String name, int age, String gender, String contactNumber, String medicalHistory) {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();

            // 1. Auto-generate a new Patient ID based on the current count
            long patientCount = em.createQuery("SELECT COUNT(p) FROM Patient p", Long.class).getSingleResult();
            String newPatientId = "P" + String.format("%03d", patientCount + 1); // Generates IDs like P001, P002, ...

            // 2. Encrypt the contact number using our utility
            String encryptedContact = EncryptionUtil.encrypt(contactNumber);

            // 3. Create the new Patient entity
            Patient newPatient = new Patient(newPatientId, name, age, gender, encryptedContact, medicalHistory);

            // 4. Persist the new entity to the database
            em.persist(newPatient);
            em.getTransaction().commit();

            return newPatient; // Return the created patient on success
        } catch (Exception e) {
            System.err.println("Error adding patient: " + e.getMessage());
            e.printStackTrace();
            // If a transaction is active and an error occurs, roll it back
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return null; // Return null on failure
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }


    public Patient findPatientById(String patientId) {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            // EntityManager.find() is the most efficient way to look up an entity by its primary key.
            return em.find(Patient.class, patientId);
        } catch (Exception e) {
            System.err.println("Error finding patient by ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}