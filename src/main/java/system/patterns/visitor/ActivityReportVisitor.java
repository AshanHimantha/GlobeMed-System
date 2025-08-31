package system.patterns.visitor;

import system.model.Appointment;
import system.model.Claim;

/**
 *
 * @author User
 */
public class ActivityReportVisitor implements ReportVisitor {

    private int scheduledAppointments = 0;
    private int completedAppointments = 0;
    private int pendingClaims = 0;
    private int closedClaims = 0;

    @Override
    public void visit(Appointment appointment) {
        // This visitor cares about the status of the appointment
        switch (appointment.getStatus().toUpperCase()) {
            case "SCHEDULED":
                scheduledAppointments++;
                break;
            case "COMPLETED":
                completedAppointments++;
                break;
        }
    }

    @Override
    public void visit(Claim claim) {
        // This visitor cares about the status of the claim
        if ("CLOSED".equals(claim.getStatus().toString())) {
            closedClaims++;
        } else {
            // Any other status is considered "pending" for this simple report
            pendingClaims++;
        }
    }

    @Override
    public String getReport() {
        StringBuilder report = new StringBuilder();
        report.append("--- System Activity Report ---\n\n");
        report.append("Appointment Status:\n");
        report.append(String.format("- Scheduled Appointments: %d\n", scheduledAppointments));
        report.append(String.format("- Completed Appointments: %d\n\n", completedAppointments));
        report.append("Claim Status:\n");
        report.append(String.format("- Pending Claims: %d\n", pendingClaims));
        report.append(String.format("- Closed Claims: %d\n", closedClaims));

        return report.toString();
    }

    // Getter methods for individual metrics to support card-based display
    public int getScheduledAppointments() {
        return scheduledAppointments;
    }

    public int getCompletedAppointments() {
        return completedAppointments;
    }

    public int getPendingClaims() {
        return pendingClaims;
    }

    public int getClosedClaims() {
        return closedClaims;
    }

    public int getTotalAppointments() {
        return scheduledAppointments + completedAppointments;
    }

    public int getTotalClaims() {
        return pendingClaims + closedClaims;
    }
}