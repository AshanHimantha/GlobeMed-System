package system.service;

import jakarta.persistence.EntityManager;
import system.model.Facility;

import java.util.List;

public class FacilityService {
    public List<Facility> getAllFacilities() {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            return em.createQuery("SELECT f FROM Facility f ORDER BY f.name", Facility.class).getResultList();
        } finally {
            em.close();
        }
    }
}