
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

    /**
     * Checks if all necessary resources for a surgery are available.
     *
     * @param proposedAppointment An Appointment object containing the proposed surgeon and time.
     * @return true if both the surgeon and the operating room are available, false otherwise.
     */
    @Override
    public boolean checkAvailability(Appointment proposedAppointment) {
        System.out.println("STRATEGY (Surgery): Performing complex availability checks...");

        // --- Step 1: Check standard resources (the surgeon's time) ---
        boolean isDoctorAvailable = appointmentService.isDoctorAvailable(
            proposedAppointment.getDoctor(),
            proposedAppointment.getAppointmentDateTime()
        );
        System.out.println("  -> Checking Surgeon availability... " + (isDoctorAvailable ? "OK" : "CONFLICT"));

        // --- Step 2: Check specialized resources required for this strategy ---
        boolean isOperatingRoomAvailable = checkOperatingRoomAvailability(
            proposedAppointment.getAppointmentDateTime()
        );
        System.out.println("  -> Checking Operating Room availability... " + (isOperatingRoomAvailable ? "OK" : "CONFLICT"));

        // --- Step 3: The strategy succeeds only if ALL checks pass ---
        return isDoctorAvailable && isOperatingRoomAvailable;
    }

    /**
     * A mock method to simulate checking an external system or a separate resource schedule.
     * In a real system, this might be a complex query or a call to another service.
     *
     * @param dateTime The time to check for Operating Room availability.
     * @return true if the room is considered available, false otherwise.
     */
    private boolean checkOperatingRoomAvailability(LocalDateTime dateTime) {
        // Simple mock logic for demonstration:
        // Let's pretend the operating rooms are always booked for maintenance between 12:00 PM and 1:00 PM.
        int hour = dateTime.getHour();
        if (hour == 12) {
            System.out.println("    (Simulated Check: Operating Room is unavailable during lunch hour maintenance.)");
            return false;
        }
        
        System.out.println("    (Simulated Check: Operating Room is available.)");
        return true;
    }
}