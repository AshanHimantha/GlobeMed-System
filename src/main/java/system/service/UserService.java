/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package system.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;
import system.enums.UserRole;
import system.model.User;


public class UserService {
    
    
    public List<User> getAllDoctors() {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            // JPQL query to select users where the role is DOCTOR
            TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.role = :role ORDER BY u.lastName",
                User.class
            );
            // Set the 'role' parameter in the query to the DOCTOR enum constant
            query.setParameter("role", UserRole.DOCTOR);
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error fetching all doctors: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList(); // Return an empty list to prevent NullPointerExceptions
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
}
