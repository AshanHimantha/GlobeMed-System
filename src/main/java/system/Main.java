package system; // Use your actual package

import com.formdev.flatlaf.themes.FlatMacLightLaf;
import system.enums.AppointmentType;
import system.enums.DayOfWeek;
import system.enums.UserRole;
import system.model.DoctorSchedule;
import system.model.Facility;
import system.model.MedicalService;
import system.model.Patient;
import system.model.User;
import system.service.AuthenticationService;
import system.service.PersistenceManager;
import system.service.ScheduleService;
import system.ui.LoginUi;
import jakarta.persistence.EntityManager;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatMacLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Failed to initialize FlatLaf look and feel.");
        }

        initializeDatabase();

        SwingUtilities.invokeLater(() -> {
            LoginUi loginUi = new LoginUi();
            loginUi.setVisible(true);
        });
    }

    /**
     * Initializes all necessary seed data for the application to run.
     * This method is idempotent - it will only add data if the tables are empty.
     */
   private static void initializeDatabase() {
        EntityManager em = PersistenceManager.getInstance().getEntityManager();
        try {
            em.getTransaction().begin();

            // --- 1. Initialize Facilities ---
            Facility mainClinic = null;
            Facility downtownHospital = null;
            if (em.createQuery("SELECT COUNT(f) FROM Facility f", Long.class).getSingleResult() == 0) {
                System.out.println("Initializing Facilities...");
                mainClinic = new Facility("Main Street Clinic", "123 Main St, Cityville");
                downtownHospital = new Facility("Downtown Hospital", "456 Central Ave, Metroburg");
                em.persist(mainClinic);
                em.persist(downtownHospital);
            } else {
                mainClinic = em.createQuery("SELECT f FROM Facility f WHERE f.name = 'Main Street Clinic'", Facility.class).getSingleResult();
                downtownHospital = em.createQuery("SELECT f FROM Facility f WHERE f.name = 'Downtown Hospital'", Facility.class).getSingleResult();
            }

            // --- 2. Initialize Medical Services ---
            if (em.createQuery("SELECT COUNT(s) FROM MedicalService s", Long.class).getSingleResult() == 0) {
                System.out.println("Initializing Medical Services...");
                // Consultations (price determined by doctor)
                em.persist(new MedicalService("Standard Consultation", "General medical check-up", AppointmentType.CONSULTATION, 0));
                em.persist(new MedicalService("Follow-up Consultation", "Follow-up medical check-up", AppointmentType.CONSULTATION, 0));
                // Diagnostics (fixed price)
                em.persist(new MedicalService("Chest X-Ray", "Radiological diagnostic test", AppointmentType.DIAGNOSTIC, 250.00));
                em.persist(new MedicalService("Blood Test - CBC", "Complete Blood Count test", AppointmentType.DIAGNOSTIC, 80.00));
                em.persist(new MedicalService("MRI Scan", "Magnetic Resonance Imaging", AppointmentType.DIAGNOSTIC, 750.00));
                // Surgeries (price determined post-op)
                em.persist(new MedicalService("Appendectomy", "Surgical removal of the appendix", AppointmentType.SURGERY, 0));
                em.persist(new MedicalService("Knee Arthroscopy", "Minimally invasive knee surgery", AppointmentType.SURGERY, 0));
            }

            // --- 3. Initialize Users ---
            User doctorAlice = em.find(User.class, "dralice");
            if (doctorAlice == null) {
                doctorAlice = new User("dralice", AuthenticationService.hashPassword("pass123"), UserRole.DOCTOR, "Alice", "Wonderland");
                doctorAlice.setConsultationFee(250.00);
                doctorAlice.setWorksAt(mainClinic);
                em.persist(doctorAlice);
            }
            User doctorBob = em.find(User.class, "drbob");
            if (doctorBob == null) {
                doctorBob = new User("drbob", AuthenticationService.hashPassword("pass123"), UserRole.DOCTOR, "Bob", "Jones");
                doctorBob.setConsultationFee(150.00);
                doctorBob.setWorksAt(downtownHospital);
                em.persist(doctorBob);
            }
            if (em.find(User.class, "nurseben") == null) {
                User nurse = new User("nurseben", AuthenticationService.hashPassword("nurse@work"), UserRole.NURSE, "Ben", "Carter");
                nurse.setWorksAt(mainClinic);
                em.persist(nurse);
            }
            if (em.find(User.class, "admincharles") == null) {
                User admin = new User("admincharles", AuthenticationService.hashPassword("adminP@ss!"), UserRole.ADMIN, "Charles", "Davis");
                admin.setWorksAt(downtownHospital);
                em.persist(admin);
            }
            if (em.find(User.class, "pharmdebra") == null) {
                User pharmacist = new User("pharmdebra", AuthenticationService.hashPassword("pharm123"), UserRole.PHARMACIST, "Debra", "Evans");
                pharmacist.setWorksAt(mainClinic);
                em.persist(pharmacist);
            }

            // --- 4. Initialize Patients ---
            if (em.find(Patient.class, "P101") == null) {
                em.persist(new Patient("P101", "John Smith", 45, "Male", null, LocalDate.of(2024, 5, 10)));
            }
            if (em.find(Patient.class, "P102") == null) {
                em.persist(new Patient("P102", "Jane Doe", 32, "Female", null, LocalDate.of(2024, 6, 15)));
            }
            if (em.find(Patient.class, "P103") == null) {
                em.persist(new Patient("P103", "Peter Jones", 68, "Male", null, LocalDate.of(2024, 7, 22)));
            }
            if (em.find(Patient.class, "P104") == null) {
                em.persist(new Patient("P104", "Mary Williams", 25, "Female", null, LocalDate.of(2024, 8, 1)));
            }

            em.getTransaction().commit();

            // --- 5. Initialize Doctor Schedules ---
            if (em.createQuery("SELECT COUNT(ds) FROM DoctorSchedule ds", Long.class).getSingleResult() == 0) {
                System.out.println("Initializing Doctor Schedules...");
                ScheduleService scheduleService = new ScheduleService();
                // Dr. Alice works at Main Clinic on Mon/Tues
                scheduleService.createSchedule(new DoctorSchedule(doctorAlice, mainClinic, DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(17, 0)));
                scheduleService.createSchedule(new DoctorSchedule(doctorAlice, mainClinic, DayOfWeek.TUESDAY, LocalTime.of(9, 0), LocalTime.of(17, 0)));
                // And at Downtown Hospital for a half-day on Wednesday
                scheduleService.createSchedule(new DoctorSchedule(doctorAlice, downtownHospital, DayOfWeek.WEDNESDAY, LocalTime.of(9, 0), LocalTime.of(12, 0)));
                
                // Dr. Bob works at Downtown Hospital full-time Mon-Fri
                scheduleService.createSchedule(new DoctorSchedule(doctorBob, downtownHospital, DayOfWeek.MONDAY, LocalTime.of(8, 30), LocalTime.of(16, 30)));
                scheduleService.createSchedule(new DoctorSchedule(doctorBob, downtownHospital, DayOfWeek.TUESDAY, LocalTime.of(8, 30), LocalTime.of(16, 30)));
                scheduleService.createSchedule(new DoctorSchedule(doctorBob, downtownHospital, DayOfWeek.WEDNESDAY, LocalTime.of(13, 0), LocalTime.of(18, 0)));
                scheduleService.createSchedule(new DoctorSchedule(doctorBob, downtownHospital, DayOfWeek.THURSDAY, LocalTime.of(8, 30), LocalTime.of(16, 30)));
                scheduleService.createSchedule(new DoctorSchedule(doctorBob, downtownHospital, DayOfWeek.FRIDAY, LocalTime.of(8, 30), LocalTime.of(16, 30)));
            }

        } catch (Exception e) {
            if (em.getTransaction() != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}