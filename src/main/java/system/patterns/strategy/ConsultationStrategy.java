/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package system.patterns.strategy;


import system.model.Appointment;
import system.service.AppointmentService;

/**
 *
 * @author User
 */


public class ConsultationStrategy implements SchedulingStrategy {

    private final AppointmentService appointmentService = new AppointmentService();

    /**
     * Checks if the resources for a standard consultation are available.
     * For a consultation, this is only the doctor's time slot.
     *
     * @param proposedAppointment An Appointment object containing the proposed doctor and time.
     * @return true if the doctor is available, false otherwise.
     */
    @Override
    public boolean checkAvailability(Appointment proposedAppointment) {
        System.out.println("STRATEGY (Consultation): Checking doctor's schedule...");

        boolean isAvailable = appointmentService.isDoctorAvailable(
            proposedAppointment.getDoctor(),
            proposedAppointment.getAppointmentDateTime()
        );

        if (isAvailable) {
            System.out.println("  -> Doctor is available.");
        } else {
            System.out.println("  -> CONFLICT: Doctor is not available at the proposed time.");
        }

        return isAvailable;
    }
}
