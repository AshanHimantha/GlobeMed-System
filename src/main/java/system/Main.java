package system; // Use your actual package

import com.formdev.flatlaf.themes.FlatMacLightLaf;
import jakarta.persistence.EntityManager;
import system.enums.AppointmentType;
import system.enums.DayOfWeek;
import system.enums.UserRole;
import system.model.DoctorSchedule;
import system.model.MedicalService;
import system.model.Patient;
import system.model.User;
import system.service.AuthenticationService;
import system.service.PersistenceManager;
import system.service.ScheduleService;
import system.ui.LoginUi;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class Main {

    public static void main(String[] args) {
        // 1. Apply the FlatLaf theme FIRST
        try {
            UIManager.setLookAndFeel(new FlatMacLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Failed to initialize FlatLaf look and feel.");
        }

        // 2. Initialize all database data BEFORE showing any UI.
        initializeDatabase();

        // 3. Launch the Swing GUI on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            LoginUi loginUi = new LoginUi();
            loginUi.setVisible(true);
        });
    }

    /**
     * A single, comprehensive method to initialize all necessary seed data
     * for the application to run correctly.
     */
    private static void initializeDatabase() {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();

            // --- 1. Initialize Medical Services ---
            if (em.createQuery("SELECT COUNT(s) FROM MedicalService s", Long.class).getSingleResult() == 0) {
                System.out.println("Initializing Medical Services...");
                em.persist(new MedicalService("Standard Consultation", "General medical check-up", AppointmentType.CONSULTATION, 0)); // Price is doctor-dependent
                em.persist(new MedicalService("Follow-up Consultation", "Follow-up medical check-up", AppointmentType.CONSULTATION, 0));
                em.persist(new MedicalService("Chest X-Ray", "Radiological diagnostic test", AppointmentType.DIAGNOSTIC, 250.00));
                em.persist(new MedicalService("Blood Test - CBC", "Complete Blood Count test", AppointmentType.DIAGNOSTIC, 80.00));
                em.persist(new MedicalService("Appendectomy", "Surgical removal of the appendix", AppointmentType.SURGERY, 0)); // Price is determined post-op
            }

            // --- 2. Initialize Users (Doctors, Nurses, Admins) ---
            User doctorAlice = null;
            if (em.find(User.class, "doctor") == null) {
                doctorAlice = new User("doctor", AuthenticationService.hashPassword("pass123"), UserRole.DOCTOR, "Alice", "Wonderland");
                doctorAlice.setConsultationFee(250.00);
                em.persist(doctorAlice);
            } else {
                doctorAlice = em.find(User.class, "doctor");
            }

            if (em.find(User.class, "nurse") == null) {
                User nurse = new User("nurse", AuthenticationService.hashPassword("nurse@work"), UserRole.NURSE, "Ben", "Carter");
                em.persist(nurse);
            }
            if (em.find(User.class, "admin") == null) {
                User admin = new User("admin", AuthenticationService.hashPassword("adminP@ss!"), UserRole.ADMIN, "Charles", "Davis");
                em.persist(admin);
            }

            // --- 3. Initialize Patients ---
            if (em.find(Patient.class, "P101") == null) {
                // Call the correct 6-parameter constructor
                em.persist(new Patient(
                        "P101",                     // patientId
                        "John Smith",               // name
                        45,                         // age
                        "Male",                     // gender
                        null,                       // contactNumberEncrypted (can be null)
                        LocalDate.of(2023, 5, 10)   // lastVisitDate
                ));
            }
            if (em.find(Patient.class, "P102") == null) {
                em.persist(new Patient(
                        "P102",                     // patientId
                        "Jane Doe",                 // name
                        32,                         // age
                        "Female",                   // gender
                        null,                       // contactNumberEncrypted
                        LocalDate.of(2023, 6, 15)   // lastVisitDate
                ));
            }

            em.getTransaction().commit(); // Commit all changes so far

            // --- 4. Initialize Doctor Schedules (requires a separate transaction) ---
            ScheduleService scheduleService = new ScheduleService();
            if (em.createQuery("SELECT COUNT(ds) FROM DoctorSchedule ds", Long.class).getSingleResult() == 0) {
                System.out.println("Initializing Doctor Schedules...");
                scheduleService.createSchedule(new DoctorSchedule(doctorAlice, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(17, 0)));
                scheduleService.createSchedule(new DoctorSchedule(doctorAlice, DayOfWeek.TUESDAY, LocalTime.of(9, 0), LocalTime.of(17, 0)));
                scheduleService.createSchedule(new DoctorSchedule(doctorAlice, DayOfWeek.WEDNESDAY, LocalTime.of(9, 0), LocalTime.of(12, 0)));
            }

        } catch (Exception e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            if (em != null) em.close();
        }
    }
}