
package system.patterns.visitor;

import java.text.NumberFormat;
import system.enums.PaymentMethod;
import system.model.Appointment;
import system.model.Claim;

/**
 *
 * @author User
 */
public class FinancialReportVisitor implements ReportVisitor{
    
    private double totalRevenueFromAppointments = 0;
    private double totalRevenueFromClaims = 0;
    private int visitedAppointments = 0;
    private int visitedClaims = 0;

    /**
     * This method is called when the visitor "visits" an Appointment object.
     * It extracts the price from the appointment.
     */
    @Override
    public void visit(Appointment appointment) {
        // We only count revenue from appointments that were paid directly (CASH/CARD)
        // because insurance-based ones will be counted in the Claim.
        if (appointment.getPaymentMethod() != null && appointment.getPaymentMethod() != PaymentMethod.INSURANCE) {
            totalRevenueFromAppointments += appointment.getPrice();
        }
        visitedAppointments++;
    }

    /**
     * This method is called when the visitor "visits" a Claim object.
     * It extracts the total amount from the claim.
     */
    @Override
    public void visit(Claim claim) {
        // We count the total billed amount of a closed claim as revenue.
        if ("CLOSED".equals(claim.getStatus().toString())) {
             totalRevenueFromClaims += claim.getTotalAmount();
        }
        visitedClaims++;
    }

    /**
     * Generates the final, formatted report string after all elements have been visited.
     */
    @Override
    public String getReport() {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        double totalRevenue = totalRevenueFromAppointments + totalRevenueFromClaims;

        StringBuilder report = new StringBuilder();
        report.append("--- Financial Report ---\n\n");
        report.append("Data Elements Scanned:\n");
        report.append(String.format("- Visited %d Appointments\n", visitedAppointments));
        report.append(String.format("- Visited %d Claims\n\n", visitedClaims));
        report.append("Revenue Summary:\n");
        report.append(String.format("- Revenue from Direct Payments: %s\n", currencyFormat.format(totalRevenueFromAppointments)));
        report.append(String.format("- Revenue from Closed Claims: %s\n", currencyFormat.format(totalRevenueFromClaims)));
        report.append("------------------------------------\n");
        report.append(String.format("TOTAL ESTIMATED REVENUE: %s\n", currencyFormat.format(totalRevenue)));
        
        return report.toString();
    }
}
