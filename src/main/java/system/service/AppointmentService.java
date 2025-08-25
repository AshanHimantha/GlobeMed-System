/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package system.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import system.model.Appointment;
import system.model.Patient;
import system.model.User;

/**
 *
 * @author User
 */
public class AppointmentService {
    /**
     * Checks if doctor is available at the specified time with a 10-minute buffer
     * @param doctor The doctor to check availability for
     * @param dateTime The requested appointment time
     * @return true if doctor is available (no appointments within 10 minutes), false otherwise
     */
    public boolean isDoctorAvailable(User doctor, LocalDateTime dateTime) {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            // Check for appointments within 10 minutes before and after the requested time
            LocalDateTime startBuffer = dateTime.minusMinutes(10);
            LocalDateTime endBuffer = dateTime.plusMinutes(10);

            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(a) FROM Appointment a WHERE a.doctor = :doctor " +
                "AND a.appointmentDateTime BETWEEN :startBuffer AND :endBuffer " +
                "AND a.status = 'SCHEDULED'",
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
     * Creates and saves a new appointment to the database.
     * @return The created Appointment object, or null on failure.
     */
    public Appointment createAppointment(Patient patient, User doctor, LocalDateTime dateTime, String description, User scheduledBy) {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            Appointment newAppointment = new Appointment(patient, doctor, dateTime, description, scheduledBy);
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
            em.close();
        }
    }
    
    /**
     * Gets all SCHEDULED appointments for a specific doctor.
     */
    public List<Appointment> getAppointmentsForDoctor(User doctor) {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            TypedQuery<Appointment> query = em.createQuery(
                "SELECT a FROM Appointment a WHERE a.doctor = :doctor AND a.status = 'SCHEDULED' ORDER BY a.appointmentDateTime",
                Appointment.class
            );
            query.setParameter("doctor", doctor);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Gets all appointments ordered by date and time.
     */
    public List<Appointment> getAllAppointments() {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            TypedQuery<Appointment> query = em.createQuery(
                "SELECT a FROM Appointment a ORDER BY a.appointmentDateTime DESC",
                Appointment.class
            );
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Gets appointments for today.
     */
    public List<Appointment> getTodaysAppointments() {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
            LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);

            TypedQuery<Appointment> query = em.createQuery(
                "SELECT a FROM Appointment a WHERE a.appointmentDateTime BETWEEN :start AND :end ORDER BY a.appointmentDateTime",
                Appointment.class
            );
            query.setParameter("start", startOfDay);
            query.setParameter("end", endOfDay);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    /**
     * Updates appointment status.
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
            em.getTransaction().rollback();
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Updates appointment time.
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
            em.getTransaction().rollback();
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }

    /**
     * Gets conflicting appointments within 10 minutes of the specified time
     * @param doctor The doctor to check
     * @param dateTime The requested appointment time
     * @return List of conflicting appointments
     */
    public List<Appointment> getConflictingAppointments(User doctor, LocalDateTime dateTime) {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            LocalDateTime startBuffer = dateTime.minusMinutes(10);
            LocalDateTime endBuffer = dateTime.plusMinutes(10);

            TypedQuery<Appointment> query = em.createQuery(
                "SELECT a FROM Appointment a WHERE a.doctor = :doctor " +
                "AND a.appointmentDateTime BETWEEN :startBuffer AND :endBuffer " +
                "AND a.status = 'SCHEDULED' " +
                "ORDER BY a.appointmentDateTime",
                Appointment.class
            );
            query.setParameter("doctor", doctor);
            query.setParameter("startBuffer", startBuffer);
            query.setParameter("endBuffer", endBuffer);

            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
