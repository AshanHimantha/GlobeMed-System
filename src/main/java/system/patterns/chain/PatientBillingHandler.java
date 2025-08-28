
package system.patterns.chain;

import javax.swing.JOptionPane;
import system.enums.ClaimStatus;
import system.enums.PaymentMethod;
import system.model.Claim;

/**
 *
 * @author User
 */
public class PatientBillingHandler extends ClaimHandler {

    public PatientBillingHandler() {
        super();
    }

    /**
     * This method signature now correctly overrides the one in ClaimHandler.
     * @param claim The claim to be processed.
     */
    @Override
    public void processClaim(Claim claim) {
        if (claim.getStatus() != ClaimStatus.PENDING_PATIENT_BILLING) {
            passToNext(claim);
            return;
        }

        double patientDueAmount = claim.getPatientDueAmount();
        if (patientDueAmount <= 0.01) {
            log("No balance due from patient. Closing claim.");
            claim.setStatus(ClaimStatus.CLOSED);
            claimService.updateClaim(claim);
            passToNext(claim);
            return;
        }

        String[] options = {"Pay with CARD", "Pay with CASH", "Cancel"};
        String message = String.format("Final Bill for Claim #%d...\nAmount Due: $%.2f",
            claim.getId(), patientDueAmount);
        
        int choice = JOptionPane.showOptionDialog(null, message, "Patient Final Billing",
            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        switch (choice) {
            case 0: // Pay with CARD
                log("Patient chose to pay with CARD. Passing to CardPaymentHandler.");
                claim.setPaymentMethod(PaymentMethod.CARD);
                claim.setStatus(ClaimStatus.CLOSED);
                passToNext(claim);
                break;
            case 1: // Pay with CASH
                log("Patient paid with CASH. Closing claim.");
                claim.setPaidByPatient(claim.getPaidByPatient() + patientDueAmount);
                claim.setStatus(ClaimStatus.CLOSED);
                claimService.updateClaim(claim);
                // Pass it on, in case there are any final logging handlers after this.
                passToNext(claim);
                break;
            default: // User cancelled
                log("Patient billing was cancelled by user.");
                // The chain stops here.
                return;
        }
    }
    /**
     * A mock method to simulate processing a credit card transaction.
     */
    private boolean processCardTransaction(double amount) {
        System.out.println("   (Contacting external payment gateway for Rs." + String.format("%.2f", amount) + "...)");
        // In a real app, this would return the result of the API call.
        return true; // Assume success for the demo.
    }
}