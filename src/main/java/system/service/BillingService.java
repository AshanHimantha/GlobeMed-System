/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package system.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import system.model.BillableItem;

/**
 *
 * @author User
 */
public class BillingService {
    public boolean saveBillableItems(List<BillableItem> items) {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            for (BillableItem item : items) {
                em.persist(item);
            }
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }
    
   public List<BillableItem> getBillableItemsForAppointment(Long appointmentId) {
        if (appointmentId == null) return java.util.Collections.emptyList();
        
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            // This is a more complex but correct query. It joins through the Claim entity.
            TypedQuery<BillableItem> query = em.createQuery(
                "SELECT b FROM BillableItem b WHERE b.claim.appointment.id = :apptId ORDER BY b.description",
                BillableItem.class
            );
            query.setParameter("apptId", appointmentId);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        } finally {
            if (em != null) em.close();
        }
    }
    
    public List<BillableItem> getBillableItemsForClaim(Long claimId) {
        if (claimId == null) return java.util.Collections.emptyList();
        
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            TypedQuery<BillableItem> query = em.createQuery(
                // --- THIS IS THE FIX ---
                // Query by the 'claim' object's 'id' field
                "SELECT b FROM BillableItem b WHERE b.claim.id = :claimId ORDER BY b.description",
                BillableItem.class
            );
            query.setParameter("claimId", claimId);
            return query.getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}
