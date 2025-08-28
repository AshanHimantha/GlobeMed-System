
package system.service;


import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;
import system.model.MedicalService;

public class MedicalServiceService {
    public List<MedicalService> getAllServices() {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            TypedQuery<MedicalService> query = em.createQuery("SELECT s FROM MedicalService s ORDER BY s.name", MedicalService.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }
}