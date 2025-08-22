package system;

import com.formdev.flatlaf.themes.FlatMacLightLaf;
import system.ui.LoginUi;


import javax.swing.*;


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
}