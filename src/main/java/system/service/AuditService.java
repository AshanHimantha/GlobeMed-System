package system.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import system.model.AuditLog;

import java.time.LocalDate;
import java.util.List;

public class AuditService {

    /**
     * Saves an AuditLog entry to the database in its own transaction.
     * @param logEntry The AuditLog object to persist.
     */
    public void log(AuditLog logEntry) {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(logEntry);
            em.getTransaction().commit();
        } catch (Exception e) {
            // In a real system, logging failures are critical and must be handled
            // (e.g., write to a local file as a fallback).
            e.printStackTrace();
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<AuditLog> searchLogs(LocalDate fromDate, LocalDate toDate, String username) {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            // Start building the query
            String jpql = "SELECT al FROM AuditLog al WHERE al.timestamp BETWEEN :start AND :end";

            boolean hasUsername = username != null && !username.trim().isEmpty();
            if (hasUsername) {
                jpql += " AND LOWER(al.username) LIKE LOWER(:username)";
            }

            jpql += " ORDER BY al.timestamp DESC"; // Show most recent first

            TypedQuery<AuditLog> query = em.createQuery(jpql, AuditLog.class);

            // Set parameters
            query.setParameter("start", fromDate.atStartOfDay());
            query.setParameter("end", toDate.plusDays(1).atStartOfDay()); // To include the whole 'to' day

            if (hasUsername) {
                query.setParameter("username", "%" + username.trim() + "%");
            }

            return query.getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}