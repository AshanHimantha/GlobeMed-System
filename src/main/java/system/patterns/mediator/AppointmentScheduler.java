package system.patterns.mediator;

import java.time.LocalDateTime;
import system.enums.AppointmentType;
import system.model.*;
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
    public boolean bookAppointment(Patient patient, User doctor, AppointmentType type,
                                   LocalDateTime dateTime, MedicalService service, Facility facility) {
        // 1. Get Contextual Information (The user performing the action)
        User scheduledBy = authenticationService.getLoggedInUser();
        if (scheduledBy == null) {
            System.err.println("MEDIATOR: Action failed. No user is logged in.");
            JOptionPane.showMessageDialog(null, "Your session may have expired. Please log in again.", "Authentication Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // 2. Perform Business Logic Checks (The Core of the Mediator's Job)
        // Check for a doctor only if one is required for the appointment type.
        if (type == AppointmentType.CONSULTATION || type == AppointmentType.SURGERY) {
            if (doctor == null) {
                System.err.println("MEDIATOR: Validation failed. A doctor is required for this appointment type.");
                JOptionPane.showMessageDialog(null, "A doctor must be selected for a " + type.toString().toLowerCase() + ".", "Input Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            System.out.println("MEDIATOR: Coordinating multi-location checks for Dr. " + doctor.getLastName() + " at " + facility.getName() + "...");

            // --- MULTI-LOCATION CHECK 1: Is the doctor scheduled to work at this specific facility and time? ---
            if (!scheduleService.isDoctorWorkingAtFacility(doctor, dateTime, facility)) {
                System.err.println("MEDIATOR: Conflict - Doctor is not on their work schedule at this facility.");
                JOptionPane.showMessageDialog(null,
                        "Booking failed: The selected doctor is not scheduled to work at " + facility.getName() + " at the chosen date and time.",
                        "Scheduling Conflict",
                        JOptionPane.WARNING_MESSAGE);
                return false; // Stop the process
            }
            System.out.println("  -> Check 1 PASSED: Doctor is on schedule at the facility.");

            // --- MULTI-LOCATION CHECK 2: Does the doctor have an overlapping appointment at ANY facility? ---
            if (!appointmentService.isDoctorAvailable(doctor, dateTime)) {
                System.err.println("MEDIATOR: Conflict - Doctor has an overlapping appointment.");
                JOptionPane.showMessageDialog(null,
                        "Booking failed: The doctor already has a conflicting appointment (possibly at another facility) within 10 minutes of this time.",
                        "Scheduling Conflict",
                        JOptionPane.WARNING_MESSAGE);
                return false; // Stop the process
            }
            System.out.println("  -> Check 2 PASSED: No overlapping appointments found at any location.");
        }

        // 3. Dynamic Price and Service Name Calculation
        double price;
        String serviceName;

        switch (type) {
            case CONSULTATION:
                if (doctor.getConsultationFee() == null) {
                    System.err.println("MEDIATOR: Cannot book consultation. Doctor has no fee set.");
                    return false;
                }
                price = doctor.getConsultationFee();
                serviceName = "Consultation with Dr. " + doctor.getLastName();
                break;
            case DIAGNOSTIC:
                if (service == null) {
                    System.err.println("MEDIATOR: Cannot book Diagnostic without a specific service.");
                    return false;
                }
                serviceName = service.getName();
                price = service.getBasePrice();
                break;
            case SURGERY:
                serviceName = "Surgical Procedure with Dr. " + doctor.getLastName();
                price = 0.00; // Final price determined after completion
                break;
            default: // Should not happen
                return false;
        }

        System.out.println("MEDIATOR: All checks passed. Price determined as $" + String.format("%.2f", price));

        // 4. Delegate Final Action to the Service
        Appointment createdAppointment = appointmentService.createAppointment(
                patient,
                doctor,
                scheduledBy,
                type,
                serviceName,
                price,
                dateTime,
                service,
                facility // Pass the facility to be saved with the appointment
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