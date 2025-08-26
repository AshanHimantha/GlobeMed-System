
package system.patterns.strategy;


import system.model.Appointment;

/**
 *
 * @author User
 */
public interface SchedulingStrategy {
    /**
     * A single method to handle the entire scheduling process for this strategy.
     * It should perform availability checks and then create the appointment if possible.
     * @return true if successful, false otherwise.
     */
    boolean checkAvailability(Appointment proposedAppointment);
}