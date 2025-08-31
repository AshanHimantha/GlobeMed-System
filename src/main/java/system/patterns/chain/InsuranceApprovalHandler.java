/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package system.patterns.chain;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import system.enums.ClaimStatus;
import system.model.Claim;

/**
 *
 * @author User
 */
public class InsuranceApprovalHandler extends ClaimHandler {

    public InsuranceApprovalHandler() {
        super();
    }

    /**
     * Processes the claim if it's in the PENDING_INSURANCE_APPROVAL state.
     * This method is designed to be called manually from a UI (like an InsuranceApprovalPanel).
     * @param claim The claim object to be processed.
     */
    @Override
    public void processClaim(Claim claim) {
        log("Insurance Approval Handler is processing Claim #" + claim.getId());

        // This handler should only act on claims waiting for this specific step.
        if (claim.getStatus() != ClaimStatus.PENDING_INSURANCE_APPROVAL) {
            log("Claim is not pending insurance approval. Skipping.");
            passToNext(claim);
            return;
        }

        // --- Present the UI options to the "insurance agent" ---
        String[] options = {"Approve Full Amount", "Approve Partial Amount", "Deny Claim", "Cancel"};
        String message = String.format(
            "Reviewing Insurance Claim #%d for %s\nTotal Amount: Rs.%.2f\n\nPlease select an action:",
            claim.getId(),
            claim.getAppointment().getPatient().getName(),
            claim.getTotalAmount()
        );
        
        int choice = JOptionPane.showOptionDialog(null, message, "Insurance Approval",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        switch (choice) {
            case 0: // Approve Full Amount
                approveFull(claim);
                break;
            case 1: // Approve Partial Amount
                approvePartial(claim);
                break;
            case 2: // Deny Claim
                denyClaim(claim);
                break;
            default: // User clicked Cancel or closed the dialog
                log("Insurance approval was cancelled by the user.");
                // The chain stops here. The claim remains in PENDING_INSURANCE_APPROVAL.
                return; // IMPORTANT: Do not pass to next handler if cancelled.
        }
        
        // After processing, pass the claim to the next handler in the chain (PatientBillingHandler).
        passToNext(claim);
    }

    private void approveFull(Claim claim) {
        log("Insurance approved FULL amount of Rs." + String.format("%.2f", claim.getTotalAmount()));
        claim.setPaidByInsurance(claim.getTotalAmount());
        claim.setStatus(ClaimStatus.CLOSED); // No patient balance, so the claim is now closed.
        claimService.updateClaim(claim);
    }

    private void approvePartial(Claim claim) {
        log("Processing PARTIAL insurance approval.");
        JTextField amountField = new JTextField();
        Object[] message = {"Enter the amount approved by insurance:", amountField};
        int option = JOptionPane.showConfirmDialog(null, message, "Enter Partial Amount", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                double approvedAmount = Double.parseDouble(amountField.getText());
                if (approvedAmount < 0 || approvedAmount > claim.getTotalAmount()) {
                    JOptionPane.showMessageDialog(null, "Invalid amount. Please enter a value between 0 and " + claim.getTotalAmount(), "Error", JOptionPane.ERROR_MESSAGE);
                    return; // Stop processing
                }
                claim.setPaidByInsurance(approvedAmount);
                claim.setStatus(ClaimStatus.PENDING_PATIENT_BILLING); // There is a balance due from the patient.
                claimService.updateClaim(claim);
                log("Insurance approved PARTIAL amount: Rs." + approvedAmount);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid number format.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            log("Partial approval was cancelled by the user.");
        }
    }

    private void denyClaim(Claim claim) {
        log("Insurance DENIED claim.");
        claim.setPaidByInsurance(0.0);
        claim.setStatus(ClaimStatus.PENDING_PATIENT_BILLING); // The full amount is now due from the patient.
        claimService.updateClaim(claim);
    }
}