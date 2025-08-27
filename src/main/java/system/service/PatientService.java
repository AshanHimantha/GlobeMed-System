package system.service;

import system.model.Patient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.ArrayList;

import java.util.Collections;
import java.util.List;
import system.enums.PatientStatus;

public class PatientService {

    public List<Patient> getAllPatients() {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            // JPQL (Java Persistence Query Language) to select all patient entities.
            TypedQuery<Patient> query = em.createQuery(
                    "SELECT p FROM Patient p WHERE p.status = :status ORDER BY p.patientId",
                    Patient.class
            );
            query.setParameter("status", PatientStatus.ACTIVE); // Only fetch ACTIVE patients
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
    
    public List<Patient> searchPatientsByName(String name, int limit) {
    EntityManager em = PersistenceManager.getInstance().getEntityManager();
    try {
        TypedQuery<Patient> query = em.createQuery(
            "SELECT p FROM Patient p WHERE p.status = 'ACTIVE' AND LOWER(p.name) LIKE LOWER(:name) ORDER BY p.name", 
            Patient.class
        );
        query.setParameter("name", "%" + name + "%");
        query.setMaxResults(limit); // Limit the results
        return query.getResultList();
    } finally {
        em.close();
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
            Patient newPatient = new Patient(newPatientId, name, age, gender, encryptedContact, medicalHistory, LocalDate.now());

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
    


    public List<Patient> searchPatients(String keyword) {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            return em.createQuery(
                    "SELECT p FROM Patient p "
                    + "WHERE p.status = :status AND ("
                    + "   LOWER(p.patientId) LIKE LOWER(CONCAT('%', :keyword, '%')) "
                    + "   OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))",
                    Patient.class)
                    .setParameter("status", PatientStatus.ACTIVE)
                    .setParameter("keyword", keyword)
                    .getResultList();
        } catch (Exception e) {
            System.err.println("Error searching patient: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public boolean updatePatient(Patient patientToUpdate) {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(patientToUpdate); // merge() is used for updating
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public boolean updatePatientDetails(String patientId, int newAge, String newContactNumber, String newMedicalHistory) {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            // Begin the transaction
            em.getTransaction().begin();

            // 1. FETCH the existing patient object from the database.
            //    This object contains all the current data (name, gender, etc.).
            Patient patientToUpdate = em.find(Patient.class, patientId);

            // 2. Check if the patient was actually found.
            if (patientToUpdate != null) {

                // 3. MODIFY ONLY the specified fields on the fetched object.
                patientToUpdate.setAge(newAge);
                patientToUpdate.setContactNumberEncrypted(EncryptionUtil.encrypt(newContactNumber));
                patientToUpdate.setMedicalHistory(newMedicalHistory);

                // 4. SAVE the updated object back to the database.
                //    em.merge() will write the changes.
                em.getTransaction().commit();
                return true; // Success!
            } else {
                // If patient is not found, do nothing and indicate failure.
                em.getTransaction().rollback();
                return false;
            }
        } catch (Exception e) {
            // If any error occurs, roll back the transaction to prevent partial updates.
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            // Always close the EntityManager.
            if (em != null) {
                em.close();
            }
        }
    }

    public boolean softDeletePatient(String patientId) {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            Patient patient = em.find(Patient.class, patientId);
            if (patient != null) {
                patient.setStatus(PatientStatus.INACTIVE);
                em.merge(patient);
                em.getTransaction().commit();
                return true;
            }
            return false; // Patient not found
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
  public Patient findPatientById(String patientId) {
    EntityManager em = PersistenceManager.getInstance().getEntityManager();
    try {
        return em.find(Patient.class, patientId);
    } finally {
        em.close();
    }
}

}
