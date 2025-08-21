package system.service;


import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import system.model.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

public class AuthenticationService {
    private static AuthenticationService instance;
    private User loggedInUser; // To store the currently logged-in user

    private AuthenticationService() {}

    public static AuthenticationService getInstance() {
        if (instance == null) {
            instance = new AuthenticationService();
        }
        return instance;
    }

    public boolean login(String username, String password) {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            TypedQuery<User> query = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);
            query.setParameter("username", username);
            User user = query.getSingleResult();

            String providedPasswordHash = hashPassword(password);
            if (user.getPasswordHash().equals(providedPasswordHash)) {
                this.loggedInUser = user;

                // --- NEW: Update last login date ---
                em.getTransaction().begin();
                user.setLastLoginDate(LocalDateTime.now());
                em.merge(user); // Persist the change
                em.getTransaction().commit();
                // --- END NEW ---

                return true;
            }
        } catch (NoResultException e) {
            return false;
        } finally {
            em.close();
        }
        return false;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public static String hashPassword(String password) {
        try {
            // FIX: Corrected "SHA-260" to the valid "SHA-256" algorithm
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // This exception should now never be thrown for SHA-256
            throw new RuntimeException("Error hashing password", e);
        }
    }
}
