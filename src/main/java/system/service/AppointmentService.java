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
    public boolean isDoctorAvailable(User doctor, LocalDateTime dateTime) {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(a) FROM Appointment a WHERE a.doctor = :doctor AND a.appointmentDateTime = :dateTime AND a.status = 'SCHEDULED'",
                Long.class
            );
            query.setParameter("doctor", doctor);
            query.setParameter("dateTime", dateTime);
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

}
