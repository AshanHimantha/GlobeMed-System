
package system.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import system.enums.AppointmentType;
import system.enums.PaymentMethod;
import system.model.Appointment;
import system.model.Patient;
import system.model.User;

/**
 *
 * @author User
 */
public class AppointmentService {


public Appointment createAppointment(Patient patient, User doctor, User scheduledBy, AppointmentType type, String serviceName, double price, LocalDateTime dateTime) {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            
            // --- THIS IS THE FIX ---
            // Now calling the correct 7-parameter constructor.
            Appointment newAppointment = new Appointment(patient, doctor, scheduledBy, type, serviceName, price, dateTime);
            
            em.persist(newAppointment);
            em.getTransaction().commit();
            return newAppointment;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return null;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Checks if a specific doctor is available at a given time with a 10-minute buffer.
     */
public boolean isDoctorAvailable(User doctor, LocalDateTime dateTime) {
    EntityManager em = PersistenceManager.getInstance().getEntityManager();
    try {
        LocalDateTime startBuffer = dateTime.minusMinutes(10);
        LocalDateTime endBuffer = dateTime.plusMinutes(10);

        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(a) FROM Appointment a WHERE a.doctor = :doctor " +
            "AND a.appointmentDateTime BETWEEN :startBuffer AND :endBuffer " +
            // --- THIS IS THE FIX ---
            "AND (a.status = 'SCHEDULED' OR a.status = 'PENDING_PAYMENT')", // Check both statuses
            Long.class
        );
        query.setParameter("doctor", doctor);
        query.setParameter("startBuffer", startBuffer);
        query.setParameter("endBuffer", endBuffer);

        return query.getSingleResult() == 0;
    } finally {
        em.close();
    }
}
    /**
     * Finds and returns a list of appointments that conflict with a given time slot.
     */
    public List<Appointment> getConflictingAppointments(User doctor, LocalDateTime dateTime) {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            LocalDateTime startBuffer = dateTime.minusMinutes(10);
            LocalDateTime endBuffer = dateTime.plusMinutes(10);
            TypedQuery<Appointment> query = em.createQuery(
                "SELECT a FROM Appointment a WHERE a.doctor = :doctor " +
                "AND a.appointmentDateTime BETWEEN :startBuffer AND :endBuffer " +
                "AND a.status = 'SCHEDULED'", Appointment.class);
            query.setParameter("doctor", doctor);
            query.setParameter("startBuffer", startBuffer);
            query.setParameter("endBuffer", endBuffer);
            return query.getResultList();
        } finally {
            if (em != null) em.close();
        }
    }

    /**
     * Updates the date and time of a specific appointment.
     */
    public boolean updateAppointmentTime(Long appointmentId, LocalDateTime newDateTime) {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            Appointment appointment = em.find(Appointment.class, appointmentId);
            if (appointment != null) {
                appointment.setAppointmentDateTime(newDateTime);
                em.merge(appointment);
                em.getTransaction().commit();
                return true;
            }
            return false; // Appointment not found
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            return false;
        } finally {
            if (em != null) em.close();
        }
    }

    /**
     * Updates the status of a specific appointment.
     */
    public boolean updateAppointmentStatus(Long appointmentId, String status) {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            Appointment appointment = em.find(Appointment.class, appointmentId);
            if (appointment != null) {
                appointment.setStatus(status);
                em.merge(appointment);
                em.getTransaction().commit();
                return true;
            }
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            return false;
        } finally {
            if (em != null) em.close();
        }
    }

    /**
     * Gets all appointments for today.
     */
    public List<Appointment> getTodaysAppointments() {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
            LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
            TypedQuery<Appointment> query = em.createQuery(
                "SELECT a FROM Appointment a WHERE a.appointmentDateTime BETWEEN :start AND :end ORDER BY a.appointmentDateTime",
                Appointment.class);
            query.setParameter("start", startOfDay);
            query.setParameter("end", endOfDay);
            return query.getResultList();
        } finally {
            if (em != null) em.close();
        }
    }
    
    public boolean processDirectPayment(Long appointmentId, PaymentMethod method) {
        // This method should not be used for insurance claims.
        if (method == PaymentMethod.INSURANCE) {
            System.err.println("processDirectPayment should not be called for INSURANCE.");
            return false;
        }

        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();

            // Find the appointment in the database
            Appointment appointment = em.find(Appointment.class, appointmentId);

            // Check if the appointment exists and is in the correct status
            if (appointment != null && "PENDING_PAYMENT".equals(appointment.getStatus())) {
                
                // Update the appointment's data
                appointment.setPaymentMethod(method);
                appointment.setStatus("SCHEDULED"); // Payment confirms the booking
                
                // Save the changes
                em.merge(appointment);
                em.getTransaction().commit();
                return true; // Success!
            } else {
                // If appointment not found or status is not PENDING_PAYMENT, do nothing.
                em.getTransaction().rollback();
                return false;
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    public boolean updatePaymentMethodAndStatus(Long appointmentId, PaymentMethod method, String status) {
    EntityManager em = PersistenceManager.getInstance().getEntityManager();
    try {
        em.getTransaction().begin();
        Appointment appointment = em.find(Appointment.class, appointmentId);
        if (appointment != null) {
            appointment.setPaymentMethod(method);
            appointment.setStatus(status);
            em.merge(appointment);
            em.getTransaction().commit();
            return true;
        }
        return false;
    } catch (Exception e) {
        if (em.getTransaction().isActive()) em.getTransaction().rollback();
        e.printStackTrace();
        return false;
    } finally {
        if (em != null) em.close();
    }
}
}