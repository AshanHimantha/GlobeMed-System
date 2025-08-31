package system.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import system.enums.DayOfWeek;
import system.model.DoctorSchedule;
import system.model.User;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ScheduleService {

    public boolean isDoctorWorking(User doctor, LocalDateTime dateTime) {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            // Convert Java's DayOfWeek to our enum
            DayOfWeek day = DayOfWeek.valueOf(dateTime.getDayOfWeek().toString());
            LocalTime time = dateTime.toLocalTime();

            TypedQuery<DoctorSchedule> query = em.createQuery(
                    "SELECT ds FROM DoctorSchedule ds WHERE ds.doctor = :doctor " +
                            "AND ds.dayOfWeek = :day AND ds.isAvailable = true " +
                            "AND :time BETWEEN ds.startTime AND ds.endTime",
                    DoctorSchedule.class
            );
            query.setParameter("doctor", doctor);
            query.setParameter("day", day);
            query.setParameter("time", time);

            // If we find at least one matching schedule, the doctor is working.
            return !query.getResultList().isEmpty();
        } catch (NoResultException e) {
            return false; // No schedule found for that day
        } finally {
            if (em != null) em.close();
        }
    }

    /**
     * Helper method to create and save a schedule entry.
     * To be used during initial database setup.
     */
    public void createSchedule(DoctorSchedule schedule) {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(schedule);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            if (em != null) em.close();
        }
    }

    public List<DoctorSchedule> getScheduleForDoctor(User doctor) {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        List<DoctorSchedule> finalSchedule = new ArrayList<>();
        try {
            TypedQuery<DoctorSchedule> query = em.createQuery(
                    "SELECT ds FROM DoctorSchedule ds WHERE ds.doctor = :doctor",
                    DoctorSchedule.class
            );
            query.setParameter("doctor", doctor);
            List<DoctorSchedule> existingSchedules = query.getResultList();

            // Use a Map for easy lookup of existing schedules
            Map<DayOfWeek, DoctorSchedule> scheduleMap = new EnumMap<>(DayOfWeek.class);
            for (DoctorSchedule ds : existingSchedules) {
                scheduleMap.put(ds.getDayOfWeek(), ds);
            }

            // Iterate through all days of the week to build a complete 7-day schedule
            for (DayOfWeek day : DayOfWeek.values()) {
                if (scheduleMap.containsKey(day)) {
                    finalSchedule.add(scheduleMap.get(day));
                } else {
                    // If no schedule exists for this day, create a default "unavailable" one
                    DoctorSchedule defaultSchedule = new DoctorSchedule(doctor, day, LocalTime.of(9, 0), LocalTime.of(17, 0));
                    defaultSchedule.setAvailable(false);
                    finalSchedule.add(defaultSchedule);
                }
            }
            return finalSchedule;
        } finally {
            if (em != null) em.close();
        }
    }

    // --- NEW METHOD 2 ---
    /**
     * Saves or updates an entire weekly schedule for a doctor in a single transaction.
     *
     * @param updatedSchedules A list of 7 DoctorSchedule objects.
     * @return true if the save was successful, false otherwise.
     */
    public boolean saveFullSchedule(List<DoctorSchedule> updatedSchedules) {
        if (updatedSchedules == null || updatedSchedules.size() != 7) {
            return false; // Invalid input
        }
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            for (DoctorSchedule schedule : updatedSchedules) {
                // em.merge() will either INSERT a new schedule (if id is null)
                // or UPDATE an existing one.
                em.merge(schedule);
            }
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
            return false;
        } finally {
            if (em != null) em.close();
        }
    }
}
