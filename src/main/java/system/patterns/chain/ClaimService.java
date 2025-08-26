/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package system.patterns.chain;

import jakarta.persistence.EntityManager;
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
            em.merge(claim); // merge() handles both save and update
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            // In a real app, you'd throw a custom exception here
        } finally {
            em.close();
        }
    }
}