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
          
            JOptionPane.showMessageDialog(null, "Your session may have expired. Please log in again.", "Authentication Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }


        if (type == AppointmentType.CONSULTATION || type == AppointmentType.SURGERY) {
            if (doctor == null) {
              
                JOptionPane.showMessageDialog(null, "A doctor must be selected for a " + type.toString().toLowerCase() + ".", "Input Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }

          

            if (!scheduleService.isDoctorWorkingAtFacility(doctor, dateTime, facility)) {
         
                JOptionPane.showMessageDialog(null,
                        "Booking failed: The selected doctor is not scheduled to work at " + facility.getName() + " at the chosen date and time.",
                        "Scheduling Conflict",
                        JOptionPane.WARNING_MESSAGE);
                return false; // Stop the process
            }
            System.out.println("  -> Check 1 PASSED: Doctor is on schedule at the facility.");

      
            if (!appointmentService.isDoctorAvailable(doctor, dateTime)) {

                JOptionPane.showMessageDialog(null,
                        "Booking failed: The doctor already has a conflicting appointment (possibly at another facility) within 10 minutes of this time.",
                        "Scheduling Conflict",
                        JOptionPane.WARNING_MESSAGE);
                return false; // Stop the process
            }
          
        }

        // 3. Dynamic Price and Service Name Calculation
        double price;
        String serviceName;

        switch (type) {
            case CONSULTATION:
                if (doctor.getConsultationFee() == null) {
              
                    return false;
                }
                price = doctor.getConsultationFee();
                serviceName = "Consultation with Dr. " + doctor.getLastName();
                break;
            case DIAGNOSTIC:
                if (service == null) {
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

        System.out.println("MEDIATOR: All checks passed. Price determined as Rs." + String.format("%.2f", price));

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