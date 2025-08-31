package system.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import system.enums.DayOfWeek;
import system.model.DoctorSchedule;
import system.model.Facility;
import system.model.User;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ScheduleService {

    /**
     * Checks if a doctor is on schedule to work at a specific facility at a given date and time.
     * @param doctor The doctor to check.
     * @param dateTime The date and time of the potential appointment.
     * @param facility The facility where the appointment would take place.
     * @return true if the doctor is scheduled and available, false otherwise.
     */
    public boolean isDoctorWorkingAtFacility(User doctor, LocalDateTime dateTime, Facility facility) {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            DayOfWeek day = DayOfWeek.valueOf(dateTime.getDayOfWeek().toString());
            LocalTime time = dateTime.toLocalTime();

            TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(ds) FROM DoctorSchedule ds WHERE ds.doctor = :doctor " +
                "AND ds.facility = :facility AND ds.dayOfWeek = :day AND ds.isAvailable = true " +
                "AND :time BETWEEN ds.startTime AND ds.endTime", Long.class);
            query.setParameter("doctor", doctor);
            query.setParameter("facility", facility);
            query.setParameter("day", day);
            query.setParameter("time", time);
            return query.getSingleResult() > 0;
        } finally {
            if (em != null) em.close();
        }
    }

    /**
     * Retrieves the full weekly schedule for a specific doctor at a specific facility.
     * If a schedule for a day doesn't exist, it creates a default, "unavailable" entry.
     * @param doctor The doctor whose schedule is needed.
     * @param facility The facility for which to fetch the schedule.
     * @return A List containing exactly 7 DoctorSchedule objects.
     */
    public List<DoctorSchedule> getScheduleForDoctorAtFacility(User doctor, Facility facility) {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        List<DoctorSchedule> finalSchedule = new ArrayList<>();
        try {
            TypedQuery<DoctorSchedule> query = em.createQuery(
                "SELECT ds FROM DoctorSchedule ds WHERE ds.doctor = :doctor AND ds.facility = :facility",
                DoctorSchedule.class
            );
            query.setParameter("doctor", doctor);
            query.setParameter("facility", facility);
            List<DoctorSchedule> existingSchedules = query.getResultList();

            Map<DayOfWeek, DoctorSchedule> scheduleMap = new EnumMap<>(DayOfWeek.class);
            existingSchedules.forEach(ds -> scheduleMap.put(ds.getDayOfWeek(), ds));

            for (DayOfWeek day : DayOfWeek.values()) {
                if (scheduleMap.containsKey(day)) {
                    finalSchedule.add(scheduleMap.get(day));
                } else {
                    DoctorSchedule defaultSchedule = new DoctorSchedule(doctor, facility, day, LocalTime.of(9, 0), LocalTime.of(17, 0));
                    defaultSchedule.setAvailable(false);
                    finalSchedule.add(defaultSchedule);
                }
            }
            return finalSchedule;
        } finally {
            if (em != null) em.close();
        }
    }

    /**
     * Saves or updates an entire weekly schedule for a doctor/facility pair in a single transaction.
     * @param updatedSchedules A list of 7 DoctorSchedule objects.
     * @return true if the save was successful, false otherwise.
     */
    public boolean saveFullSchedule(List<DoctorSchedule> updatedSchedules) {
        if (updatedSchedules == null || updatedSchedules.isEmpty()) return false;
        
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();
            for (DoctorSchedule schedule : updatedSchedules) {
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

    /**
     * Helper method to create and save a single schedule entry, used for initial data seeding.
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
}