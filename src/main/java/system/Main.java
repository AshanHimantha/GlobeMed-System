package system;

import com.formdev.flatlaf.themes.FlatMacLightLaf;
import jakarta.persistence.EntityManager;
import system.ui.LoginUi;


import javax.swing.*;
import system.model.Patient;
import system.model.User;
import system.service.PersistenceManager;


public class Main {

    public static void main(String[] args) {
        // 1. Apply the FlatLaf theme FIRST
        try {
            UIManager.setLookAndFeel(new FlatMacLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Failed to initialize FlatLaf look and feel.");
        }

        // 3. Launch the Swing GUI on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            LoginUi loginUi = new LoginUi();
            loginUi.setVisible(true);
            
            
            
            //initializeDatabase();
            
//MainFrame mainf = new MainFrame();
//mainf.setVisible(true);
        });
    }

//    private static void initializeDatabase() {
//        EntityManager em = PersistenceManager.getInstance().getEntityManager();
//        em.getTransaction().begin();
//
//        if (em.find(User.class, "doctor") == null) {
//            // Use the updated constructor with first and last names
//            User doctor = new User(
//                    "doctor",
//                    AuthenticationService.hashPassword("pass123"),
//                    UserRole.DOCTOR,
//                    "Alice",
//                    "Wonderland"
//            );
//            em.persist(doctor);
//        }
//        if (em.find(User.class, "nurse") == null) {
//            User nurse = new User(
//                    "nurse",
//                    AuthenticationService.hashPassword("nurse@work"),
//                    UserRole.NURSE,
//                    "Bob",
//                    "Builder"
//            );
//            em.persist(nurse);
//        }
//        if (em.find(User.class, "admin") == null) {
//            User admin = new User(
//                    "admin",
//                    AuthenticationService.hashPassword("adminP@ss!"),
//                    UserRole.ADMIN,
//                    "Charles",
//                    "test"
//            );
//            em.persist(admin);
//        }
//
//        em.getTransaction().commit();
//        em.close();
//    }
    
    
    
    
    
    
//    private static void initializeDatabase() {
//        EntityManager em = PersistenceManager.getInstance().getEntityManager();
//        
//        try {
//            em.getTransaction().begin();
//
//            if (em.find(Patient.class, "P101") == null) {
//                Patient patient1 = new Patient("P101", "John Smith", "Diagnosed with hypertension in 2020. Allergic to penicillin.");
//                em.persist(patient1);
//            }
//            if (em.find(Patient.class, "P102") == null) {
//                Patient patient2 = new Patient("P102", "Jane Doe", "History of asthma. No known allergies.");
//                em.persist(patient2);
//            }
//            if (em.find(Patient.class, "P103") == null) {
//                Patient patient3 = new Patient("P103", "Peter Jones", "Requires annual check-ups for diabetes type 2.");
//                em.persist(patient3);
//            }
//
//            em.getTransaction().commit();
//        } catch (Exception e) {
//            // If something goes wrong, roll back the transaction
//            if (em.getTransaction().isActive()) {
//                em.getTransaction().rollback();
//            }
//            e.printStackTrace();
//        } finally {
//            em.close();
//        }
//    }
//    
    
    
}