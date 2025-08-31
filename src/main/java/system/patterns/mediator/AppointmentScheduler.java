package system.patterns.mediator;

import java.time.LocalDateTime;
import system.enums.AppointmentType;
import system.model.Appointment;
import system.model.MedicalService;
import system.model.Patient;
import system.model.User;
import system.service.AppointmentService;
import system.service.AuthenticationService;
import system.service.ScheduleService;

import javax.swing.*;

/**
 *
 * @author User
 */
public class AppointmentScheduler implements AppointmentMediator {

    private final AppointmentService appointmentService;
    private final AuthenticationService authenticationService;
    private final ScheduleService scheduleService;

    public AppointmentScheduler() {
        this.appointmentService = new AppointmentService();
        this.authenticationService = AuthenticationService.getInstance();
        this.scheduleService = new ScheduleService();
    }


    @Override
    public boolean bookAppointment(Patient patient, User doctor, AppointmentType type, LocalDateTime dateTime, MedicalService service) {

        // 1. Get Contextual Information (The user performing the action)
        User scheduledBy = authenticationService.getLoggedInUser();
        if (scheduledBy == null) {
            System.err.println("MEDIATOR: Action failed. No user is logged in.");
            JOptionPane.showMessageDialog(null, "Your session may have expired. Please log in again.", "Authentication Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // 2. Perform Business Logic Checks (The Core of the Mediator's Job)
        // Check doctor's working hours and for overlapping appointments.
        if (doctor != null) {
            System.out.println("MEDIATOR: Coordinating checks for Dr. " + doctor.getLastName() + "...");

            // CHECK 1: Is the doctor scheduled to be working at this time?
            if (!scheduleService.isDoctorWorking(doctor, dateTime)) {
                System.err.println("MEDIATOR: Conflict - Doctor is not on their work schedule.");
                JOptionPane.showMessageDialog(null,
                        "Booking failed: The selected doctor is not scheduled to work at the chosen date and time.",
                        "Scheduling Conflict", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            System.out.println("  -> Check 1 PASSED: Doctor is on schedule.");

            // CHECK 2: Does the doctor have another overlapping appointment?
            if (!appointmentService.isDoctorAvailable(doctor, dateTime)) {
                System.err.println("MEDIATOR: Conflict - Doctor has an overlapping appointment.");
                JOptionPane.showMessageDialog(null,
                        "Booking failed: The doctor already has a conflicting appointment within 10 minutes of this time.",
                        "Scheduling Conflict", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            System.out.println("  -> Check 2 PASSED: No overlapping appointments found.");
        }

        // 3. Dynamic Price and Service Name Calculation
        double price = 0.0;
        String serviceName = "";

        switch (type) {
            case CONSULTATION:
                if (doctor == null || doctor.getConsultationFee() == null) {
                    System.err.println("MEDIATOR: Cannot book consultation. Doctor is invalid or has no fee set.");
                    return false;
                }
                price = doctor.getConsultationFee();
                serviceName = "Consultation with Dr. " + doctor.getLastName();
                break;

            case DIAGNOSTIC:
                if (service == null) {
                    System.err.println("MEDIATOR: Cannot book Diagnostic without a specific service from the catalog.");
                    return false;
                }
                serviceName = service.getName();
                price = service.getBasePrice();
                break;

            case SURGERY:
                if (doctor == null) {
                    System.err.println("MEDIATOR: Cannot book Surgery without a surgeon.");
                    return false;
                }
                serviceName = "Surgical Procedure with Dr. " + doctor.getLastName();
                price = 0.00; // Final price is determined after completion.
                break;
        }

        System.out.println("MEDIATOR: All checks passed. Price determined as Rs." + String.format("%.2f", price));

        // 4. Delegate Final Action to the Service with all required parameters.
        Appointment createdAppointment = appointmentService.createAppointment(
                patient,
                doctor,
                scheduledBy,
                type,
                serviceName,
                price,
                dateTime,
                service // This is correctly null for non-diagnostic types
        );

        // 5. Handle the Result
        if (createdAppointment != null) {
            System.out.println("MEDIATOR: Appointment successfully created with ID: " + createdAppointment.getId());
            return true;
        } else {
            System.err.println("MEDIATOR: An error occurred while saving the appointment to the database.");
            return false;
        }
    }
}