package system.patterns.mediator;

import java.time.LocalDateTime;
import system.enums.AppointmentType;
import system.model.Appointment;
import system.model.MedicalService;
import system.model.Patient;
import system.model.User;
import system.service.AppointmentService;
import system.service.AuthenticationService;

/**
 *
 * @author User
 */
public class AppointmentScheduler implements AppointmentMediator {

    private final AppointmentService appointmentService;
    private final AuthenticationService authenticationService;

    public AppointmentScheduler() {
        this.appointmentService = new AppointmentService();
        this.authenticationService = AuthenticationService.getInstance();
    }

    @Override
    public boolean bookAppointment(Patient patient, User doctor, AppointmentType type, LocalDateTime dateTime, MedicalService service) {

        User scheduledBy = authenticationService.getLoggedInUser();
        if (scheduledBy == null) { /* handle error */ return false; }
        if (doctor != null && !appointmentService.isDoctorAvailable(doctor, dateTime)) { /* handle error */ return false; }

        double price = 0.0;
        String serviceName = "";

        switch (type) {
            case CONSULTATION:
                if (doctor == null || doctor.getConsultationFee() == null) { return false; }
                price = doctor.getConsultationFee();
                serviceName = "Consultation with Dr. " + doctor.getLastName();
                break;

            case DIAGNOSTIC:
                // --- THIS IS THE FIX ---
                if (service == null) {
                    System.err.println("MEDIATOR: Cannot book Diagnostic without a specific service.");
                    return false;
                }
                // Get the name and price from the service object passed from the UI
                serviceName = service.getName();
                price = service.getBasePrice();
                break;
                // --- END OF FIX ---

            case SURGERY:
                if (doctor == null) { return false; }
                serviceName = "Surgical Procedure with Dr. " + doctor.getLastName();
                price = 0.00; // Price determined later
                break;
        }

        System.out.println("MEDIATOR: Price determined as $" + price);

        Appointment createdAppointment = appointmentService.createAppointment(
            patient, doctor, scheduledBy, type, serviceName, price, dateTime
        );

        return createdAppointment != null;
    }
}