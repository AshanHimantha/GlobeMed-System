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
    
    public User findUserByUsername(String username) {
    EntityManager em = PersistenceManager.getInstance().getEntityManager();
    try {
        return em.find(User.class, username);
    } finally {
        em.close();
    }
}
    
    public List<User> searchDoctorsByName(String name, int limit) {
    EntityManager em = PersistenceManager.getInstance().getEntityManager();
    try {
        TypedQuery<User> query = em.createQuery(
            "SELECT u FROM User u WHERE u.role = :role AND LOWER(u.lastName) LIKE LOWER(:name) ORDER BY u.lastName", 
            User.class
        );
        query.setParameter("role", UserRole.DOCTOR);
        query.setParameter("name", "%" + name + "%");
        query.setMaxResults(limit);
        return query.getResultList();
    } finally {
        em.close();
    }
}
    
    public List<User> getAllUsers() {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u ORDER BY u.lastName, u.firstName",
                User.class
            );
            return query.getResultList();
        } catch (Exception e) {
            System.err.println("Error fetching all users: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void addUser(String username, String password, UserRole role, String firstName, String lastName, String contactNumber, String address, Double consultationFee) {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();

            User user = new User(username, password, role, firstName, lastName);
            user.setContactNumber(contactNumber);
            user.setAddress(address);

            // Only set consultation fee for doctors
            if (role == UserRole.DOCTOR && consultationFee != null) {
                user.setConsultationFee(consultationFee);
            }

            em.persist(user);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error adding user: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to add user", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void updateUser(String username, UserRole role, String firstName, String lastName,
                          String contactNumber, String address, Double consultationFee) {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();

            User user = em.find(User.class, username);
            if (user == null) {
                throw new RuntimeException("User not found: " + username);
            }

            // Update user properties
            user.setRole(role);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setContactNumber(contactNumber);
            user.setAddress(address);

            // Only set consultation fee for doctors
            if (role == UserRole.DOCTOR && consultationFee != null) {
                user.setConsultationFee(consultationFee);
            } else {
                user.setConsultationFee(null); // Clear consultation fee if not a doctor
            }

            em.merge(user);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Error updating user: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to update user", e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}

