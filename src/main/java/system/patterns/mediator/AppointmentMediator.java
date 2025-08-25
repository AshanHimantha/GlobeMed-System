
package system.patterns.mediator;

import java.time.LocalDateTime;
import system.model.Patient;
import system.model.User;


public interface AppointmentMediator {
    boolean bookAppointment(Patient patient, User doctor, LocalDateTime dateTime, String description);
}
