
package system.patterns.chain;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import system.enums.ClaimStatus;
import system.enums.PaymentMethod;
import system.model.Claim;

/**
 *
 * @author User
 */
public class CardPaymentHandler extends ClaimHandler {

    public CardPaymentHandler() {
        super();
    }

    /**
     * Processes the claim if the payment method is CARD. Otherwise, it passes
     * the claim to the next handler in the chain.
     * @param claim The claim object to be processed.
     */
    @Override
    public void processClaim(Claim claim) {
        // Step 1: Check if this handler is responsible for this claim.
        if (claim.getPaymentMethod() != PaymentMethod.CARD) {
            log("Skipping card payment processing (Method is not CARD).");
            passToNext(claim);
            return;
        }
        
        double amountToCharge = claim.getPatientDueAmount();
        if (amountToCharge <= 0) {
             log("No patient balance due. Skipping card payment processing.");
             passToNext(claim);
             return;
        }

        log("Attempting to process CARD payment for $" + String.format("%.2f", amountToCharge));

        // Step 2: Simulate the core business logic (calling a payment gateway).
        boolean paymentSuccess = processCardTransaction(amountToCharge);
        
        // Step 3: Update the claim's state based on the outcome.
        if (paymentSuccess) {
            log("Card transaction successful.");
            claim.setPaidByPatient(claim.getPaidByPatient() + amountToCharge);
            
            // If the balance is now zero, the claim is fully resolved.
            if (claim.getPatientDueAmount() <= 0.01) { // Use tolerance for floating-point math
                claim.setStatus(ClaimStatus.CLOSED);
                log("Claim is now fully paid and has been CLOSED.");
            }
            claimService.updateClaim(claim);
        } else {
            log("Card transaction FAILED.");
            claim.setStatus(ClaimStatus.PATIENT_PAYMENT_FAILED);
            claimService.updateClaim(claim);
            // In a real app, a notification might be sent to the billing department here.
        }

        // Step 4: Always pass to the next handler.
        passToNext(claim);
    }

    /**
     * A mock method to simulate contacting an external payment gateway API.
     * @param amount The amount to charge.
     * @return true if the transaction is approved, false if it is declined.
     */
    private boolean processCardTransaction(double amount) {
        log("   (Simulating call to external payment gateway for $" + String.format("%.2f", amount) + "...)");
        
        // Let's simulate a failure for amounts ending in ".99" to test the workflow.
        if (String.format("%.2f", amount).endsWith(".99")) {
            log("   (Gateway Response: DECLINED)");
            return false;
        }
        
        log("   (Gateway Response: APPROVED)");
        return true;
    }
}