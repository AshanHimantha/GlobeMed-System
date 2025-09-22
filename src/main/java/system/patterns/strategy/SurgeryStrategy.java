
package system.patterns.strategy;

import java.time.LocalDateTime;
import system.model.Appointment;
import system.service.AppointmentService;

/**
 *
 * @author User
 */
public class SurgeryStrategy implements SchedulingStrategy {

    private final AppointmentService appointmentService = new AppointmentService();


    @Override
    public boolean checkAvailability(Appointment proposedAppointment) {
        System.out.println("STRATEGY (Surgery): Performing complex availability checks...");

    
        boolean isDoctorAvailable = appointmentService.isDoctorAvailable(
            proposedAppointment.getDoctor(),
            proposedAppointment.getAppointmentDateTime()
        );
        System.out.println("  -> Checking Surgeon availability... " + (isDoctorAvailable ? "OK" : "CONFLICT"));

     
        boolean isOperatingRoomAvailable = checkOperatingRoomAvailability(
            proposedAppointment.getAppointmentDateTime()
        );
        System.out.println("  -> Checking Operating Room availability... " + (isOperatingRoomAvailable ? "OK" : "CONFLICT"));

   
        return isDoctorAvailable && isOperatingRoomAvailable;
    }


    private boolean checkOperatingRoomAvailability(LocalDateTime dateTime) {
       
        int hour = dateTime.getHour();
        if (hour == 12) {
            System.out.println("    (Simulated Check: Operating Room is unavailable during lunch hour maintenance.)");
            return false;
        }
        
        System.out.println("    (Simulated Check: Operating Room is available.)");
        return true;
    }
}