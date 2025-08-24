
package system.patterns.mediator;

import java.time.LocalDateTime;
import system.model.Appointment;
import system.model.Patient;
import system.model.User;
import system.service.AppointmentService;
import system.service.AuthenticationService;

/**
 *
 * @author User
 */
public class AppointmentScheduler implements AppointmentMediator{
    private final AppointmentService appointmentService;
    private final AuthenticationService authenticationService;

    public AppointmentScheduler() {
        this.appointmentService = new AppointmentService();
        this.authenticationService = AuthenticationService.getInstance(); // Get the singleton
    }

    @Override
    public boolean bookAppointment(Patient patient, User doctor, LocalDateTime dateTime, String description) {
        // --- This is the core coordination logic ---
        
        // 1. Get the user who is currently logged in (the one scheduling the appointment)
        User scheduledBy = authenticationService.getLoggedInUser();
        if (scheduledBy == null) {
            System.err.println("MEDIATOR LOG: No user logged in. Cannot schedule appointment.");
            return false;
        }

        // 2. Check for business rule: Doctor availability
        if (!appointmentService.isDoctorAvailable(doctor, dateTime)) {
            System.err.println("MEDIATOR LOG: Conflict detected. Dr. " + doctor.getLastName() + " is not available at this time.");
            return false;
        }
        
        // 3. (Future) Other checks could go here:
        //    - Check if the clinic is open at this time.
        //    - Check if the patient has any outstanding bills.

        // 4. If all checks pass, proceed to create the appointment.
        System.out.println("MEDIATOR LOG: All checks passed. Creating appointment...");
        Appointment newAppointment = appointmentService.createAppointment(patient, doctor, dateTime, description, scheduledBy);
        
        if (newAppointment != null) {
            // 5. (Future) Notify relevant parties (e.g., send an email notification).
            //    - NotificationService.sendConfirmation(patient, newAppointment);
            //    - NotificationService.sendNewBookingAlert(doctor, newAppointment);
            System.out.println("MEDIATOR LOG: Appointment created successfully (ID: " + newAppointment.getId() + ")");
            return true;
        } else {
            System.err.println("MEDIATOR LOG: Failed to save the appointment to the database.");
            return false;
        }
    }
}
