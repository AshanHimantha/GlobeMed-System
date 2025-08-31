
package system.patterns.mediator;

import java.time.LocalDateTime;
import system.enums.AppointmentType;
import system.model.Facility;
import system.model.MedicalService;
import system.model.Patient;
import system.model.User;


public interface AppointmentMediator {
    // This signature must include ALL the data needed to create an appointment.
    boolean bookAppointment(Patient patient, User doctor, AppointmentType type,
                            LocalDateTime dateTime, MedicalService service, Facility facility);
}