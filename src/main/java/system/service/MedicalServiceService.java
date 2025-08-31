package system.service;


import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;
import system.enums.AppointmentType;
import system.model.MedicalService;

public class MedicalServiceService {
    public List<MedicalService> getAllServices() {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            TypedQuery<MedicalService> query = em.createQuery("SELECT s FROM MedicalService s ORDER BY s.name", MedicalService.class);
            List<MedicalService> services = query.getResultList();

            // If no services exist, create some sample diagnostic services
            if (services.isEmpty()) {
                createSampleDiagnosticServices();
                // Re-fetch after creating sample data
                services = query.getResultList();
            }

            return services;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }

    private void createSampleDiagnosticServices() {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();

            // Create sample diagnostic services
            MedicalService xray = new MedicalService("Chest X-Ray", "Standard chest X-ray imaging", AppointmentType.DIAGNOSTIC, 150.00);
            MedicalService bloodTest = new MedicalService("Blood Test - CBC", "Complete Blood Count test", AppointmentType.DIAGNOSTIC, 75.00);
            MedicalService mri = new MedicalService("MRI Scan", "Magnetic Resonance Imaging scan", AppointmentType.DIAGNOSTIC, 800.00);
            MedicalService ctScan = new MedicalService("CT Scan", "Computed Tomography scan", AppointmentType.DIAGNOSTIC, 600.00);
            MedicalService ultrasound = new MedicalService("Ultrasound", "Ultrasound imaging", AppointmentType.DIAGNOSTIC, 200.00);
            MedicalService ecg = new MedicalService("ECG", "Electrocardiogram test", AppointmentType.DIAGNOSTIC, 100.00);
            MedicalService bloodSugar = new MedicalService("Blood Sugar Test", "Glucose level test", AppointmentType.DIAGNOSTIC, 50.00);
            MedicalService urinalysis = new MedicalService("Urinalysis", "Urine analysis test", AppointmentType.DIAGNOSTIC, 40.00);

            em.persist(xray);
            em.persist(bloodTest);
            em.persist(mri);
            em.persist(ctScan);
            em.persist(ultrasound);
            em.persist(ecg);
            em.persist(bloodSugar);
            em.persist(urinalysis);

            em.getTransaction().commit();
            System.out.println("Created sample diagnostic services in database");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public void saveMedicalService(MedicalService service) {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(service);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}