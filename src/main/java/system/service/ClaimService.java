/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package system.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;
import system.enums.AppointmentType;
import system.enums.ClaimStatus;
import system.model.Appointment;
import system.model.Claim;
import system.service.PersistenceManager;

/**
 *
 * @author User
 */
public class ClaimService {

    /**
     * Saves or updates a Claim entity in the database.
     * This method will be called by each handler after it processes the claim.
     * @param claim The Claim object with its updated status and data.
     */
    public void updateClaim(Claim claim) {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(claim);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            if (em != null) em.close();
        }
    }
    
    public List<Claim> getAllClaims() {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            TypedQuery<Claim> query = em.createQuery("SELECT c FROM Claim c", Claim.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
    
    public Claim findClaimByAppointmentId(Long appointmentId) {
        if (appointmentId == null) return null;
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            // Find the one claim associated with this appointment
            TypedQuery<Claim> query = em.createQuery(
                "SELECT c FROM Claim c WHERE c.appointment.id = :apptId", Claim.class
            );
            query.setParameter("apptId", appointmentId);
            query.setMaxResults(1); // There should only ever be one
            return query.getSingleResult();
        } catch (jakarta.persistence.NoResultException e) {
            return null; // No claim found for this appointment
        } finally {
            if (em != null) em.close();
        }
    }
    
    public Claim createClaimWithItems(Claim claim) {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            // By persisting the Claim, CascadeType.ALL will automatically persist its items.
            em.persist(claim);

            em.getTransaction().commit();
            return claim;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            return null;
        } finally {
            if (em != null) em.close();
        }
    }
    
    
    public Claim createClaimFromAppointment(Appointment appointment) {
        if (!"COMPLETED".equals(appointment.getStatus())) {
            throw new IllegalStateException("Cannot create a claim for an appointment that is not completed.");
        }
        
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            // In a real system, you'd calculate totalAmount from BillableItems here.
            // For now, we use the appointment's stored price.
            Claim newClaim = new Claim(appointment, appointment.getPrice(), appointment.getPaymentMethod());
            em.persist(newClaim);
            em.getTransaction().commit();
            return newClaim;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            return null;
        } finally {
            if (em != null) em.close();
        }
    }

public List<Appointment> getAppointmentsReadyForBilling() {
    EntityManager em = PersistenceManager.getInstance().getEntityManager();
    try {
        // Find completed appointments that are marked for INSURANCE and do NOT yet have a claim.
        TypedQuery<Appointment> query = em.createQuery(
            "SELECT a FROM Appointment a LEFT JOIN Claim c ON a.id = c.appointment.id " +
            "WHERE a.status = 'COMPLETED' AND a.paymentMethod = 'INSURANCE' AND c.id IS NULL",
            Appointment.class
        );
        return query.getResultList();
    } finally {
        em.close();
    }
}

public List<Claim> findClaimsByStatus(ClaimStatus status) {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            TypedQuery<Claim> query = em.createQuery(
                "SELECT c FROM Claim c WHERE c.status = :status ORDER BY c.claimDate",
                Claim.class
            );
            query.setParameter("status", status);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        } finally {
            if (em != null) em.close();
        }
    }
}